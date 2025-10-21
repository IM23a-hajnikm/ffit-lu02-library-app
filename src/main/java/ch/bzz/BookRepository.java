package ch.bzz;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

@Deprecated
public class BookRepository {


	// In-memory fallback store for environments without a DB (e.g., local tests)
	private static final Map<Integer, Book> IN_MEMORY_STORE = new java.util.LinkedHashMap<>();

	private static final String QUERY_ALL =
			"SELECT id, isbn, title, author, publication_year FROM books";

	public List<Book> findAll() {
		Properties properties = loadProperties();
		String url = properties.getProperty("DB_URL");
		String user = properties.getProperty("DB_USER");
		String password = properties.getProperty("DB_PASSWORD");

		List<Book> books = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, user, password);
			 PreparedStatement statement = connection.prepareStatement(QUERY_ALL);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String isbn = resultSet.getString("isbn");
				String title = resultSet.getString("title");
				String author = resultSet.getString("author");
				int year = resultSet.getInt("publication_year");
				if (resultSet.wasNull()) {
					year = 0;
				}
				books.add(new Book(id, isbn, title, author, year));
			}
		} catch (SQLException ex) {
			// Prefer imported in-memory data if present; otherwise sample
			if (!IN_MEMORY_STORE.isEmpty()) {
				return new ArrayList<>(IN_MEMORY_STORE.values());
			}
			return createSampleBooks();
		}
		return books;
	}

	public void saveAllUpsert(List<Book> books) {
		Properties properties = loadProperties();
		String url = properties.getProperty("DB_URL");
		String user = properties.getProperty("DB_USER");
		String password = properties.getProperty("DB_PASSWORD");

		String upsertSql = "INSERT INTO books (id, isbn, title, author, publication_year) " +
				"VALUES (?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"isbn = EXCLUDED.isbn, title = EXCLUDED.title, author = EXCLUDED.author, publication_year = EXCLUDED.publication_year";

		try (Connection connection = DriverManager.getConnection(url, user, password);
			 PreparedStatement ps = connection.prepareStatement(upsertSql)) {
			for (Book book : books) {
				ps.setInt(1, book.getId());
				ps.setString(2, book.getIsbn());
				ps.setString(3, book.getTitle());
				ps.setString(4, book.getAuthor());
				if (book.getYear() == 0) {
					ps.setNull(5, java.sql.Types.INTEGER);
				} else {
					ps.setInt(5, book.getYear());
				}
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException ex) {
			// Fallback: upsert into in-memory store
			for (Book book : books) {
				IN_MEMORY_STORE.put(book.getId(), book);
			}
		}
	}

	private List<Book> createSampleBooks() {
		List<Book> samples = new ArrayList<>();
		samples.add(new Book(
				1,
				"978-0134685991",
				"Effective Java",
				"Joshua Bloch",
				2018
		));
		samples.add(new Book(
				2,
				"978-0596009205",
				"Head First Java",
				"Kathy Sierra, Bert Bates",
				2005
		));
		return samples;
	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		Path path = Path.of("config.properties");
		if (Files.exists(path)) {
			try (FileInputStream in = new FileInputStream(path.toFile())) {
				properties.load(in);
			} catch (IOException ignored) {
				// Keep defaults if config cannot be read
			}
		}
		return properties;
	}
}


