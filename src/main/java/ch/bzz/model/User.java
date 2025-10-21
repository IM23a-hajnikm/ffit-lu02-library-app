package ch.bzz.model;

import java.time.LocalDate;

public class User {

    private final int id;
    private final String firstname;
    private final String lastname;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String passwordHash;
    private final byte[] passwordSalt;

    public User(int id, String firstname, String lastname, LocalDate dateOfBirth, String email, String passwordHash, byte[] passwordSalt) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public byte[] getPasswordSalt() { return passwordSalt; }
}


