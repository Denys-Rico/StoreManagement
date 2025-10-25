package Main;

import config.config;
import java.util.Scanner;

public class Customer {
    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    
    public static void showMenu(int userId, String username) {
        int choice;

        do {
            System.out.println("\n=== CUSTOMER DASHBOARD ===");
            System.out.println("Welcome, " + username + "!");
            System.out.println("1. View Products");
            System.out.println("2. Buy Product");
            System.out.println("3. View My Purchases");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    buyProduct(userId, username);
                    break;
                case 3:
                    viewPurchases(userId);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

   
    public static void viewProducts() {
        String query = "SELECT * FROM tbl_product";
        String[] headers = {"ID", "Name", "Price"};
        String[] columns = {"p_id", "p_name", "p_price"};
        db.viewRecords(query, headers, columns);
    }

   
    public static void buyProduct(int userId, String username) {
        viewProducts();

        System.out.print("Enter Product ID to buy: ");
        int productId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Branch Name: ");
        String branch = sc.nextLine();

        System.out.print("Enter Branch Location: ");
        String location = sc.nextLine();

        double price = db.getDouble("SELECT p_price FROM tbl_product WHERE p_id = ?", productId);
        double total = price * quantity;

        System.out.println("\n=== Order Summary ===");
        System.out.println("Product ID: " + productId);
        System.out.println("Quantity: " + quantity);
        System.out.println("Branch: " + branch);
        System.out.println("Location: " + location);
        System.out.println("Total Price: ₱" + total);
        System.out.print("Confirm purchase? (Y/N): ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String insertQuery = "INSERT INTO tbl_sales(u_id, p_id, quantity, sale_date, total, branch, location) " +
                                 "VALUES (?, ?, ?, datetime('now'), ?, ?, ?)";
            db.addRecord(insertQuery,
                         String.valueOf(userId),       
                         String.valueOf(productId),    
                         String.valueOf(quantity),     
                         String.valueOf(total),        
                         branch,                       
                         location);                    
            System.out.println("✅ Purchase successful!");
        } else {
            System.out.println("❌ Purchase cancelled.");
        }
    }

    public static void viewPurchases(int userId) {
    String query = "SELECT s.s_id, p.p_name, s.quantity, s.total, s.branch, s.location, s.sale_date " +
                   "FROM tbl_sales s JOIN tbl_product p ON s.p_id = p.p_id " +
                   "WHERE s.u_id = ?";
    String[] headers = {"Sale ID", "Product", "Qty", "Total", "Branch", "Location", "Date"};
    String[] columns = {"s_id", "p_name", "quantity", "total", "branch", "location", "sale_date"};
    db.viewRecords(query, headers, columns, String.valueOf(userId));
}

    
}
