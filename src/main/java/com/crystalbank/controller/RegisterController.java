package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        accountTypeCombo.getItems().addAll("Savings", "Current");
        accountTypeCombo.setValue("Savings");
    }

    @FXML
    private void handleRegister() {
        String fullName    = fullNameField.getText().trim();
        String username    = usernameField.getText().trim();
        String password    = passwordField.getText().trim();
        String confirmPass = confirmPasswordField.getText().trim();
        String email       = emailField.getText().trim();
        String phone       = phoneField.getText().trim();
        String accountType = accountTypeCombo.getValue();

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()
                || email.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (!password.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (DatabaseManager.usernameExists(username)) {
            showError("Username already taken.");
            return;
        }
        if (DatabaseManager.emailExists(email)) {
            showError("Email already registered.");
            return;
        }

        boolean success = DatabaseManager.registerUser(
            fullName, username, password, email, phone, accountType
        );

        if (success) {
            showSuccess("Account created successfully! Please login.");
            clearFields();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        MainApp.loadScene("login.fxml", "CrystalBank — Login");
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e53935;");
    }

    private void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #43a047;");
    }

    private void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        phoneField.clear();
        accountTypeCombo.setValue("Savings");
    }
}
