package com.example.atmjava;

import java.sql.*;

public class DatabaseConnector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ATM";  // ✔ Correct DB name
        String user = "root";
        String password = "devanshu108";
        String userId = "USER1234";  // Use real userID that exists in users table

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            // ✔ Correct table & column names
            String query = "SELECT balance FROM users WHERE userID = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Balance: " + balance);
            } else {
                System.out.println("User ID not found.");
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }
}
