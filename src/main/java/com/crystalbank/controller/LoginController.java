package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.model.Account;
import com.crystalbank.model.User;
import com.crystalbank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        User user = DatabaseManager.login(username, password);
        if (user == null) {
            showError("Invalid username or password.");
            return;
        }

        SessionManager.setCurrentUser(user);

        if (user.getRole().equals("ADMIN")) {
            MainApp.loadScene("admin.fxml", "CrystalBank — Admin Panel");
        } else {
            Account account = DatabaseManager.getAccountByUserId(user.getUserId());
            SessionManager.setCurrentAccount(account);
            MainApp.loadScene("dashboard.fxml", "CrystalBank — Dashboard");
        }
    }

    @FXML
    private void handleGoRegister() {
        MainApp.loadScene("register.fxml", "CrystalBank — Register");
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e53935;");
    }
}
