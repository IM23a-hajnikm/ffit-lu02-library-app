package ch.bzz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	private static final String URL = Config.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/localdb");
	private static final String USER = Config.getOrDefault("DB_USER", "localuser");
	private static final String PASSWORD = Config.getOrDefault("DB_PASSWORD", "");

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}



