package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TransferController {

    @FXML private TextField recipientField;
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
    private void handleTransfer() {
        String recipient   = recipientField.getText().trim();
        String amountText  = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (recipient.isEmpty() || amountText.isEmpty()) {
            showError("Please fill in recipient account and amount.");
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

        String result = DatabaseManager.transfer(
            SessionManager.getCurrentAccount().getAccountId(),
            recipient, amount
        );

        if (result.equals("SUCCESS")) {
            showSuccess(String.format("Successfully transferred RM %.2f to %s!", amount, recipient));
            recipientField.clear();
            amountField.clear();
            descriptionField.clear();
            var acc = DatabaseManager.getAccountByUserId(
                SessionManager.getCurrentUser().getUserId()
            );
            SessionManager.setCurrentAccount(acc);
            balanceLabel.setText(String.format("Current Balance: RM %.2f", acc.getBalance()));
        } else {
            showError(result);
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
