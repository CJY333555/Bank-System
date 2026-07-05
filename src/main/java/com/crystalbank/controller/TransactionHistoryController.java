package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.model.Transaction;
import com.crystalbank.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TransactionHistoryController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, Double> amountCol;
    @FXML private TableColumn<Transaction, Double> balanceCol;
    @FXML private TableColumn<Transaction, String> descCol;
    @FXML private Label accountLabel;

    @FXML
    private void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Format amount column with RM
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("RM %.2f", item));
            }
        });

        // Format balance column with RM
        balanceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("RM %.2f", item));
            }
        });

        // Color type column
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(switch (item) {
                        case "Deposit", "Transfer In" -> "-fx-text-fill: #43a047; -fx-font-weight: bold;";
                        case "Withdraw", "Transfer Out" -> "-fx-text-fill: #e53935; -fx-font-weight: bold;";
                        default -> "";
                    });
                }
            }
        });

        accountLabel.setText("Account: " + SessionManager.getCurrentAccount().getAccountNumber());

        List<Transaction> transactions = DatabaseManager.getTransactions(
            SessionManager.getCurrentAccount().getAccountId()
        );
        transactionTable.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("dashboard.fxml", "CrystalBank — Dashboard");
    }
}
