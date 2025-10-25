package Main;

import config.config;
import java.util.Scanner;
import java.sql.*;

public class Login {

    public static String[] loginUser() {
        config cf = new config();
        Scanner sc = new Scanner(System.in);

        System.out.println("\n==== STORE SYSTEM ====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                return performLogin(cf, sc);
            case 2:
                registerUser(cf, sc);
                return loginUser(); 
            default:
                System.out.println("Invalid choice.");
                return null;
        }
    }

    private static String[] performLogin(config cf, Scanner sc) {
        System.out.print("Enter username: ");
        String name = sc.next();
        System.out.print("Enter gender: ");
        String gender = sc.next();

        String query = "SELECT * FROM tbl_user WHERE u_name = ? AND u_gender = ?";
        try (Connection conn = cf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("u_role");
                String status = rs.getString("u_status");
                int id = rs.getInt("u_id");

                if (status.equalsIgnoreCase("Pending")) {
                    System.out.println("Your account is still pending approval by a Manager.");
                    return null;
                } else {
                    System.out.println("\nLogin successful! Welcome " + name + " (" + role + ")");
                    return new String[]{String.valueOf(id), role, name};
                }
            } else {
                System.out.println("Invalid credentials!");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return null;
        }
    }

    private static void registerUser(config cf, Scanner sc) {
        System.out.print("Enter Name: ");
        String name = sc.next();
        System.out.print("Enter Gender: ");
        String gender = sc.next();
        System.out.print("Choose Role: [Manager, Cashier, Customer]: ");
        String role = sc.next();

        String status;
        if (role.equalsIgnoreCase("Customer")) {
            status = "Approved";
        } else {
            status = "Pending";
            System.out.println("Your role request is pending manager approval.");
        }

        String sql = "INSERT INTO tbl_user(u_name, u_gender, u_role, u_status) VALUES (?, ?, ?, ?)";
        cf.addRecord(sql, name, gender, role, status);
    }
}
