package com.example.atmjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ATM";
        String user = "root";
        String password = "devanshu108";
        String userId = "some_user_id";  // You will provide the actual user ID dynamically

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            // Correct query with parameter
            String query = "SELECT balance FROM accounts WHERE user_id = ?";

            // Use PreparedStatement to prevent SQL injection
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, userId);  // Set user ID dynamically here

            // Execute the query and process the result
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Balance: " + balance);
                // You can add more logic here
            } else {
                System.out.println("User ID not found.");
            }

            // Close resources
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }
}
