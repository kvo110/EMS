import com.employeemgmt.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Simple test to verify database connection works
 * Run this after setting up your database in MySQL Workbench
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        
        // Test basic connection
        if (dbConn.testConnection()) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
            return;
        }
        
        // Test querying your enhanced schema
        try (Connection conn = dbConn.getAdminConnection()) {
            Statement stmt = conn.createStatement();
            
            // Test if your tables exist
            System.out.println("\n📋 Checking database tables...");
            
            String[] tables = {"employees", "address", "city", "state", "division", 
                             "job_titles", "role", "user_account", "pay_statement"};
            
            for (String table : tables) {
                try {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("✅ Table '" + table + "' exists with " + count + " records");
                    }
                    rs.close();
                } catch (Exception e) {
                    System.out.println("❌ Table '" + table + "' not found or error: " + e.getMessage());
                }
            }
            
            // Test a sample query
            System.out.println("\n👥 Sample employee data:");
            ResultSet rs = stmt.executeQuery(
                "SELECT e.empid, e.first_name, e.last_name, e.email " +
                "FROM employees e LIMIT 3"
            );
            
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s %s, Email: %s%n",
                    rs.getInt("empid"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            System.out.println("❌ Error testing database: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🎉 Database connection test completed!");
    }
}
