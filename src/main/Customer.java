package Main;

import config.config;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class Customer {
    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    public static void showMenu(int userId, String username) {
        int choice;

        do {
            System.out.println("\n======================= CUSTOMER DASHBOARD =====================");
            System.out.println("                       Welcome, " + username + "!");
            System.out.println("                        Your User ID: " + userId); 
            System.out.println("================================================================");
            System.out.println("| 1.                     -- Buy Products --                    | ");  
            System.out.println("| 2.                  -- View My Purchases --                  | ");
            System.out.println("| 3.                       -- Logout --                        | ");
            System.out.println("================================================================");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    buyProducts(userId, username); 
                    if (!askContinue()) return;
                    break;
                    
                case 2:
                    viewPurchases(userId);
                    if (!askContinue()) return;
                    break;
                    
                case 3:
                    System.out.println("\nLogging out...");
                    
                    String[] userInfo = Login.loginUser(); 
                    if (userInfo != null) {
                        int newUserId = Integer.parseInt(userInfo[0]);
                        String newRole = userInfo[1];
                        String newUsername = userInfo[2];

                        switch (newRole.toLowerCase()) {
                            case "manager":
                                Manager.showMenu();
                                break;
                            case "cashier":
                                Cashier.showMenu();
                                break;
                            case "customer":
                                Customer.showMenu(newUserId, newUsername);
                                break;
                            default:
                                System.out.println("Unknown role. Returning to login.");
                                Login.loginUser();
                        }
                    }
                    return; 
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    public static boolean askContinue() {
    Scanner sc = new Scanner(System.in);
    System.out.print("\nDo you want to continue? (Y/N): ");
    String input = sc.nextLine();
    return input.equalsIgnoreCase("Y");
}

    
    public static void viewProducts() {
        String query = "SELECT p_id, p_name, p_price, p_stock, p_expiration, " +
                       "CASE " +
                       "WHEN p_stock = 0 THEN 'Unavailable' " +
                       "WHEN p_expiration IS NOT NULL AND DATE(p_expiration) < DATE('now') THEN 'Expired' " +
                       "ELSE 'Available' " +
                       "END AS status " +
                       "FROM tbl_product";

        String[] headers = {"ID", "Name", "Price", "Stock", "Expiration", "Status"};
        String[] columns = {"p_id", "p_name", "p_price", "p_stock", "p_expiration", "status"};

        db.viewRecords(query, headers, columns);
    }

    public static void buyProducts(int userId, String username) {
    System.out.println("\n=== BUY PRODUCTS ===");
    System.out.println("Customer Name: " + username);
    System.out.println("User ID: " + userId);

    List<Integer> productIds = new ArrayList<>();
    List<Integer> quantities = new ArrayList<>();
    List<Double> totals = new ArrayList<>();
    boolean adding = true;

    while (adding) {
       
        viewProducts();

        System.out.print("\nEnter Product ID to add: ");
        int productId = sc.nextInt();
        sc.nextLine();

        
        String stockStr = db.getSingleValue("SELECT p_stock FROM tbl_product WHERE p_id = ?", productId);
        String priceStr = db.getSingleValue("SELECT p_price FROM tbl_product WHERE p_id = ?", productId);
        String expStr   = db.getSingleValue("SELECT p_expiration FROM tbl_product WHERE p_id = ?", productId);

        int stock = Integer.parseInt(stockStr);
        double price = Double.parseDouble(priceStr);

        
        if (expStr != null && !expStr.isEmpty()) {
            java.time.LocalDate expDate = java.time.LocalDate.parse(expStr);
            if (expDate.isBefore(java.time.LocalDate.now())) {
                System.out.println("This product has expired! Cannot add to cart.");
                continue;
            }
        }

        
        if (stock <= 0) {
            System.out.println("Product is unavailable. Cannot add to cart.");
            continue;
        }

        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        sc.nextLine();

        if (quantity > stock) {
            System.out.println("Not enough stock! Available: " + stock);
            continue;
        }

        double total = price * quantity;
        System.out.println("Added to cart: Product " + productId + " x" + quantity + " = ₱" + total);

        productIds.add(productId);
        quantities.add(quantity);
        totals.add(total);

        System.out.print("\nAdd another product? (Y/N): ");
        if (!sc.nextLine().equalsIgnoreCase("Y")) adding = false;
    }

    if (productIds.isEmpty()) {
        System.out.println("No valid products added. Exiting purchase.");
        return;
    }

    System.out.print("Enter Branch Name: ");
    String branch = sc.nextLine();
    System.out.print("Enter Branch Location: ");
    String location = sc.nextLine();

    
    System.out.println("\n=== ORDER SUMMARY ===");
    double grandTotal = 0;
    for (int i = 0; i < productIds.size(); i++) {
        System.out.println("Product ID: " + productIds.get(i) + " | Qty: " + quantities.get(i) + " | Total: ₱" + totals.get(i));
        grandTotal += totals.get(i);
    }
    System.out.println("💰 GRAND TOTAL: ₱" + grandTotal);

  
    System.out.print("Confirm purchase? (Y/N): ");
    if (sc.nextLine().equalsIgnoreCase("Y")) {
        for (int i = 0; i < productIds.size(); i++) {
           
            String insertQuery = "INSERT INTO tbl_sales(u_id, p_id, quantity, sale_date, total, branch, location) " +
                                 "VALUES (?, ?, ?, datetime('now'), ?, ?, ?)";
            db.addRecord(insertQuery,
                         String.valueOf(userId),
                         String.valueOf(productIds.get(i)),
                         String.valueOf(quantities.get(i)),
                         String.valueOf(totals.get(i)),
                         branch,
                         location);

            
            String updateStock = "UPDATE tbl_product SET p_stock = p_stock - ? WHERE p_id = ?";
            db.updateRecord(updateStock, String.valueOf(quantities.get(i)), productIds.get(i));
        }
        System.out.println("Purchase completed successfully!");
        System.out.println("GRAND TOTAL PAID: ₱" + grandTotal);
    } else {
        System.out.println("Checkout cancelled.");
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
