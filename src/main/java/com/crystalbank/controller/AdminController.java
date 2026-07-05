package com.crystalbank.controller;

import com.crystalbank.MainApp;
import com.crystalbank.database.DatabaseManager;
import com.crystalbank.model.Account;
import com.crystalbank.model.Transaction;
import com.crystalbank.model.User;
import com.crystalbank.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AdminController {

    // Users tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdCol;
    @FXML private TableColumn<User, String>  userNameCol;
    @FXML private TableColumn<User, String>  userUsernameCol;
    @FXML private TableColumn<User, String>  userEmailCol;
    @FXML private TableColumn<User, String>  userPhoneCol;
    @FXML private TableColumn<User, String>  userCreatedCol;

    // Accounts tab
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, Integer> accIdCol;
    @FXML private TableColumn<Account, String>  accNumberCol;
    @FXML private TableColumn<Account, String>  accTypeCol;
    @FXML private TableColumn<Account, Double>  accBalanceCol;
    @FXML private TableColumn<Account, String>  accStatusCol;
    @FXML private TableColumn<Account, String>  accCreatedCol;

    // Transactions tab
    @FXML private TableView<Transaction> txTable;
    @FXML private TableColumn<Transaction, Integer> txIdCol;
    @FXML private TableColumn<Transaction, Integer> txAccIdCol;
    @FXML private TableColumn<Transaction, String>  txTypeCol;
    @FXML private TableColumn<Transaction, Double>  txAmountCol;
    @FXML private TableColumn<Transaction, Double>  txBalanceCol;
    @FXML private TableColumn<Transaction, String>  txDescCol;
    @FXML private TableColumn<Transaction, String>  txDateCol;

    @FXML private Label adminNameLabel;
    @FXML private Label statusMessageLabel;

    @FXML
    private void initialize() {
        adminNameLabel.setText("Admin: " + SessionManager.getCurrentUser().getFullName());
        setupUsersTable();
        setupAccountsTable();
        setupTransactionsTable();
        loadAllData();
    }

    private void setupUsersTable() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userCreatedCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void setupAccountsTable() {
        accIdCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        accNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accTypeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        accBalanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        accStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        accCreatedCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        accBalanceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("RM %.2f", item));
            }
        });

        accStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(switch (item) {
                        case "Active" -> "-fx-text-fill: #43a047; -fx-font-weight: bold;";
                        case "Frozen" -> "-fx-text-fill: #1e88e5; -fx-font-weight: bold;";
                        case "Closed" -> "-fx-text-fill: #e53935; -fx-font-weight: bold;";
                        default -> "";
                    });
                }
            }
        });
    }

    private void setupTransactionsTable() {
        txIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        txAccIdCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        txTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        txAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        txBalanceCol.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        txDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        txDateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        txAmountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("RM %.2f", item));
            }
        });
    }

    private void loadAllData() {
        List<User> users = DatabaseManager.getAllClients();
        usersTable.setItems(FXCollections.observableArrayList(users));

        List<Account> accounts = DatabaseManager.getAllAccounts();
        accountsTable.setItems(FXCollections.observableArrayList(accounts));

        List<Transaction> transactions = DatabaseManager.getAllTransactions();
        txTable.setItems(FXCollections.observableArrayList(transactions));
    }

    @FXML
    private void handleFreezeAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Please select an account."); return; }
        if (DatabaseManager.updateUserStatus(selected.getAccountId(), "Frozen")) {
            showStatus("Account " + selected.getAccountNumber() + " frozen.");
            loadAllData();
        }
    }

    @FXML
    private void handleActivateAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Please select an account."); return; }
        if (DatabaseManager.updateUserStatus(selected.getAccountId(), "Active")) {
            showStatus("Account " + selected.getAccountNumber() + " activated.");
            loadAllData();
        }
    }

    @FXML
    private void handleCloseAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Please select an account."); return; }
        if (DatabaseManager.updateUserStatus(selected.getAccountId(), "Closed")) {
            showStatus("Account " + selected.getAccountNumber() + " closed.");
            loadAllData();
        }
    }

    @FXML
    private void handleRefresh() {
        loadAllData();
        showStatus("Data refreshed.");
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        MainApp.loadScene("login.fxml", "CrystalBank — Login");
    }

    private void showStatus(String msg) {
        statusMessageLabel.setText(msg);
    }
}
