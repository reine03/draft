package byod.controller;

import byod.session.SessionManager;
import byod.util.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        try {
            String hashedPassword = sha256(password);
            String sql = "SELECT id, full_name, role FROM users WHERE username = ? AND password = ?";
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SessionManager.login(
                            rs.getInt("id"),
                            username,
                            rs.getString("full_name"),
                            rs.getString("role")
                        );
                        openMainWindow();
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        passwordField.clear();
                    }
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Login error: " + e.getMessage());
        }
    }

    private void openMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/byod/fxml/main.fxml"));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(loader.load(), 1100, 700));
        stage.setTitle("BYOD Registration and Monitoring System");
        stage.show();
    }

    private String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
