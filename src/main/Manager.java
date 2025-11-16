package Main;

import static Main.Cashier.askContinue;
import config.config;
import java.util.Scanner;

public class Manager {
    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    public static void showMenu() {
        int choice;

        do {
            System.out.println("\n====================== MANAGER DASHBOARD ========================");
            System.out.println("| 1                     -- View Users --                        |");
            System.out.println("| 2.                -- Approve Pending Users --                 |");
            System.out.println("| 3.                    -- Delete User --                       |");
            System.out.println("| 4.                -- Product Management --                    |");
            System.out.println("| 5.                       -- Logout --                         |");
            System.out.println("=================================================================");
            System.out.print("Enter choice:");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                    if (!askContinue()) return;
                    break;
                    
                case 2:
                    approveUser();
                    if (!askContinue()) return;
                    break;
                    
                case 3:
                    deleteUser();
                    if (!askContinue()) return;
                    break;
                    
                case 4:
                    productMenu();
                    if (!askContinue()) return;
                    break;
                    
                case 5:
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


    public static void viewUsers() {
        String query = "SELECT * FROM tbl_user";
        String[] headers = {"ID", "Name", "Email", "Role", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_role", "u_status"};
        db.viewRecords(query, headers, columns);
    }

    public static void approveUser() {
        viewUsers();
        System.out.print("Enter user ID to approve: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "UPDATE tbl_user SET u_status = 'Approved' WHERE u_id = ?";
        db.updateRecord(query, id);
        System.out.println("User approved successfully!");

        viewUsers();
    }

    public static void deleteUser() {

    String viewQuery = "SELECT * FROM tbl_user";
    String[] headers = {"ID", "Name", "Email", "Role", "Status"};
    String[] columns = {"u_id", "u_name", "u_email", "u_role", "u_status"};
    db.viewRecords(viewQuery, headers, columns);

    System.out.print("\nEnter user IDs to delete (comma or space separated): ");
    String input = sc.nextLine();

    
    String[] parts = input.split("[, ]+");

  
    System.out.print("Are you sure you want to delete these users? (Y/N): ");
    String confirm = sc.nextLine();
    if (!confirm.equalsIgnoreCase("Y")) {
        System.out.println("Deletion cancelled.");
        return;
    }

    System.out.println("\nDeleting selected users...");

    for (String part : parts) {
        try {
            int id = Integer.parseInt(part.trim());

            String query = "DELETE FROM tbl_user WHERE u_id = ?";
            db.updateRecord(query, id);

            System.out.println("User with ID " + id + " deleted.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid entry skipped: " + part);
        }
    }

    System.out.println("\nUpdated user list:");
    db.viewRecords(viewQuery, headers, columns);
}




    public static void productMenu() {
        int choice;
        do {
            System.out.println("\n=== PRODUCT MANAGEMENT ===");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Delete Sale");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProduct();
                    if (!askContinue()) return;
                    break;
                case 2:
                    viewProducts();
                    if (!askContinue()) return;
                    break;
                case 3:
                    updateProduct();
                    if (!askContinue()) return;
                    break;
                case 4:
                    deleteProduct();
                    if (!askContinue()) return;
                    break;
                case 5:
                    deleteSale();
                    if (!askContinue()) return;
                    break;
                case 6:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 6);
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


public static void addProduct() {
    System.out.println("\n=== Add New Product ===");
    System.out.print("Enter Product Name: ");
    String name = sc.nextLine();

    System.out.print("Enter Product Price: ");
    double price = sc.nextDouble();
    sc.nextLine();

    System.out.print("Enter Stock Quantity: ");
    int stock = sc.nextInt();
    sc.nextLine();
    if (stock < 0) stock = 0;

    System.out.print("Enter Expiration Date (YYYY-MM-DD) or leave blank: ");
    String expiration = sc.nextLine();
    if (expiration.isEmpty()) expiration = null;

    String query = "INSERT INTO tbl_product(p_name, p_price, p_stock, p_expiration) VALUES (?, ?, ?, ?)";
    db.addRecord(query, name, String.valueOf(price), String.valueOf(stock), expiration);

    System.out.println("Product added successfully!");
    viewProducts();
}


public static void updateProduct() {
    viewProducts();
    System.out.print("\nEnter Product ID to update: ");
    int id = sc.nextInt();
    sc.nextLine();

    System.out.print("Enter new Product Name: ");
    String name = sc.nextLine();

    System.out.print("Enter new Product Price: ");
    double price = sc.nextDouble();
    sc.nextLine();

    System.out.print("Enter new Stock Quantity: ");
    int stock = sc.nextInt();
    sc.nextLine();
    if (stock < 0) stock = 0;

    System.out.print("Enter new Expiration Date (YYYY-MM-DD) or leave blank: ");
    String expiration = sc.nextLine();
    if (expiration.isEmpty()) expiration = null;

    String query = "UPDATE tbl_product SET p_name = ?, p_price = ?, p_stock = ?, p_expiration = ? WHERE p_id = ?";
    db.updateRecord(query, name, String.valueOf(price), String.valueOf(stock), expiration, id);

    System.out.println("Product updated successfully!");
    viewProducts();
}


    public static void deleteProduct() {
        viewProducts();
        System.out.print("Enter Product ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "DELETE FROM tbl_product WHERE p_id = ?";
        db.updateRecord(query, id);
        System.out.println("Product deleted successfully!");

        viewProducts();
    }
    public static void deleteSale() {
    
    String query = "SELECT s.s_id, u.u_name, p.p_name, s.quantity, s.total, s.branch, s.location, s.sale_date " +
                   "FROM tbl_sales s " +
                   "JOIN tbl_user u ON s.u_id = u.u_id " +
                   "JOIN tbl_product p ON s.p_id = p.p_id";

    String[] headers = {"Sale ID", "User", "Product", "Qty", "Total", "Branch", "Location", "Date"};
    String[] columns = {"s_id", "u_name", "p_name", "quantity", "total", "branch", "location", "sale_date"};

    db.viewRecords(query, headers, columns);

    
    System.out.print("Enter Sale ID to delete: ");
    int saleId = sc.nextInt();
    sc.nextLine();

    
    String deleteQuery = "DELETE FROM tbl_sales WHERE s_id = ?";
    db.deleteRecord(deleteQuery, saleId);

    System.out.println("Sale deleted successfully!");

    
    db.viewRecords(query, headers, columns);
}



}
