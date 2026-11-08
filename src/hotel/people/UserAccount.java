package hotel.people;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserAccount {
    private String username;
    private String passwordHash;
    private AccountType type;
    private int ownerId;
    private boolean active;

    public UserAccount(String username, String passwordHash, AccountType type, int ownerId, boolean active) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.type = type;
        this.ownerId = ownerId;
        this.active = active;
    }

    public static UserAccount create(String username, String password, AccountType type, int ownerId) {
        return new UserAccount(username, hash(password), type, ownerId, true);
    }

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder text = new StringBuilder();
            for (byte b : bytes) {
                text.append(String.format("%02x", b));
            }
            return text.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean checkPassword(String password) {
        return active && passwordHash.equals(hash(password));
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!checkPassword(oldPassword)) {
            return false;
        }
        passwordHash = hash(newPassword);
        return true;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountType getType() {
        return type;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
