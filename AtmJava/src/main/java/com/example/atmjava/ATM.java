package com.example.atmjava;

import java.sql.*;
import java.util.*;

public class ATM {
    private Connection connection;
    private Scanner scanner;
    private Map<String, User> usersMap = new HashMap<>();
    private Map<String, List<Transaction>> transactionHistoryMap = new HashMap<>();

    // SQL statements
    private static final String INSERT_USER_SQL = "INSERT INTO users (userID, userPIN, name, accountNumber, balance) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_USER_SQL = "SELECT * FROM users WHERE userID = ? AND userPIN = ?";
    private static final String INSERT_TRANSACTION_SQL = "INSERT INTO transactions (userID, amount, transactionType, targetAccountNumber) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BALANCE_SQL = "SELECT balance FROM users WHERE userID = ?";
    private static final String UPDATE_BALANCE_SQL = "UPDATE users SET balance = ? WHERE userID = ?";
    private static final String SELECT_TRANSACTIONS_SQL = "SELECT * FROM transactions WHERE userID = ?";

    // Mock database for users
    public static Map<String, String> userDatabase = new HashMap<>();

    // Method to validate user login
    public static boolean validateLogin(String userId, String password) {
        String storedPassword = userDatabase.get(userId);
        return storedPassword != null && storedPassword.equals(password);
    }

    // Method to add users to the mock database (for testing purposes)
    public static void addUser(String userId, String password) {
        userDatabase.put(userId, password);
    }

    public ATM() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM", "root", "devanshu108");
            scanner = new Scanner(System.in);
            loadUsersFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.showMainMenu();
    }

    private void loadUsersFromDatabase() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                String userId = rs.getString("userID");
                String name = rs.getString("name");
                String pin = rs.getString("userPIN");
                String accountNumber = rs.getString("accountNumber");
                double balance = rs.getDouble("balance");

                usersMap.put(userId, new User(userId, pin, name, accountNumber, balance));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users from database: " + e.getMessage());
        }
    }

    public void showMainMenu() {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Create New User Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = getInputOption();
            switch (option) {
                case 1 -> loginFlow();
                case 2 -> createUserFlow();
                case 3 -> exitApplication();
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    private int getInputOption() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void loginFlow() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        login(userId, pin);
    }

    private void createUserFlow() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        String pin = scanner.nextLine();
        createNewUser(name, pin);
    }

    private void exitApplication() {
        System.out.println("Exiting the application.");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
        System.exit(0);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        long accountNumber = 100000000000L + (long) (random.nextDouble() * 900000000000L);
        return String.valueOf(accountNumber);
    }

    private String generateUserID() {
        Random random = new Random();
        return "USER" + (1000 + random.nextInt(9000));
    }

    public void createNewUser(String name, String pin) {
        String userId = generateUserID();
        String accountNumber = generateAccountNumber();
        User newUser = new User(userId, pin, name, accountNumber, 0.0);

        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_USER_SQL)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, pin);
            pstmt.setString(3, name);
            pstmt.setString(4, accountNumber);
            pstmt.setDouble(5, 0.0);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User account created successfully!");
                System.out.println("Your User ID: " + userId);
                System.out.println("Your Account Number: " + accountNumber);

                usersMap.put(userId, newUser);
            } else {
                System.out.println("Failed to create account.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    public boolean login(String userId, String pin) {
        User user = usersMap.get(userId);

        if (user != null && user.getPin().equals(pin)) {
            System.out.println("Login successful!");
            userMenu(userId);
            return true;
        } else {
            System.out.println("Invalid User ID or PIN.");
        }
        return false;
    }

    public void userMenu(String userId) {
        while (true) {
            System.out.println("\n1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View Transaction History");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int option = getInputOption();
            switch (option) {
                case 1 -> deposit(userId);
                case 2 -> withdraw(userId);
                case 3 -> transfer(userId);
                case 4 -> showTransactionHistory(userId);
                case 5 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    public void deposit(String userId) {
        System.out.print("Enter amount to deposit: ");
        double amount = getInputAmount();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(INSERT_TRANSACTION_SQL)) {
                ps.setString(1, userId);
                ps.setDouble(2, amount);
                ps.setString(3, "deposit");
                ps.setNull(4, Types.VARCHAR);
                ps.executeUpdate();
                updateBalance(userId, amount);
                connection.commit();
                System.out.println("Deposit successful.");
                addTransaction(userId, amount, "deposit", null);
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error during deposit: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error managing transaction: " + e.getMessage());
        }
    }

    public void withdraw(String userId) {
        System.out.print("Enter amount to withdraw: ");
        double amount = getInputAmount();
        double balance = getBalance(userId);
        if (balance >= amount) {
            try {
                connection.setAutoCommit(false);
                try (PreparedStatement ps = connection.prepareStatement(INSERT_TRANSACTION_SQL)) {
                    ps.setString(1, userId);
                    ps.setDouble(2, amount);
                    ps.setString(3, "withdraw");
                    ps.setNull(4, Types.VARCHAR);
                    ps.executeUpdate();
                    updateBalance(userId, -amount);
                    connection.commit();
                    System.out.println("Withdrawal successful.");
                    addTransaction(userId, amount, "withdraw", null);
                } catch (SQLException e) {
                    connection.rollback();
                    System.err.println("Error during withdrawal: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Error managing transaction: " + e.getMessage());
            }
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    public void transfer(String userId) {
        System.out.print("Enter recipient User ID: ");
        String recipientId = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = getInputAmount();
        if (!isRecipientValid(recipientId)) {
            System.out.println("Recipient user not found.");
            return;
        }
        double balance = getBalance(userId);
        if (balance >= amount) {
            try {
                connection.setAutoCommit(false);
                try (PreparedStatement ps = connection.prepareStatement(INSERT_TRANSACTION_SQL)) {
                    ps.setString(1, userId);
                    ps.setDouble(2, amount);
                    ps.setString(3, "transfer");
                    ps.setString(4, usersMap.get(recipientId).getAccountNumber());
                    ps.executeUpdate();
                    updateBalance(userId, -amount);
                    updateBalance(recipientId, amount);
                    connection.commit();
                    System.out.println("Transfer successful.");
                    addTransaction(userId, amount, "transfer", usersMap.get(recipientId).getAccountNumber());
                } catch (SQLException e) {
                    connection.rollback();
                    System.err.println("Error during transfer: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Error managing transaction: " + e.getMessage());
            }
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    private boolean isRecipientValid(String recipientId) {
        return usersMap.containsKey(recipientId);
    }

    private void showTransactionHistory(String userId) {
        List<Transaction> transactions = transactionHistoryMap.getOrDefault(userId, new ArrayList<>());
        if (transactions.isEmpty()) {
            System.out.println("No transaction history found.");
        } else {
            System.out.println("Transaction History:");
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private void addTransaction(String userId, double amount, String type, String targetAccount) {
        if (!transactionHistoryMap.containsKey(userId)) {
            transactionHistoryMap.put(userId, new ArrayList<>());
        }
        Transaction transaction = new Transaction(userId, amount, type, targetAccount);
        transactionHistoryMap.get(userId).add(transaction);
    }

    private double getBalance(String userId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BALANCE_SQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching balance: " + e.getMessage());
        }
        return 0;
    }

    private void updateBalance(String userId, double amount) {
        double currentBalance = getBalance(userId);
        double newBalance = currentBalance + amount;
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_BALANCE_SQL)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
    }

    private double getInputAmount() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid amount.");
            }
        }
    }

    private static class User {
        private String userID;
        private String pin;
        private String name;
        private String accountNumber;
        private double balance;

        public User(String userID, String pin, String name, String accountNumber, double balance) {
            this.userID = userID;
            this.pin = pin;
            this.name = name;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        // Getters for User class
        public String getUserID() {
            return userID;
        }

        public String getPin() {
            return pin;
        }

        public String getName() {
            return name;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public double getBalance() {
            return balance;
        }
    }

    private static class Transaction {
        private String userId;
        private double amount;
        private String transactionType;
        private String targetAccountNumber;

        public Transaction(String userId, double amount, String transactionType, String targetAccountNumber) {
            this.userId = userId;
            this.amount = amount;
            this.transactionType = transactionType;
            this.targetAccountNumber = targetAccountNumber;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "userId='" + userId + '\'' +
                    ", amount=" + amount +
                    ", transactionType='" + transactionType + '\'' +
                    ", targetAccountNumber='" + targetAccountNumber + '\'' +
                    '}';
        }
    }
}