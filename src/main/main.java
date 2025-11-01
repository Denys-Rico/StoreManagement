package Main;

import config.config;

public class main {

    public static void main(String[] args) {
        
        String[] userInfo = Login.loginUser();
        if (userInfo == null) return; 

        
        int userId = Integer.parseInt(userInfo[0]);
        String role = userInfo[1];

        
        switch (role.toLowerCase()) {
            case "manager":
                Manager.showMenu();
                break;

            case "cashier":
                Cashier.showMenu();
                break;

            case "customer":
         int cId = Integer.parseInt(userInfo[0]);  
         String username = userInfo[1];               
         Customer.showMenu(userId, username);         
         break;


            default:
                System.out.println("Unknown role: " + role);
                break;
        }
    }

    
    public static void viewUser() {
        String votersQuery = "SELECT * FROM tbl_user";
        String[] votersHeaders = {"ID", "Name", "Password", "Role"};
        String[] votersColumns = {"u_id", "u_name", "u_pass", "u_role"};
        config cf = new config();
        cf.viewRecords(votersQuery, votersHeaders, votersColumns);
    }

    public static void viewProduct() {
        String query = "SELECT * FROM tbl_product";
        String[] headers = {"ID", "Name", "Price"};
        String[] columns = {"p_id", "p_name", "p_price"};
        config cf = new config();
        cf.viewRecords(query, headers, columns);
    }

    public static void viewBranch() {
        String query = "SELECT * FROM tbl_branch";
        String[] headers = {"ID", "Name", "Location"};
        String[] columns = {"b_id", "b_name", "b_location"};
        config cf = new config();
        cf.viewRecords(query, headers, columns);
    }

    public static void viewSales() {
        String query = "SELECT * FROM tbl_sales";
        String[] headers = {"ID", "User ID", "Product ID", "Quantity", "Date"};
        String[] columns = {"s_id", "u_id", "p_id", "quantity", "sale_date"};
        config cf = new config();
        cf.viewRecords(query, headers, columns);
    }
}
