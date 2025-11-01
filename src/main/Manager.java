package Main;

import config.config;
import java.util.Scanner;

public class Manager {
    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    public static void showMenu() {
        int choice;

        do {
            System.out.println("\n=== MANAGER DASHBOARD ===");
            System.out.println("1. View Users");
            System.out.println("2. Approve Pending Users");
            System.out.println("3. Delete User");
            System.out.println("4. Product Management");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    approveUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    productMenu();
                    break;
                case 0:
                    System.out.println("\nLogging out...");
                    // ✅ Return to login page
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
                    return; // ✅ Exit the current loop after logout
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
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
        System.out.println("✅ User approved successfully!");

        viewUsers();
    }

    public static void deleteUser() {
        
        String viewQuery = "SELECT * FROM tbl_user";
        String[] headers = {"ID", "Name", "Email", "Role", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_role", "u_status"};
        db.viewRecords(viewQuery, headers, columns);

        System.out.print("Enter user ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "DELETE FROM tbl_user WHERE u_id = ?";
        db.updateRecord(query, id);
        System.out.println("🗑️ User deleted successfully!");

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
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    deleteSale();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
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

    public static void addProduct() {
        System.out.println("\n=== Add New Product ===");
        System.out.print("Enter Product Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Product Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        String query = "INSERT INTO tbl_product(p_name, p_price) VALUES (?, ?)";
        db.addRecord(query, name, String.valueOf(price));

        System.out.println("✅ Product added successfully!");
        viewProducts();
    }

    public static void updateProduct() {
        viewProducts();
        System.out.print("Enter Product ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new Product Name: ");
        String name = sc.nextLine();
        System.out.print("Enter new Product Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        String query = "UPDATE tbl_product SET p_name = ?, p_price = ? WHERE p_id = ?";
        db.updateRecord(query, name, String.valueOf(price), id);
        System.out.println("✅ Product updated successfully!");

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
