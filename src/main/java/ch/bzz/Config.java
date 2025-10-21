package ch.bzz;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

	private static final Properties PROPS = new Properties();
	private static boolean loaded = false;

	private static void loadIfNeeded() {
		if (loaded) return;
		Path path = Path.of("config.properties");
		if (Files.exists(path)) {
			try (FileInputStream in = new FileInputStream(path.toFile())) {
				PROPS.load(in);
			} catch (IOException ignored) {
				// ignore; keep defaults
			}
		}
		loaded = true;
	}

	public static String get(String key) {
		loadIfNeeded();
		return PROPS.getProperty(key);
	}

	public static String getOrDefault(String key, String defaultValue) {
		loadIfNeeded();
		return PROPS.getProperty(key, defaultValue);
	}
}



