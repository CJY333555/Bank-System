package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.model.Account;
import com.crystalbank.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label accountNumberLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        refreshData();
    }

    private void refreshData() {
        // Reload fresh account data from DB
        Account account = DatabaseManager.getAccountByUserId(
            SessionManager.getCurrentUser().getUserId()
        );
        SessionManager.setCurrentAccount(account);

        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());
        accountNumberLabel.setText(account.getAccountNumber());
        accountTypeLabel.setText(account.getAccountType());
        balanceLabel.setText(String.format("RM %.2f", account.getBalance()));
        statusLabel.setText(account.getStatus());

        // Color status
        switch (account.getStatus()) {
            case "Active"  -> statusLabel.setStyle("-fx-text-fill: #43a047; -fx-font-weight: bold;");
            case "Frozen"  -> statusLabel.setStyle("-fx-text-fill: #1e88e5; -fx-font-weight: bold;");
            case "Closed"  -> statusLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
        }
    }

    @FXML private void handleDeposit() {
        MainApp.loadScene("deposit.fxml", "CrystalBank — Deposit");
    }

    @FXML private void handleWithdraw() {
        MainApp.loadScene("withdraw.fxml", "CrystalBank — Withdraw");
    }

    @FXML private void handleTransfer() {
        MainApp.loadScene("transfer.fxml", "CrystalBank — Transfer");
    }

    @FXML private void handleHistory() {
        MainApp.loadScene("transactionHistory.fxml", "CrystalBank — Transaction History");
    }

    @FXML private void handleLogout() {
        SessionManager.logout();
        MainApp.loadScene("login.fxml", "CrystalBank — Login");
    }
}
