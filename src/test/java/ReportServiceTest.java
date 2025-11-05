import com.employeemgmt.dao.DatabaseConnection;
import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.models.Payroll;
import com.employeemgmt.models.User;
import com.employeemgmt.services.ReportService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Test Cases for ReportService
 * 
 * Tests report generation with role-based access control:
 * - Pay statement history (with access control)
 * - Total pay by job title (HR Admin only)
 * - Total pay by division (HR Admin only)
 * - Employees hired report (HR Admin only)
 * - Security: Verify unauthorized access is denied
 */
public class ReportServiceTest {
    
    private static DatabaseConnection dbConnection;
    private static PayrollDAO payrollDAO;
    private static ReportService reportService;
    
    // Test users
    private static User hrAdminUser;
    private static User generalEmployeeUser;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("REPORT SERVICE TEST SUITE");
        System.out.println("=".repeat(80));
        
        try {
            // Initialize database connection and services
            dbConnection = DatabaseConnection.getInstance();
            payrollDAO = new PayrollDAO(dbConnection);
            reportService = new ReportService(payrollDAO);
            
            // Test database connection first
            if (!dbConnection.testConnection()) {
                System.out.println("❌ Database connection failed! Cannot run tests.");
                return;
            }
            
            System.out.println("✅ Database connection successful!\n");
            
            // Create test users
            setupTestUsers();
            
            // Run test cases
            testPayStatementHistoryAccess();
            testTotalPayByJobTitleAccess();
            testTotalPayByDivisionAccess();
            testEmployeesHiredReportAccess();
            testReportFormatting();
            testSecurityRestrictions();
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ ALL REPORT SERVICE TESTS COMPLETED");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("❌ Test suite failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup test users for role-based access testing
     */
    private static void setupTestUsers() {
        // HR Admin user (can access all reports)
        hrAdminUser = new User();
        hrAdminUser.setUserId(1);
        hrAdminUser.setUsername("hr_admin");
        hrAdminUser.setRole(User.UserRole.HR_ADMIN);
        hrAdminUser.setActive(true);
        
        // General Employee user (limited access)
        generalEmployeeUser = new User();
        generalEmployeeUser.setUserId(2);
        generalEmployeeUser.setUsername("john.smith");
        generalEmployeeUser.setRole(User.UserRole.GENERAL_EMPLOYEE);
        generalEmployeeUser.setEmpid(1); // Employee ID 1
        generalEmployeeUser.setActive(true);
        
        System.out.println("✅ Test users created:");
        System.out.println("   - HR Admin: " + hrAdminUser.getUsername());
        System.out.println("   - General Employee: " + generalEmployeeUser.getUsername() + " (Employee ID: " + generalEmployeeUser.getEmpid() + ")\n");
    }
    
    /**
     * Test: Pay statement history with role-based access
     */
    private static void testPayStatementHistoryAccess() {
        System.out.println("\n📋 Test 1: Pay Statement History Access Control");
        System.out.println("-".repeat(80));
        
        try {
            int testEmployeeId = 1;
            
            // Test 1a: HR Admin accessing any employee's pay statements
            System.out.println("   Test 1a: HR Admin accessing employee " + testEmployeeId + " pay statements");
            List<Payroll> adminPayStatements = reportService.generatePayStatementHistory(testEmployeeId, hrAdminUser);
            System.out.println("   ✅ PASSED: HR Admin can access employee " + testEmployeeId + " pay statements");
            System.out.println("      Retrieved " + adminPayStatements.size() + " pay statement(s)");
            
            // Test 1b: General Employee accessing their own pay statements
            System.out.println("\n   Test 1b: General Employee accessing their own pay statements");
            List<Payroll> employeePayStatements = reportService.generatePayStatementHistory(
                    generalEmployeeUser.getEmpid(), generalEmployeeUser);
            System.out.println("   ✅ PASSED: General Employee can access their own pay statements");
            System.out.println("      Retrieved " + employeePayStatements.size() + " pay statement(s)");
            
            // Test 1c: General Employee trying to access another employee's pay statements
            System.out.println("\n   Test 1c: General Employee trying to access another employee's pay statements");
            try {
                reportService.generatePayStatementHistory(2, generalEmployeeUser);
                System.out.println("   ❌ FAILED: Security breach - General Employee accessed another employee's data!");
            } catch (SecurityException e) {
                System.out.println("   ✅ PASSED: Security exception thrown as expected");
                System.out.println("      Message: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Total pay by job title with role-based access
     */
    private static void testTotalPayByJobTitleAccess() {
        System.out.println("\n📋 Test 2: Total Pay By Job Title Access Control");
        System.out.println("-".repeat(80));
        
        try {
            int month = 10;
            int year = 2024;
            
            // Test 2a: HR Admin accessing the report
            System.out.println("   Test 2a: HR Admin accessing total pay by job title report");
            Map<String, Double> adminReport = reportService.generateTotalPayByJobTitle(month, year, hrAdminUser);
            System.out.println("   ✅ PASSED: HR Admin can access total pay by job title report");
            System.out.println("      Retrieved data for " + adminReport.size() + " job title(s)");
            
            // Test 2b: General Employee trying to access the report
            System.out.println("\n   Test 2b: General Employee trying to access total pay by job title report");
            try {
                reportService.generateTotalPayByJobTitle(month, year, generalEmployeeUser);
                System.out.println("   ❌ FAILED: Security breach - General Employee accessed HR Admin report!");
            } catch (SecurityException e) {
                System.out.println("   ✅ PASSED: Security exception thrown as expected");
                System.out.println("      Message: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Total pay by division with role-based access
     */
    private static void testTotalPayByDivisionAccess() {
        System.out.println("\n📋 Test 3: Total Pay By Division Access Control");
        System.out.println("-".repeat(80));
        
        try {
            int month = 10;
            int year = 2024;
            
            // Test 3a: HR Admin accessing the report
            System.out.println("   Test 3a: HR Admin accessing total pay by division report");
            Map<String, Double> adminReport = reportService.generateTotalPayByDivision(month, year, hrAdminUser);
            System.out.println("   ✅ PASSED: HR Admin can access total pay by division report");
            System.out.println("      Retrieved data for " + adminReport.size() + " division(s)");
            
            // Test 3b: General Employee trying to access the report
            System.out.println("\n   Test 3b: General Employee trying to access total pay by division report");
            try {
                reportService.generateTotalPayByDivision(month, year, generalEmployeeUser);
                System.out.println("   ❌ FAILED: Security breach - General Employee accessed HR Admin report!");
            } catch (SecurityException e) {
                System.out.println("   ✅ PASSED: Security exception thrown as expected");
                System.out.println("      Message: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Employees hired report with role-based access
     */
    private static void testEmployeesHiredReportAccess() {
        System.out.println("\n📋 Test 4: Employees Hired Report Access Control");
        System.out.println("-".repeat(80));
        
        try {
            LocalDate startDate = LocalDate.of(2020, 1, 1);
            LocalDate endDate = LocalDate.of(2022, 12, 31);
            
            // Test 4a: HR Admin accessing the report
            System.out.println("   Test 4a: HR Admin accessing employees hired report");
            List<Integer> adminReport = reportService.generateEmployeesHiredReport(startDate, endDate, hrAdminUser);
            System.out.println("   ✅ PASSED: HR Admin can access employees hired report");
            System.out.println("      Found " + adminReport.size() + " employee(s) hired in the date range");
            
            // Test 4b: General Employee trying to access the report
            System.out.println("\n   Test 4b: General Employee trying to access employees hired report");
            try {
                reportService.generateEmployeesHiredReport(startDate, endDate, generalEmployeeUser);
                System.out.println("   ❌ FAILED: Security breach - General Employee accessed HR Admin report!");
            } catch (SecurityException e) {
                System.out.println("   ✅ PASSED: Security exception thrown as expected");
                System.out.println("      Message: " + e.getMessage());
            }
            
            // Test 4c: Invalid date range
            System.out.println("\n   Test 4c: Invalid date range (start after end)");
            try {
                reportService.generateEmployeesHiredReport(endDate, startDate, hrAdminUser);
                System.out.println("   ❌ FAILED: Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✅ PASSED: IllegalArgumentException thrown as expected");
                System.out.println("      Message: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Report formatting methods
     */
    private static void testReportFormatting() {
        System.out.println("\n📋 Test 5: Report Formatting");
        System.out.println("-".repeat(80));
        
        try {
            // Test 5a: Format pay statement history
            System.out.println("   Test 5a: Format pay statement history report");
            List<Payroll> payStatements = reportService.generatePayStatementHistory(1, hrAdminUser);
            String formattedReport = reportService.formatPayStatementHistoryReport(payStatements);
            System.out.println("   ✅ PASSED: Pay statement history report formatted successfully");
            System.out.println("   Report preview (first 3 lines):");
            String[] lines = formattedReport.split("\n");
            for (int i = 0; i < Math.min(3, lines.length); i++) {
                System.out.println("      " + lines[i]);
            }
            
            // Test 5b: Format total pay by job title
            System.out.println("\n   Test 5b: Format total pay by job title report");
            Map<String, Double> jobTitleReport = reportService.generateTotalPayByJobTitle(10, 2024, hrAdminUser);
            String formattedJobTitle = reportService.formatTotalPayByJobTitleReport(jobTitleReport, 10, 2024);
            System.out.println("   ✅ PASSED: Total pay by job title report formatted successfully");
            System.out.println("   Report preview (first 3 lines):");
            String[] jobTitleLines = formattedJobTitle.split("\n");
            for (int i = 0; i < Math.min(3, jobTitleLines.length); i++) {
                System.out.println("      " + jobTitleLines[i]);
            }
            
            // Test 5c: Format total pay by division
            System.out.println("\n   Test 5c: Format total pay by division report");
            Map<String, Double> divisionReport = reportService.generateTotalPayByDivision(10, 2024, hrAdminUser);
            String formattedDivision = reportService.formatTotalPayByDivisionReport(divisionReport, 10, 2024);
            System.out.println("   ✅ PASSED: Total pay by division report formatted successfully");
            System.out.println("   Report preview (first 3 lines):");
            String[] divisionLines = formattedDivision.split("\n");
            for (int i = 0; i < Math.min(3, divisionLines.length); i++) {
                System.out.println("      " + divisionLines[i]);
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Additional security restrictions
     */
    private static void testSecurityRestrictions() {
        System.out.println("\n📋 Test 6: Additional Security Restrictions");
        System.out.println("-".repeat(80));
        
        try {
            // Test 6a: Null user
            System.out.println("   Test 6a: Null user attempting to access report");
            try {
                reportService.generatePayStatementHistory(1, null);
                System.out.println("   ❌ FAILED: Should have thrown SecurityException for null user");
            } catch (SecurityException e) {
                System.out.println("   ✅ PASSED: SecurityException thrown for null user");
            } catch (Exception e) {
                System.out.println("   ⚠️  Different exception thrown: " + e.getClass().getSimpleName());
            }
            
            // Test 6b: Invalid month range
            System.out.println("\n   Test 6b: Invalid month range (month = 0)");
            try {
                reportService.generateTotalPayByJobTitle(0, 2024, hrAdminUser);
                System.out.println("   ❌ FAILED: Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✅ PASSED: IllegalArgumentException thrown as expected");
            }
            
            // Test 6c: Invalid month range (month = 13)
            System.out.println("\n   Test 6c: Invalid month range (month = 13)");
            try {
                reportService.generateTotalPayByJobTitle(13, 2024, hrAdminUser);
                System.out.println("   ❌ FAILED: Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✅ PASSED: IllegalArgumentException thrown as expected");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ FAILED: Exception occurred - " + e.getMessage());
            e.printStackTrace();
        }
    }
}

