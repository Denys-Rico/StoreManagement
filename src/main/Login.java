package Main;

import config.config;
import java.util.Scanner;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login {

    public static String[] loginUser() {
        config cf = new config();
        Scanner sc = new Scanner(System.in);

        System.out.println("\n==== STORE SYSTEM ====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine(); 

        switch (choice) {
            case 1:
                return performLogin(cf, sc);
            case 2:
                registerUser(cf, sc);
                return loginUser(); 
            case 3:
                System.out.println("Exiting the system... Goodbye!");
                System.exit(0);
                return null;
            default:
                System.out.println("Invalid choice.");
                return loginUser();
        }
    }

    private static String[] performLogin(config cf, Scanner sc) {
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        
        String hashedPassword = hashPassword(password);

        String query = "SELECT * FROM tbl_user WHERE u_email = ? AND u_password = ?";
        try (Connection conn = cf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("u_role");
                String status = rs.getString("u_status");
                String name = rs.getString("u_name");
                int id = rs.getInt("u_id");

                if (status.equalsIgnoreCase("Pending")) {
                    System.out.println("Your account is still pending approval by a Manager.");
                    return null;
                } else {
                    System.out.println("\n✅ Login successful! Welcome " + name + " (" + role + ")");
                    return new String[]{String.valueOf(id), role, name};
                }
            } else {
                System.out.println("❌ Invalid email or password!");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return null;
        }
    }

    private static void registerUser(config cf, Scanner sc) {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Choose Role [Manager, Cashier, Customer]: ");
        String role = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        
        String hashedPassword = hashPassword(password);

        String status;
        if (role.equalsIgnoreCase("Customer")) {
            status = "Approved";
        } else {
            status = "Pending";
            System.out.println("Your role request is pending manager approval.");
        }

        String sql = "INSERT INTO tbl_user(u_name, u_email, u_role, u_status, u_password) VALUES (?, ?, ?, ?, ?)";
        cf.addRecord(sql, name, email, role, status, hashedPassword);

        System.out.println("\n✅ Registration successful! You can now log in.");
    }

    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }
}
