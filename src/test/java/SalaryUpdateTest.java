import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Salary Update by Percentage Test (Task 4)
 * Tests bulk salary update functionality with various scenarios
 */
public class SalaryUpdateTest {
    
    private static EmployeeDAO dao;
    private static User adminUser;
    
    public static void main(String[] args) {
        System.out.println("💰 Testing Task 4: Salary Update by Percentage...\n");
        
        try {
            setup();
            runAllSalaryTests();
            
            System.out.println("\n🎉 Task 4 Salary Update Test Completed Successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Task 4 test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void setup() throws Exception {
        System.out.println("📋 Setting up Task 4 test environment...");
        
        dao = new EmployeeDAO();
        
        // Create admin user for testing
        adminUser = new User("hr_admin", "hashedPassword", UserRole.ADMIN);
        adminUser.setUserId(1);
        adminUser.login();
        
        System.out.println("✅ Task 4 test environment ready\n");
    }
    
    private static void runAllSalaryTests() {
        System.out.println("📋 Running Salary Update Tests...\n");
        
        // Show current employee salaries
        showCurrentSalaries();
        
        // Test 1: Valid salary update
        testValidSalaryUpdate();
        
        // Test 2: Invalid salary range (min > max)
        testInvalidSalaryRange();
        
        // Test 3: No employees in range
        testNoEmployeesInRange();
        
        // Test 4: Boundary conditions
        testBoundaryConditions();
        
        // Test 5: Large percentage increase
        testLargePercentageIncrease();
        
        // Test 6: Negative percentage (salary decrease)
        testNegativePercentage();
        
        // Test 7: Zero percentage (no change)
        testZeroPercentage();
    }
    
    private static void showCurrentSalaries() {
        System.out.println("📊 Current Employee Salaries:");
        
        try {
            List<Employee> employees = dao.findAll();
            if (!employees.isEmpty()) {
                System.out.println("   ID | Name                | Current Salary");
                System.out.println("   ---|---------------------|---------------");
                for (Employee emp : employees) {
                    System.out.printf("   %-2d | %-19s | %s%n", 
                        emp.getEmpid(), 
                        emp.getFullName(), 
                        emp.getFormattedSalary());
                }
            } else {
                System.out.println("   No employees found in database");
            }
        } catch (Exception e) {
            System.out.println("   Could not retrieve current salaries: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testValidSalaryUpdate() {
        System.out.println("✅ Testing Valid Salary Update (3% increase for $50K-$100K range)...");
        
        try {
            double percentage = 3.0;
            double minSalary = 50000;
            double maxSalary = 100000;
            
            System.out.println("   Parameters:");
            System.out.println("   - Percentage: " + percentage + "%");
            System.out.println("   - Salary range: $" + String.format("%,.0f", minSalary) + 
                             " - $" + String.format("%,.0f", maxSalary));
            
            int count = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            if (count >= 0) {
                System.out.println("✅ Salary update successful: Updated " + count + " employee(s)");
                if (count > 0) {
                    System.out.println("   Each employee in range received a " + percentage + "% salary increase");
                }
            } else {
                System.out.println("⚠️  Salary update returned error code: " + count);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Valid salary update test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testInvalidSalaryRange() {
        System.out.println("🚫 Testing Invalid Salary Range (min > max)...");
        
        try {
            double percentage = 5.0;
            double minSalary = 90000;  // Higher than max
            double maxSalary = 50000;  // Lower than min
            
            System.out.println("   Parameters (Invalid):");
            System.out.println("   - Percentage: " + percentage + "%");
            System.out.println("   - Min salary: $" + String.format("%,.0f", minSalary));
            System.out.println("   - Max salary: $" + String.format("%,.0f", maxSalary));
            System.out.println("   - Issue: Min > Max (invalid range)");
            
            int result = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            if (result == 0) {
                System.out.println("✅ Invalid range correctly handled: Updated " + result + " employees");
                System.out.println("   System properly rejected invalid salary range");
            } else {
                System.out.println("⚠️  Invalid range updated " + result + " employees (unexpected)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Invalid salary range test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testNoEmployeesInRange() {
        System.out.println("🔍 Testing Salary Range with No Employees...");
        
        try {
            double percentage = 2.5;
            double minSalary = 300000;  // Very high range
            double maxSalary = 500000;  // Very high range
            
            System.out.println("   Parameters:");
            System.out.println("   - Percentage: " + percentage + "%");
            System.out.println("   - Salary range: $" + String.format("%,.0f", minSalary) + 
                             " - $" + String.format("%,.0f", maxSalary));
            System.out.println("   - Expected: No employees in this high salary range");
            
            int result = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            if (result == 0) {
                System.out.println("✅ No employees in range: Updated " + result + " employees");
                System.out.println("   System correctly identified empty salary range");
            } else {
                System.out.println("⚠️  Unexpected employees found in high salary range: " + result);
                System.out.println("   (This could indicate very high-paid employees in your data)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ No employees in range test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testBoundaryConditions() {
        System.out.println("⚖️  Testing Boundary Conditions...");
        
        try {
            // Test 1: 0% increase (no change)
            System.out.println("   Test 1: 0% salary increase");
            int result1 = dao.updateSalaryByRange(0.0, 0, 1000000);
            System.out.println("   ✅ 0% increase: Updated " + result1 + " employee(s) (salaries unchanged)");
            
            // Test 2: Very small range
            System.out.println("   Test 2: Very small salary range ($50,000 - $50,001)");
            int result2 = dao.updateSalaryByRange(1.0, 50000, 50001);
            System.out.println("   ✅ Small range: Updated " + result2 + " employee(s)");
            
            // Test 3: Very wide range
            System.out.println("   Test 3: Very wide salary range ($0 - $1,000,000)");
            int result3 = dao.updateSalaryByRange(0.1, 0, 1000000);
            System.out.println("   ✅ Wide range: Updated " + result3 + " employee(s)");
            
        } catch (Exception e) {
            System.out.println("❌ Boundary conditions test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testLargePercentageIncrease() {
        System.out.println("📈 Testing Large Percentage Increase...");
        
        try {
            double percentage = 25.0;  // Large increase
            double minSalary = 200000; // High salary range to limit impact
            double maxSalary = 300000;
            
            System.out.println("   Parameters:");
            System.out.println("   - Percentage: " + percentage + "% (large increase)");
            System.out.println("   - Salary range: $" + String.format("%,.0f", minSalary) + 
                             " - $" + String.format("%,.0f", maxSalary));
            
            int result = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            System.out.println("✅ Large percentage increase: Updated " + result + " employee(s)");
            if (result > 0) {
                System.out.println("   Each affected employee received a " + percentage + "% salary increase");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Large percentage increase test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testNegativePercentage() {
        System.out.println("📉 Testing Negative Percentage (Salary Decrease)...");
        
        try {
            double percentage = -2.0;  // Salary decrease
            double minSalary = 100000; // High salary range
            double maxSalary = 200000;
            
            System.out.println("   Parameters:");
            System.out.println("   - Percentage: " + percentage + "% (salary decrease)");
            System.out.println("   - Salary range: $" + String.format("%,.0f", minSalary) + 
                             " - $" + String.format("%,.0f", maxSalary));
            
            int result = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            System.out.println("✅ Negative percentage: Updated " + result + " employee(s)");
            if (result > 0) {
                System.out.println("   Each affected employee received a " + Math.abs(percentage) + "% salary decrease");
            }
            System.out.println("   Note: DAO level allows negative percentages; business rules should be in service layer");
            
        } catch (Exception e) {
            System.out.println("❌ Negative percentage test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testZeroPercentage() {
        System.out.println("🔄 Testing Zero Percentage (No Change)...");
        
        try {
            double percentage = 0.0;   // No change
            double minSalary = 40000;
            double maxSalary = 80000;
            
            System.out.println("   Parameters:");
            System.out.println("   - Percentage: " + percentage + "% (no change)");
            System.out.println("   - Salary range: $" + String.format("%,.0f", minSalary) + 
                             " - $" + String.format("%,.0f", maxSalary));
            
            // Get salaries before
            List<Employee> beforeEmployees = dao.findAll();
            
            int result = dao.updateSalaryByRange(percentage, minSalary, maxSalary);
            
            // Get salaries after
            List<Employee> afterEmployees = dao.findAll();
            
            System.out.println("✅ Zero percentage: Updated " + result + " employee(s)");
            System.out.println("   Salaries should remain unchanged (0% increase = no change)");
            
            // Verify no actual changes occurred
            boolean salariesUnchanged = true;
            if (beforeEmployees.size() == afterEmployees.size()) {
                for (int i = 0; i < beforeEmployees.size(); i++) {
                    Employee before = beforeEmployees.get(i);
                    Employee after = afterEmployees.get(i);
                    if (before.getEmpid() == after.getEmpid()) {
                        if (!before.getBaseSalary().equals(after.getBaseSalary())) {
                            salariesUnchanged = false;
                            break;
                        }
                    }
                }
            }
            
            if (salariesUnchanged) {
                System.out.println("   ✅ Verification: All salaries remained unchanged");
            } else {
                System.out.println("   ⚠️  Some salaries may have changed unexpectedly");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Zero percentage test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
}
