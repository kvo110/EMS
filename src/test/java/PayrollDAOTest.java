import com.employeemgmt.dao.DatabaseConnection;
import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.models.Payroll;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Test Cases for PayrollDAO

 */
public class PayrollDAOTest {
    
    private static DatabaseConnection dbConnection;
    private static PayrollDAO payrollDAO;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("PAYROLL DAO TEST SUITE");
        System.out.println("=".repeat(80));
        
        try {
            // Initialize database connection and DAO
            dbConnection = DatabaseConnection.getInstance();
            payrollDAO = new PayrollDAO(dbConnection);
            
            // Test database connection first
            if (!dbConnection.testConnection()) {
                System.out.println("❌ Database connection failed! Cannot run tests.");
                return;
            }
            
            System.out.println("✅ Database connection successful!\n");
            
            // Run test cases
            testGetPayStatementHistory();
            testGetTotalPayByJobTitle();
            testGetTotalPayByDivision();
            testGetEmployeesHiredInDateRange();
            testSaveAndUpdatePayroll();
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ ALL PAYROLL DAO TESTS COMPLETED");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("❌ Test suite failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Get pay statement history for an employee
     */
    private static void testGetPayStatementHistory() {
        System.out.println("\n📋 Test 1: Get Pay Statement History");
        System.out.println("-".repeat(80));
        
        try {
            int testEmployeeId = 1; // Using sample employee ID 1
            
            List<Payroll> payStatements = payrollDAO.getPayStatementHistory(testEmployeeId);
            
            if (payStatements == null) {
                System.out.println("❌ FAILED: Pay statement list is null");
                return;
            }
            
            System.out.println("✅ PASSED: Retrieved " + payStatements.size() + " pay statement(s) for employee ID " + testEmployeeId);
            
            if (!payStatements.isEmpty()) {
                System.out.println("\n   Pay Statement Details:");
                for (Payroll payroll : payStatements) {
                    System.out.println(String.format("   - ID: %d, Date: %s, Gross: $%.2f, Taxes: $%.2f, Net: $%.2f",
                            payroll.getId(),
                            payroll.getPayDate(),
                            payroll.getGross().doubleValue(),
                            payroll.getTaxes().doubleValue(),
                            payroll.getNet().doubleValue()));
                }
                
                // Verify sorting (most recent first)
                if (payStatements.size() > 1) {
                    LocalDate previousDate = payStatements.get(0).getPayDate();
                    boolean sorted = true;
                    for (int i = 1; i < payStatements.size(); i++) {
                        LocalDate currentDate = payStatements.get(i).getPayDate();
                        if (currentDate.isAfter(previousDate)) {
                            sorted = false;
                            break;
                        }
                        previousDate = currentDate;
                    }
                    if (sorted) {
                        System.out.println("✅ PASSED: Pay statements are sorted by most recent date first");
                    } else {
                        System.out.println("❌ FAILED: Pay statements are not sorted correctly");
                    }
                }
            } else {
                System.out.println("⚠️  WARNING: No pay statements found for employee ID " + testEmployeeId);
            }
            
        } catch (Exception e) {
            System.out.println("❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Get total pay by job title report
     */
    private static void testGetTotalPayByJobTitle() {
        System.out.println("\n📋 Test 2: Get Total Pay By Job Title");
        System.out.println("-".repeat(80));
        
        try {
            int month = 10; // October
            int year = 2024;
            
            Map<String, Double> report = payrollDAO.getTotalPayByJobTitle(month, year);
            
            if (report == null) {
                System.out.println("❌ FAILED: Report map is null");
                return;
            }
            
            System.out.println("✅ PASSED: Retrieved total pay by job title report for " + getMonthName(month) + " " + year);
            System.out.println("\n   Report Details:");
            
            double grandTotal = 0;
            for (Map.Entry<String, Double> entry : report.entrySet()) {
                System.out.println(String.format("   - %s: $%.2f", entry.getKey(), entry.getValue()));
                grandTotal += entry.getValue();
            }
            
            System.out.println(String.format("\n   Grand Total: $%.2f", grandTotal));
            
            if (report.isEmpty()) {
                System.out.println("⚠️  WARNING: Report is empty (no pay data for this month/year)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Get total pay by division report
     */
    private static void testGetTotalPayByDivision() {
        System.out.println("\n📋 Test 3: Get Total Pay By Division");
        System.out.println("-".repeat(80));
        
        try {
            int month = 10; // October
            int year = 2024;
            
            Map<String, Double> report = payrollDAO.getTotalPayByDivision(month, year);
            
            if (report == null) {
                System.out.println("❌ FAILED: Report map is null");
                return;
            }
            
            System.out.println("✅ PASSED: Retrieved total pay by division report for " + getMonthName(month) + " " + year);
            System.out.println("\n   Report Details:");
            
            double grandTotal = 0;
            for (Map.Entry<String, Double> entry : report.entrySet()) {
                System.out.println(String.format("   - %s: $%.2f", entry.getKey(), entry.getValue()));
                grandTotal += entry.getValue();
            }
            
            System.out.println(String.format("\n   Grand Total: $%.2f", grandTotal));
            
            if (report.isEmpty()) {
                System.out.println("⚠️  WARNING: Report is empty (no pay data for this month/year)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Get employees hired in date range
     */
    private static void testGetEmployeesHiredInDateRange() {
        System.out.println("\n📋 Test 4: Get Employees Hired In Date Range");
        System.out.println("-".repeat(80));
        
        try {
            LocalDate startDate = LocalDate.of(2020, 1, 1);
            LocalDate endDate = LocalDate.of(2022, 12, 31);
            
            List<Integer> employeeIds = payrollDAO.getEmployeesHiredInDateRange(startDate, endDate);
            
            if (employeeIds == null) {
                System.out.println("❌ FAILED: Employee ID list is null");
                return;
            }
            
            System.out.println("✅ PASSED: Retrieved employees hired between " + startDate + " and " + endDate);
            System.out.println("   Total employees hired: " + employeeIds.size());
            
            if (!employeeIds.isEmpty()) {
                System.out.println("   Employee IDs: " + employeeIds);
            } else {
                System.out.println("⚠️  WARNING: No employees found in this date range");
            }
            
        } catch (Exception e) {
            System.out.println("❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Save and update payroll records
     */
    private static void testSaveAndUpdatePayroll() {
        System.out.println("\n📋 Test 5: Save and Update Payroll Records");
        System.out.println("-".repeat(80));
        
        try {
            // Test save operation
            int testEmployeeId = 1;
            LocalDate payDate = LocalDate.now();
            BigDecimal gross = new BigDecimal("5000.00");
            BigDecimal taxes = new BigDecimal("1200.00");
            
            Payroll newPayroll = new Payroll(testEmployeeId, payDate, gross, taxes);
            long savedId = payrollDAO.save(newPayroll);
            
            if (savedId > 0) {
                System.out.println("✅ PASSED: Successfully saved new payroll record with ID: " + savedId);
                
                // Test update operation
                newPayroll.setGross(new BigDecimal("5500.00"));
                newPayroll.setTaxes(new BigDecimal("1320.00"));
                newPayroll.recalculateNetPay();
                
                boolean updated = payrollDAO.update(newPayroll);
                
                if (updated) {
                    System.out.println("✅ PASSED: Successfully updated payroll record");
                    
                    // Verify update
                    Payroll retrieved = payrollDAO.findById(savedId);
                    if (retrieved != null && retrieved.getGross().compareTo(new BigDecimal("5500.00")) == 0) {
                        System.out.println("✅ PASSED: Update verified - gross pay is now $5500.00");
                    } else {
                        System.out.println("❌ FAILED: Update verification failed");
                    }
                } else {
                    System.out.println("❌ FAILED: Update operation returned false");
                }
            } else {
                System.out.println("❌ FAILED: Save operation returned invalid ID");
            }
            
        } catch (Exception e) {
            System.out.println("❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get month name from month number
     */
    private static String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        
        if (month >= 1 && month <= 12) {
            return monthNames[month - 1];
        }
        
        return "Unknown";
    }
}

