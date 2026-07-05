package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepositController {

    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private Label messageLabel;
    @FXML private Label balanceLabel;

    @FXML
    private void initialize() {
        balanceLabel.setText(String.format("Current Balance: RM %.2f",
            SessionManager.getCurrentAccount().getBalance()));
    }

    @FXML
    private void handleDeposit() {
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (amountText.isEmpty()) {
            showError("Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
            return;
        }

        if (amount <= 0) {
            showError("Amount must be greater than 0.");
            return;
        }

        if (amount > 50000) {
            showError("Maximum deposit per transaction is RM 50,000.");
            return;
        }

        if (description.isEmpty()) description = "Cash Deposit";

        boolean success = DatabaseManager.deposit(
            SessionManager.getCurrentAccount().getAccountId(),
            amount, description
        );

        if (success) {
            showSuccess(String.format("Successfully deposited RM %.2f!", amount));
            amountField.clear();
            descriptionField.clear();
            // Refresh balance
            var acc = DatabaseManager.getAccountByUserId(
                SessionManager.getCurrentUser().getUserId()
            );
            SessionManager.setCurrentAccount(acc);
            balanceLabel.setText(String.format("Current Balance: RM %.2f", acc.getBalance()));
        } else {
            showError("Deposit failed. Account may not be active.");
        }
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("dashboard.fxml", "CrystalBank — Dashboard");
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e53935;");
    }

    private void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #43a047;");
    }
}
