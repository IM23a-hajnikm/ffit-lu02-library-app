package ch.bzz.db;

import ch.bzz.Database;
import ch.bzz.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserPersistor {

    public void insert(User user) {
        String sql = "INSERT INTO users (firstname, lastname, date_of_birth, email, password_hash, password_salt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFirstname());
            ps.setString(2, user.getLastname());
            ps.setObject(3, user.getDateOfBirth());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPasswordHash());
            ps.setBytes(6, user.getPasswordSalt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }
}


