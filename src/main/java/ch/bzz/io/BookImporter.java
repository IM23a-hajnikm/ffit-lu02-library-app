package ch.bzz.io;

import ch.bzz.model.Book;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BookImporter {

	private final DelimitedFileReader reader;

	public BookImporter(DelimitedFileReader reader) {
		this.reader = reader;
	}

	public List<Book> importTsv(Path file) throws IOException {
		List<String[]> rows = reader.read(file, '\t', true);
		List<Book> result = new ArrayList<>();
		for (String[] parts : rows) {
			if (parts.length < 5) continue;
			int id;
			try { id = Integer.parseInt(parts[0].trim()); } catch (NumberFormatException e) { continue; }
			String isbn = parts[1].trim();
			String title = parts[2].trim();
			String author = parts[3].trim();
			int year = 0;
			try { year = Integer.parseInt(parts[4].trim()); } catch (NumberFormatException ignored) {}
			result.add(new Book(id, isbn, title, author, year));
		}
		return result;
	}
}



