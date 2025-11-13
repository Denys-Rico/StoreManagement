package Main;
import config.config;
import java.util.Scanner;

public class Cashier {

    public static void showMenu() {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Cashier Panel ---");
            System.out.println("1. View Products");
            System.out.println("2. Add Sales");
            System.out.println("3. View Sales");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    main.viewProduct();
                    break;
                case 2:
                    System.out.print("Enter User ID: ");
                    int uid = sc.nextInt();
                    System.out.print("Enter Product ID: ");
                    int pid = sc.nextInt();
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();

                    config cf = new config();
                    String sql = "INSERT INTO tbl_sales(u_id, p_id, quantity, sale_date) VALUES (?, ?, ?, DATE('now'))";
                    cf.addRecord(sql, String.valueOf(uid), String.valueOf(pid), String.valueOf(qty));
                    main.viewSales();
                    break;
                case 3:
                    main.viewSales();
                    break;
                case 4:
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
}