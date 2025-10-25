package config;

import java.sql.*;
import java.util.*;

public class config {

    // ======================
    // CONNECT TO DATABASE
    // ======================
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:storeDB.db");
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return con;
    }

    // ======================
    // ADD RECORD (INSERT)
    // ======================
    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("✅ Record added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding record: " + e.getMessage());
        }
    }

    // ======================
    // VIEW RECORDS (SELECT)
    // ======================
    public void viewRecords(String sqlQuery, String[] headers, String[] columns) {
        if (headers.length != columns.length) {
            System.out.println("Error: Headers and columns mismatch.");
            return;
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            // Header line
            StringBuilder headerLine = new StringBuilder("-----------------------------------------------------------------\n| ");
            for (String header : headers) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n-----------------------------------------------------------------");
            System.out.println(headerLine);

            // Rows
            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String col : columns) {
                    row.append(String.format("%-20s | ", rs.getString(col)));
                }
                System.out.println(row);
            }
            System.out.println("-----------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    // ======================
    // UPDATE RECORD
    // ======================
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("✅ Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error updating record: " + e.getMessage());
        }
    }

    // ======================
    // DELETE RECORD
    // ======================
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("🗑 Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting record: " + e.getMessage());
        }
    }

    // ======================
    // GET SINGLE DOUBLE VALUE (E.g., product price)
    // ======================
    public double getDouble(String sqlQuery, int id) {
        double result = 0.0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching double: " + e.getMessage());
        }
        return result;
    }

    // ======================
    // FETCH RECORDS (for programmatic use)
    // ======================
    public List<Map<String, Object>> fetchRecords(String sqlQuery, Object... values) {
        List<Map<String, Object>> records = new ArrayList<>();

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                records.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching records: " + e.getMessage());
        }

        return records;
    }
    public void viewRecords(String query, String[] headers, String[] columns, String param) {
    if (headers.length != columns.length) {
        System.out.println("Error: Headers and columns mismatch.");
        return;
    }

    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        // Set the parameter (e.g., u_id)
        pstmt.setString(1, param);

        try (ResultSet rs = pstmt.executeQuery()) {

            // Header line
            StringBuilder headerLine = new StringBuilder("-----------------------------------------------------------------\n| ");
            for (String header : headers) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n-----------------------------------------------------------------");
            System.out.println(headerLine);

            // Rows
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                StringBuilder row = new StringBuilder("| ");
                for (String col : columns) {
                    row.append(String.format("%-20s | ", rs.getString(col)));
                }
                System.out.println(row);
            }

            if (!hasRows) {
                System.out.println("| No records found.                                               |");
            }

            System.out.println("-----------------------------------------------------------------");
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving records: " + e.getMessage());
    }
}

}
