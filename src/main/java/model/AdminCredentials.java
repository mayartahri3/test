package model;

import Db.AdminDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class AdminCredentials {



    // For demonstration, we're using simple SHA-256 hashing with hardcoded values
    private static final String ADMIN_USERNAME = "admin";
    // This is the SHA-256 hash of "admin123"
    private static final String ADMIN_PASSWORD_HASH = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

    public static boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        return username.equals(ADMIN_USERNAME) &&
                hashPassword(password).equals(ADMIN_PASSWORD_HASH);
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plain text comparison in case SHA-256 is not available
            System.err.println("Warning: SHA-256 not available, using plain text comparison");
            return password;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


}