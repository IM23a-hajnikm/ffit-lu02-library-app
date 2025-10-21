package ch.bzz.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DelimitedFileReader {

	public List<String[]> read(Path file, char delimiter, boolean hasHeader) throws IOException {
		List<String[]> rows = new ArrayList<>();
        System.out.println("absolute path: " + file.toAbsolutePath());
		try (BufferedReader reader = Files.newBufferedReader(file.toAbsolutePath(), StandardCharsets.UTF_8)) {
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) continue;
				if (first && hasHeader) { first = false; continue; }
				first = false;
				rows.add(line.split(java.util.regex.Pattern.quote(String.valueOf(delimiter)), -1));
			}
		}
		return rows;
	}
}







