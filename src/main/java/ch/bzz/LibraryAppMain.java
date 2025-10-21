package ch.bzz;

import ch.bzz.db.BookPersistor;
import ch.bzz.db.UserPersistor;
import ch.bzz.io.BookImporter;
import ch.bzz.io.DelimitedFileReader;
import ch.bzz.model.Book;
import ch.bzz.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryAppMain {
private static final Logger log = LoggerFactory.getLogger(LibraryAppMain.class);
    public static void main(String[] args) {
        try (Scanner scanner = new java.util.Scanner(System.in)) {
			while (true) {
				System.out.println("Enter command:");
				if (!scanner.hasNextLine()) {
					break;
				}
				String input = scanner.nextLine().trim();
				if ("quit".equals(input)) {
					break;
				} else if ("help".equals(input)) {
					System.out.println("help quit listBooks importBooks");
				} else if (input.startsWith("listBooks")) {
					Integer limit = null;
					String[] tokens = input.split("\\s+", 2);
					if (tokens.length == 2) {
						try { limit = Integer.parseInt(tokens[1]); }
						catch (NumberFormatException nfe) { log.warn("Invalid limit provided to listBooks: '{}'", tokens[1]); }
					}
					var repo = new BookPersistor();
					List<Book> books = repo.findAll();
					int max = limit == null ? books.size() : Math.max(0, Math.min(limit, books.size()));
					for (int i = 0; i < max; i++) {
						Book book = books.get(i);
						System.out.println(book.getTitle());
					}
				} else if (input.startsWith("importBooks ")) {
					String path = input.substring("importBooks ".length()).trim();
					var repo = new BookPersistor();
					var importer = new BookImporter(new DelimitedFileReader());
					List<Book> toImport;
					try {
						System.out.println("path: " + path);
						System.out.println("absolute path: " + Path.of(path).toAbsolutePath());

						toImport = importer.importTsv(Path.of(path));
					} catch (java.io.IOException e) {
						log.error("Failed to import books from file: {}", path, e);
						continue;
					}
					repo.upsertAll(toImport);
				} else if (input.startsWith("createUser ")) {
					String[] tokens = input.split("\\s+");
					if (tokens.length < 6) {
						System.out.println("Usage: createUser <firstname> <lastname> <yyyy-mm-dd> <email> <password>");
						continue;
					}
					String firstname = tokens[1];
					String lastname = tokens[2];
					LocalDate dateOfBirth;
					try {
						dateOfBirth = LocalDate.parse(tokens[3]);
					} catch (Exception ex) {
						System.out.println("Invalid date format. Use yyyy-mm-dd");
						continue;
					}
					String email = tokens[4];
					String password = tokens[5];

					byte[] salt = generateSalt();
					String hash;
					try {
						hash = hashPassword(password, salt);
					} catch (NoSuchAlgorithmException e) {
						System.out.println("Password hashing algorithm not available");
						continue;
					}

					User user = new User(0, firstname, lastname, dateOfBirth, email, hash, salt);
					try {
						new UserPersistor().insert(user);
						System.out.println("User created: " + firstname + " " + lastname);
					} catch (RuntimeException re) {
						System.out.println("Failed to create user: " + re.getMessage());
					}
				} else {
					System.out.println("Eingabe nicht als Befehl erkannt: " + input);
				}
			}
        }
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashed = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }
}
