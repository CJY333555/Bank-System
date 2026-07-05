package com.crystalbank.database;

import com.crystalbank.model.Account;
import com.crystalbank.model.Transaction;
import com.crystalbank.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:crystalbank.db";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Connection ────────────────────────────────────────────────────────────
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // ── Initialize tables + seed admin ───────────────────────────────────────
    public static void initialize() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                user_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name  TEXT    NOT NULL,
                username   TEXT    NOT NULL UNIQUE,
                password   TEXT    NOT NULL,
                email      TEXT    NOT NULL UNIQUE,
                phone      TEXT    NOT NULL,
                role       TEXT    NOT NULL DEFAULT 'CLIENT',
                created_at TEXT    NOT NULL
            )""";

        String createAccounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id        INTEGER NOT NULL,
                account_number TEXT    NOT NULL UNIQUE,
                account_type   TEXT    NOT NULL,
                balance        REAL    NOT NULL DEFAULT 0.0,
                status         TEXT    NOT NULL DEFAULT 'Active',
                created_at     TEXT    NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )""";

        String createTransactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id     INTEGER NOT NULL,
                type           TEXT    NOT NULL,
                amount         REAL    NOT NULL,
                balance_after  REAL    NOT NULL,
                description    TEXT,
                created_at     TEXT    NOT NULL,
                FOREIGN KEY (account_id) REFERENCES accounts(account_id)
            )""";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createAccounts);
            stmt.execute(createTransactions);
            seedAdmin(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates default admin account if none exists
    private static void seedAdmin(Connection conn) throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(check)) {
            if (rs.getInt(1) == 0) {
                String now = LocalDateTime.now().format(FMT);
                String sql = """
                    INSERT INTO users (full_name, username, password, email, phone, role, created_at)
                    VALUES ('Admin', 'admin', 'admin123', 'admin@crystalbank.com', '0100000000', 'ADMIN', ?)
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, now);
                    ps.executeUpdate();
                }
            }
        }
    }

    // ── AUTH ──────────────────────────────────────────────────────────────────
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    // ── USERS ─────────────────────────────────────────────────────────────────
    public static boolean registerUser(String fullName, String username,
                                       String password, String email,
                                       String phone, String accountType) {
        String now = LocalDateTime.now().format(FMT);
        String sqlUser = """
            INSERT INTO users (full_name, username, password, email, phone, role, created_at)
            VALUES (?, ?, ?, ?, ?, 'CLIENT', ?)
            """;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sqlUser,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, now);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int userId = keys.getInt(1);
                createAccount(conn, userId, accountType, now);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getAllClients() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CLIENT' ORDER BY user_id DESC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean updateUserStatus(int accountId, String status) {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // ── ACCOUNTS ──────────────────────────────────────────────────────────────
    private static void createAccount(Connection conn, int userId,
                                      String accountType, String now) throws SQLException {
        String accNumber = generateAccountNumber();
        String sql = """
            INSERT INTO accounts (user_id, account_number, account_type, balance, status, created_at)
            VALUES (?, ?, ?, 0.0, 'Active', ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, accNumber);
            ps.setString(3, accountType);
            ps.setString(4, now);
            ps.executeUpdate();
        }
    }

    public static Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_id DESC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapAccount(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── TRANSACTIONS ──────────────────────────────────────────────────────────
    public static boolean deposit(int accountId, double amount, String description) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            Account acc = getAccountById(conn, accountId);
            if (acc == null || !acc.getStatus().equals("Active")) return false;

            double newBalance = acc.getBalance() + amount;
            updateBalance(conn, accountId, newBalance);
            insertTransaction(conn, accountId, "Deposit", amount, newBalance, description);
            conn.commit();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static boolean withdraw(int accountId, double amount, String description) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            Account acc = getAccountById(conn, accountId);
            if (acc == null || !acc.getStatus().equals("Active")) return false;
            if (acc.getBalance() < amount) return false;

            double newBalance = acc.getBalance() - amount;
            updateBalance(conn, accountId, newBalance);
            insertTransaction(conn, accountId, "Withdraw", amount, newBalance, description);
            conn.commit();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static String transfer(int fromAccountId, String toAccountNumber, double amount) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            Account from = getAccountById(conn, fromAccountId);
            Account to   = getAccountByNumberConn(conn, toAccountNumber);

            if (from == null || !from.getStatus().equals("Active"))
                return "Your account is not active.";
            if (to == null)
                return "Recipient account not found.";
            if (to.getAccountId() == fromAccountId)
                return "Cannot transfer to the same account.";
            if (!to.getStatus().equals("Active"))
                return "Recipient account is not active.";
            if (from.getBalance() < amount)
                return "Insufficient balance.";

            double fromNew = from.getBalance() - amount;
            double toNew   = to.getBalance() + amount;

            updateBalance(conn, fromAccountId, fromNew);
            updateBalance(conn, to.getAccountId(), toNew);

            insertTransaction(conn, fromAccountId, "Transfer Out", amount, fromNew,
                "Transfer to " + toAccountNumber);
            insertTransaction(conn, to.getAccountId(), "Transfer In", amount, toNew,
                "Transfer from " + from.getAccountNumber());

            conn.commit();
            return "SUCCESS";
        } catch (SQLException e) { e.printStackTrace(); return "Transfer failed."; }
    }

    public static List<Transaction> getTransactions(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions WHERE account_id = ?
            ORDER BY created_at DESC LIMIT 50
            """;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC LIMIT 100";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private static Account getAccountById(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        }
        return null;
    }

    private static Account getAccountByNumberConn(Connection conn, String number) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        }
        return null;
    }

    private static void updateBalance(Connection conn, int accountId, double balance)
            throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private static void insertTransaction(Connection conn, int accountId, String type,
                                          double amount, double balanceAfter,
                                          String description) throws SQLException {
        String now = LocalDateTime.now().format(FMT);
        String sql = """
            INSERT INTO transactions (account_id, type, amount, balance_after, description, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            ps.setString(5, description);
            ps.setString(6, now);
            ps.executeUpdate();
        }
    }

    private static String generateAccountNumber() {
        return "CB" + (100000000 + new Random().nextInt(900000000));
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("full_name"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("role"),
            rs.getString("created_at")
        );
    }

    private static Account mapAccount(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("account_id"),
            rs.getInt("user_id"),
            rs.getString("account_number"),
            rs.getString("account_type"),
            rs.getDouble("balance"),
            rs.getString("status"),
            rs.getString("created_at")
        );
    }

    private static Transaction mapTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("account_id"),
            rs.getString("type"),
            rs.getDouble("amount"),
            rs.getDouble("balance_after"),
            rs.getString("description"),
            rs.getString("created_at")
        );
    }
}
