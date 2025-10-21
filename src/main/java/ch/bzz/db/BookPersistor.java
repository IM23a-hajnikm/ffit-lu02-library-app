package ch.bzz.db;

import ch.bzz.Database;
import ch.bzz.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookPersistor {

	// In-memory fallback when DB not available
	private static final Map<Integer, Book> IN_MEMORY = new LinkedHashMap<>();

	public List<Book> findAll() {
		String sql = "SELECT id, isbn, title, author, publication_year FROM books";
		try (Connection con = Database.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			List<Book> result = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String isbn = rs.getString("isbn");
				String title = rs.getString("title");
				String author = rs.getString("author");
				int year = rs.getInt("publication_year");
				if (rs.wasNull()) year = 0;
				result.add(new Book(id, isbn, title, author, year));
			}
			return result;
		} catch (SQLException e) {
			if (IN_MEMORY.isEmpty()) {
				return createSampleBooks();
			}
			java.util.LinkedHashMap<Integer, Book> merged = new java.util.LinkedHashMap<>();
			for (Book sample : createSampleBooks()) {
				merged.put(sample.getId(), sample);
			}
			for (java.util.Map.Entry<Integer, Book> entry : IN_MEMORY.entrySet()) {
				merged.put(entry.getKey(), entry.getValue());
			}
			return new ArrayList<>(merged.values());
		}
	}

	public void upsertAll(List<Book> books) {
		String upsert = "INSERT INTO books (id, isbn, title, author, publication_year) " +
				"VALUES (?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"isbn = EXCLUDED.isbn, title = EXCLUDED.title, author = EXCLUDED.author, publication_year = EXCLUDED.publication_year";
		try (Connection con = Database.getConnection();
			 PreparedStatement ps = con.prepareStatement(upsert)) {
			for (Book b : books) {
				ps.setInt(1, b.getId());
				ps.setString(2, b.getIsbn());
				ps.setString(3, b.getTitle());
				ps.setString(4, b.getAuthor());
				if (b.getYear() == 0) ps.setNull(5, java.sql.Types.INTEGER); else ps.setInt(5, b.getYear());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			for (Book b : books) {
				IN_MEMORY.put(b.getId(), b);
			}
		}
	}

	private List<Book> createSampleBooks() {
		List<Book> samples = new ArrayList<>();
		samples.add(new Book(1, "978-0134685991", "Effective Java", "Joshua Bloch", 2018));
		samples.add(new Book(2, "978-0596009205", "Head First Java", "Kathy Sierra, Bert Bates", 2005));
		return samples;
	}
}


