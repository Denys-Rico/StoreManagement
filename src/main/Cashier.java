package Main;

import config.config;
import java.util.Scanner;

public class Cashier {
    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    public static void showMenu() {
        int choice;

        do {
            System.out.println("\n======================= Cashier Panel =======================");
            System.out.println("| 1.                 -- View Products --                    |");
            System.out.println("| 2.                 -- Update Stock --                     |");
            System.out.println("| 3.                 -- View Sales --                       |");
            System.out.println("| 4.                 -- Check Low Stock --                  |");
            System.out.println("| 5.                 -- Generate Sales Report --            |");
            System.out.println("| 0.                 -- Logout --                           |");
            System.out.println("=============================================================");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewProducts();
                    if (!askContinue()) return;
                    break;
                    
                case 2:
                    updateStock();
                    if (!askContinue()) return;
                    break;
                    
                case 3:
                    viewSales();
                    if (!askContinue()) return;
                    break;
                    
                case 4:
                    checkLowStock();
                    if (!askContinue()) return;
                    break;
                    
                case 5:
                    generateSalesReport();
                    if (!askContinue()) return;
                    break;
                    
                case 0:
                    logout();
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
        String sql = "SELECT p_id, p_name, p_price, p_stock, p_expiration FROM tbl_product";
        String[] headers = {"ID", "Name", "Price", "Stock", "Expiration"};
        String[] columns = {"p_id", "p_name", "p_price", "p_stock", "p_expiration"};
        db.viewRecords(sql, headers, columns);
    }

    public static void updateStock() {
        
         String viewSql = "SELECT p_id, p_name, p_price, p_stock, p_expiration FROM tbl_product";
        String[] headers = {"ID", "Name", "Price", "Stock", "Expiration"};
        String[] columns = {"p_id", "p_name", "p_price", "p_stock", "p_expiration"};
        db.viewRecords(viewSql, headers, columns);
        
        System.out.print("Enter Product ID to update: ");
        int pid = sc.nextInt();
        sc.nextLine();
        
        System.out.print("Enter new stock quantity: ");
        int stock = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE tbl_product SET p_stock = ? WHERE p_id = ?";
        db.updateRecord(sql, stock, pid);
    }

    public static void viewSales() {
        String sql = "SELECT s.s_id, u.u_name, p.p_name, s.quantity, s.total, s.branch, s.location, s.sale_date " +
                     "FROM tbl_sales s " +
                     "JOIN tbl_user u ON s.u_id = u.u_id " +
                     "JOIN tbl_product p ON s.p_id = p.p_id";
        String[] headers = {"Sale ID", "User", "Product", "Qty", "Total", "Branch", "Location", "Date"};
        String[] columns = {"s_id", "u_name", "p_name", "quantity", "total", "branch", "location", "sale_date"};
        db.viewRecords(sql, headers, columns);
    }

    public static void checkLowStock() {
        String sql = "SELECT p_id, p_name, p_stock FROM tbl_product WHERE p_stock <= 5";
        String[] headers = {"ID", "Name", "Stock"};
        String[] columns = {"p_id", "p_name", "p_stock"};
        db.viewRecords(sql, headers, columns);
    }

    public static void generateSalesReport() {
        String sql = "SELECT p.p_name, SUM(s.quantity) AS total_sold, SUM(s.total) AS revenue " +
                     "FROM tbl_sales s " +
                     "JOIN tbl_product p ON s.p_id = p.p_id " +
                     "GROUP BY s.p_id";
        String[] headers = {"Product", "Total Sold", "Revenue"};
        String[] columns = {"p_name", "total_sold", "revenue"};
        db.viewRecords(sql, headers, columns);
    }

    public static void logout() {
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
    }
}
    