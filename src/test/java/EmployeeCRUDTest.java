import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Employee CRUD Operations Test (Task 3)
 * Tests Create, Read, Update, Delete functionality
 */
public class EmployeeCRUDTest {
    
    private static EmployeeDAO dao;
    private static EmployeeService service;
    private static User adminUser;
    private static User employeeUser;
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Task 3: Employee CRUD Operations...\n");
        
        try {
            setup();
            runAllCRUDTests();
            
            System.out.println("\n🎉 Task 3 CRUD Operations Test Completed Successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Task 3 test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void setup() throws Exception {
        System.out.println("📋 Setting up Task 3 test environment...");
        
        dao = new EmployeeDAO();
        service = new EmployeeService();
        
        // Create test users
        adminUser = new User("hr_admin", "hashedPassword", UserRole.ADMIN);
        adminUser.setUserId(1);
        adminUser.login();
        
        employeeUser = new User("john.smith", "hashedPassword", UserRole.EMPLOYEE, 1);
        employeeUser.setUserId(2);
        employeeUser.login();
        
        System.out.println("✅ Task 3 test environment ready\n");
    }
    
    private static void runAllCRUDTests() {
        System.out.println("📋 Running CRUD Operation Tests...\n");
        
        // C - Create
        testEmployeeCreation();
        
        // R - Read (Search)
        testEmployeeSearch();
        
        // U - Update
        testEmployeeUpdate();
        
        // D - Delete
        testEmployeeDeletion();
        
        // Additional validation tests
        testDataValidation();
        testRoleBasedAccess();
    }
    
    private static void testEmployeeCreation() {
        System.out.println("➕ Testing Employee Creation (CREATE)...");
        
        try {
            Employee newEmp = new Employee();
            newEmp.setFirstName("Alice");
            newEmp.setLastName("Johnson");
            newEmp.setEmail("alice.johnson@company.com");
            newEmp.setSsn("555-66-7777");
            newEmp.setDob(LocalDate.of(1988, 3, 20));
            newEmp.setHireDate(LocalDate.of(2023, 6, 1));
            newEmp.setBaseSalary(BigDecimal.valueOf(62000));
            
            System.out.println("   Creating employee: " + newEmp.getFullName());
            boolean result = dao.save(newEmp);
            
            if (result) {
                System.out.println("✅ Employee creation successful - ID: " + newEmp.getEmpid());
                System.out.println("   Employee details: " + newEmp.toString());
                
                // Store ID for cleanup
                createdEmployeeId = newEmp.getEmpid();
            } else {
                System.out.println("⚠️  Employee creation failed (may be expected without database connection)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Employee creation test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static int createdEmployeeId = -1;
    
    private static void testEmployeeSearch() {
        System.out.println("🔍 Testing Employee Search (READ)...");
        
        try {
            // Test 1: Search by ID
            System.out.println("   Testing search by ID...");
            SearchResult result = service.searchEmployeeById(1, adminUser);
            if (result.isSuccess()) {
                System.out.println("✅ Search by ID successful: " + result.getMessage());
                if (result.getCount() > 0) {
                    Employee emp = result.getEmployees().get(0);
                    System.out.println("   Found: " + emp.getFullName() + " - " + emp.getFormattedSalary());
                }
            } else {
                System.out.println("⚠️  Search by ID: " + result.getMessage());
            }
            
            // Test 2: Search by name
            System.out.println("   Testing search by name...");
            SearchResult nameResult = service.searchByName("John", "Smith", adminUser);
            if (nameResult.isSuccess()) {
                System.out.println("✅ Search by name successful: Found " + nameResult.getCount() + " employee(s)");
                for (Employee emp : nameResult.getEmployees()) {
                    System.out.println("   - " + emp.getFullName() + " (ID: " + emp.getEmpid() + ")");
                }
            } else {
                System.out.println("⚠️  Search by name: " + nameResult.getMessage());
            }
            
            // Test 3: Search all employees
            System.out.println("   Testing get all employees...");
            SearchResult allResult = service.getAllEmployees(adminUser);
            if (allResult.isSuccess()) {
                System.out.println("✅ Get all employees successful: Found " + allResult.getCount() + " employee(s)");
            } else {
                System.out.println("⚠️  Get all employees: " + allResult.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Employee search test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testEmployeeUpdate() {
        System.out.println("🔄 Testing Employee Update (UPDATE)...");
        
        try {
            Optional<Employee> empOpt = dao.findById(1);
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                BigDecimal originalSalary = emp.getBaseSalary();
                BigDecimal newSalary = BigDecimal.valueOf(68000);
                
                System.out.println("   Updating employee: " + emp.getFullName());
                System.out.println("   Original salary: " + emp.getFormattedSalary());
                System.out.println("   New salary: $" + String.format("%,.2f", newSalary));
                
                emp.setBaseSalary(newSalary);
                boolean result = dao.update(emp);
                
                if (result) {
                    System.out.println("✅ Employee update successful");
                    
                    // Verify the update
                    Optional<Employee> updatedEmp = dao.findById(1);
                    if (updatedEmp.isPresent() && updatedEmp.get().getBaseSalary().equals(newSalary)) {
                        System.out.println("✅ Update verification successful");
                    }
                    
                    // Restore original salary
                    emp.setBaseSalary(originalSalary);
                    dao.update(emp);
                    System.out.println("   Restored original salary");
                } else {
                    System.out.println("⚠️  Employee update failed");
                }
            } else {
                System.out.println("⚠️  No employee found with ID 1 for update test");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Employee update test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testEmployeeDeletion() {
        System.out.println("🗑️  Testing Employee Deletion (DELETE - Soft Delete)...");
        
        try {
            if (createdEmployeeId > 0) {
                System.out.println("   Deleting test employee with ID: " + createdEmployeeId);
                boolean deleteResult = dao.delete(createdEmployeeId);
                
                if (deleteResult) {
                    System.out.println("✅ Employee soft delete successful");
                    
                    // Verify deletion (employee should be marked inactive)
                    Optional<Employee> deletedEmp = dao.findById(createdEmployeeId);
                    if (deletedEmp.isEmpty()) {
                        System.out.println("✅ Employee properly removed from active queries");
                    } else if (!deletedEmp.get().isActive()) {
                        System.out.println("✅ Employee marked as inactive (soft delete)");
                    }
                } else {
                    System.out.println("⚠️  Employee deletion failed");
                }
            } else {
                // Create a test employee for deletion
                Employee testEmp = new Employee();
                testEmp.setFirstName("Delete");
                testEmp.setLastName("Me");
                testEmp.setEmail("delete.me@company.com");
                testEmp.setSsn("999-88-7777");
                testEmp.setDob(LocalDate.of(1990, 1, 1));
                testEmp.setHireDate(LocalDate.of(2023, 1, 1));
                testEmp.setBaseSalary(BigDecimal.valueOf(50000));
                
                if (dao.save(testEmp)) {
                    System.out.println("   Created test employee for deletion: " + testEmp.getFullName());
                    boolean deleteResult = dao.delete(testEmp.getEmpid());
                    if (deleteResult) {
                        System.out.println("✅ Employee soft delete successful");
                    } else {
                        System.out.println("⚠️  Employee deletion failed");
                    }
                } else {
                    System.out.println("⚠️  Could not create test employee for deletion");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Employee deletion test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testDataValidation() {
        System.out.println("🔒 Testing Data Validation...");
        
        try {
            Employee emp = new Employee();
            emp.setFirstName("Test");
            emp.setLastName("Validation");
            emp.setEmail("test@example.com");
            emp.setSsn("123-45-6789");
            emp.setDob(LocalDate.of(1990, 1, 1));
            emp.setHireDate(LocalDate.of(2020, 1, 1));
            
            // Test 1: Negative salary validation
            try {
                emp.setBaseSalary(BigDecimal.valueOf(-5000));
                System.out.println("❌ Negative salary should have been rejected");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Negative salary correctly rejected: " + e.getMessage());
            }
            
            // Test 2: Invalid email validation
            try {
                emp.setEmail("invalid-email");
                System.out.println("❌ Invalid email should have been rejected");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Invalid email correctly rejected: " + e.getMessage());
            }
            
            // Test 3: Invalid SSN validation
            try {
                emp.setSsn("invalid-ssn");
                System.out.println("❌ Invalid SSN should have been rejected");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Invalid SSN correctly rejected: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Data validation test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testRoleBasedAccess() {
        System.out.println("🔐 Testing Role-Based Access Control...");
        
        try {
            // Test 1: Employee trying to access other employee's data
            SearchResult result = service.searchEmployeeById(999, employeeUser);
            if (!result.isSuccess() && result.getMessage().contains("You can only view")) {
                System.out.println("✅ Employee access properly restricted: " + result.getMessage());
            } else {
                System.out.println("⚠️  Employee access control may need review");
            }
            
            // Test 2: Employee accessing own data
            SearchResult ownResult = service.searchEmployeeById(1, employeeUser);
            if (ownResult.isSuccess()) {
                System.out.println("✅ Employee can access own data: " + ownResult.getMessage());
            } else {
                System.out.println("⚠️  Employee cannot access own data: " + ownResult.getMessage());
            }
            
            // Test 3: Admin accessing any data
            SearchResult adminResult = service.searchEmployeeById(1, adminUser);
            if (adminResult.isSuccess()) {
                System.out.println("✅ Admin can access any employee data");
            } else {
                System.out.println("⚠️  Admin access issue: " + adminResult.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Role-based access test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
}
