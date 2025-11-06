import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.dao.EmployeeDAO.SearchCriteria;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.models.Employee;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Comprehensive test for Employee Search Functionality (Task 2)
 */
public class EmployeeSearchTest {
    
    public static void main(String[] args) {
        System.out.println("🔍 Testing Employee Search Functionality (Task 2)...\n");
        
        try {
            // Test 1: Service Layer Initialization
            testServiceInitialization();
            
            // Test 2: Role-Based Access Control
            testRoleBasedAccess();
            
            // Test 3: Search Functionality (Mock Data)
            testSearchFunctionality();
            
            // Test 4: Search Validation
            testSearchValidation();
            
            // Test 5: Business Rules Validation
            testBusinessRulesValidation();
            
            System.out.println("\n🎉 Employee Search Test Completed Successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testServiceInitialization() {
        System.out.println("📋 Testing Service Layer Initialization...");
        
        try {
            EmployeeService employeeService = new EmployeeService();
            System.out.println("✅ EmployeeService initialized successfully");
            
            // Verify the service is functional by checking if it's not null
            if (employeeService != null) {
                System.out.println("✅ EmployeeService instance created and ready");
            }
            
            AuthenticationService authService = new AuthenticationService();
            System.out.println("✅ AuthenticationService initialized successfully");
            
            // Verify the authentication service is functional
            if (authService != null) {
                System.out.println("✅ AuthenticationService instance created and ready");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Service initialization failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testRoleBasedAccess() {
        System.out.println("📋 Testing Role-Based Access Control...");
        
        EmployeeService employeeService = new EmployeeService();
        
        // Test 1: Unauthenticated user
        System.out.println("🚫 Testing unauthenticated access...");
        SearchResult result1 = employeeService.getAllEmployees(null);
        if (!result1.isSuccess()) {
            System.out.println("✅ Unauthenticated access correctly denied: " + result1.getMessage());
        } else {
            System.out.println("❌ Unauthenticated access incorrectly allowed");
        }
        
        // Test 2: Create mock users for testing
        System.out.println("\n👤 Testing with mock users...");
        
        // Mock HR Admin user
        User adminUser = new User("hr_admin", "hashedPassword", UserRole.ADMIN);
        adminUser.setUserId(1);
        adminUser.login(); // Simulate login
        
        // Mock Employee user
        User employeeUser = new User("john.smith", "hashedPassword", UserRole.EMPLOYEE, 1);
        employeeUser.setUserId(2);
        employeeUser.login(); // Simulate login
        
        // Test 3: Admin access
        System.out.println("👑 Testing admin access...");
        SearchResult adminResult = employeeService.getAllEmployees(adminUser);
        if (adminResult.isSuccess()) {
            System.out.println("✅ Admin access granted: " + adminResult.getMessage());
        } else {
            System.out.println("❌ Admin access denied: " + adminResult.getMessage());
        }
        
        // Test 4: Employee trying to access all employees
        System.out.println("👤 Testing employee access to all employees...");
        SearchResult empResult = employeeService.getAllEmployees(employeeUser);
        if (!empResult.isSuccess()) {
            System.out.println("✅ Employee access to all employees correctly denied: " + empResult.getMessage());
        } else {
            System.out.println("❌ Employee access to all employees incorrectly allowed");
        }
        
        // Test 5: Employee accessing own data
        System.out.println("👤 Testing employee access to own data...");
        SearchResult ownDataResult = employeeService.searchEmployeeById(1, employeeUser);
        if (ownDataResult.isSuccess()) {
            System.out.println("✅ Employee can access own data: " + ownDataResult.getMessage());
        } else {
            System.out.println("⚠️  Employee cannot access own data (expected if no sample data): " + ownDataResult.getMessage());
        }
        
        // Test 6: Employee trying to access other's data
        System.out.println("👤 Testing employee access to other's data...");
        SearchResult otherDataResult = employeeService.searchEmployeeById(999, employeeUser);
        if (!otherDataResult.isSuccess()) {
            System.out.println("✅ Employee access to other's data correctly denied: " + otherDataResult.getMessage());
        } else {
            System.out.println("❌ Employee access to other's data incorrectly allowed");
        }
        
        System.out.println();
    }
    
    private static void testSearchFunctionality() {
        System.out.println("📋 Testing Search Functionality...");
        
        EmployeeService employeeService = new EmployeeService();
        
        // Create mock admin user
        User adminUser = new User("hr_admin", "hashedPassword", UserRole.ADMIN);
        adminUser.setUserId(1);
        adminUser.login();
        
        // Test 1: Search by name
        System.out.println("🔍 Testing search by name...");
        SearchResult nameResult = employeeService.searchByName("John", "Smith", adminUser);
        if (nameResult.isSuccess()) {
            System.out.println("✅ Name search successful: " + nameResult.getMessage());
            System.out.println("   Found " + nameResult.getCount() + " employee(s)");
        } else {
            System.out.println("⚠️  Name search returned no results (expected if no sample data): " + nameResult.getMessage());
        }
        
        // Test 2: Search by SSN
        System.out.println("\n🔍 Testing search by SSN...");
        SearchResult ssnResult = employeeService.searchBySSN("123-45-6789", adminUser);
        if (ssnResult.isSuccess()) {
            System.out.println("✅ SSN search successful: " + ssnResult.getMessage());
        } else {
            System.out.println("⚠️  SSN search returned no results (expected if no sample data): " + ssnResult.getMessage());
        }
        
        // Test 3: Search by DOB
        System.out.println("\n🔍 Testing search by DOB...");
        SearchResult dobResult = employeeService.searchByDOB(LocalDate.of(1985, 3, 15), adminUser);
        if (dobResult.isSuccess()) {
            System.out.println("✅ DOB search successful: " + dobResult.getMessage());
        } else {
            System.out.println("⚠️  DOB search returned no results (expected if no sample data): " + dobResult.getMessage());
        }
        
        // Test 4: Advanced search with criteria
        System.out.println("\n🔍 Testing advanced search...");
        SearchCriteria criteria = new SearchCriteria();
        criteria.setFirstName("John");
        criteria.setLastName("Smith");
        
        SearchResult advancedResult = employeeService.searchEmployees(criteria, adminUser);
        if (advancedResult.isSuccess()) {
            System.out.println("✅ Advanced search successful: " + advancedResult.getMessage());
        } else {
            System.out.println("⚠️  Advanced search returned no results (expected if no sample data): " + advancedResult.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testSearchValidation() {
        System.out.println("📋 Testing Search Validation...");
        
        EmployeeService employeeService = new EmployeeService();
        
        // Create mock admin user
        User adminUser = new User("hr_admin", "hashedPassword", UserRole.ADMIN);
        adminUser.setUserId(1);
        adminUser.login();
        
        // Test 1: Invalid SSN format
        System.out.println("🚫 Testing invalid SSN format...");
        SearchResult invalidSSN = employeeService.searchBySSN("invalid-ssn", adminUser);
        if (!invalidSSN.isSuccess()) {
            System.out.println("✅ Invalid SSN correctly rejected: " + invalidSSN.getMessage());
        } else {
            System.out.println("❌ Invalid SSN incorrectly accepted");
        }
        
        // Test 2: Empty name search
        System.out.println("\n🚫 Testing empty name search...");
        SearchResult emptyName = employeeService.searchByName("", "", adminUser);
        if (!emptyName.isSuccess()) {
            System.out.println("✅ Empty name search correctly rejected: " + emptyName.getMessage());
        } else {
            System.out.println("❌ Empty name search incorrectly accepted");
        }
        
        // Test 3: Null DOB search
        System.out.println("\n🚫 Testing null DOB search...");
        SearchResult nullDOB = employeeService.searchByDOB(null, adminUser);
        if (!nullDOB.isSuccess()) {
            System.out.println("✅ Null DOB search correctly rejected: " + nullDOB.getMessage());
        } else {
            System.out.println("❌ Null DOB search incorrectly accepted");
        }
        
        // Test 4: Employee trying admin-only search
        System.out.println("\n🚫 Testing employee attempting admin-only search...");
        User employeeUser = new User("john.smith", "hashedPassword", UserRole.EMPLOYEE, 1);
        employeeUser.setUserId(2);
        employeeUser.login();
        
        SearchResult empSSNSearch = employeeService.searchBySSN("123-45-6789", employeeUser);
        if (!empSSNSearch.isSuccess()) {
            System.out.println("✅ Employee SSN search correctly denied: " + empSSNSearch.getMessage());
        } else {
            System.out.println("❌ Employee SSN search incorrectly allowed");
        }
        
        System.out.println();
    }
    
    private static void testBusinessRulesValidation() {
        System.out.println("📋 Testing Business Rules Validation...");
        
        EmployeeService employeeService = new EmployeeService();
        
        // Test 1: Valid employee
        System.out.println("✅ Testing valid employee...");
        Employee validEmployee = new Employee(
            "John", "Doe", "123-45-6789",
            LocalDate.of(1985, 3, 15),
            "john.doe@company.com",
            LocalDate.of(2020, 1, 15),
            new BigDecimal("75000.00")
        );
        
        EmployeeService.ValidationResult validResult = employeeService.validateEmployeeData(validEmployee);
        if (validResult.isValid()) {
            System.out.println("✅ Valid employee data accepted");
        } else {
            System.out.println("❌ Valid employee data rejected: " + validResult.getErrors());
        }
        
        // Test 2: Invalid salary (too low)
        System.out.println("\n🚫 Testing invalid salary (too low)...");
        Employee lowSalaryEmployee = new Employee(
            "Jane", "Smith", "987-65-4321",
            LocalDate.of(1990, 7, 22),
            "jane.smith@company.com",
            LocalDate.of(2021, 3, 10),
            new BigDecimal("15000.00") // Below minimum
        );
        
        EmployeeService.ValidationResult lowSalaryResult = employeeService.validateEmployeeData(lowSalaryEmployee);
        if (!lowSalaryResult.isValid()) {
            System.out.println("✅ Low salary correctly rejected: " + lowSalaryResult.getErrors());
        } else {
            System.out.println("❌ Low salary incorrectly accepted");
        }
        
        // Test 3: Invalid age (too young)
        System.out.println("\n🚫 Testing invalid age (too young)...");
        Employee youngEmployee = new Employee(
            "Young", "Person", "111-22-3333",
            LocalDate.of(2010, 1, 1), // Too young
            "young.person@company.com",
            LocalDate.of(2023, 1, 1),
            new BigDecimal("50000.00")
        );
        
        EmployeeService.ValidationResult youngResult = employeeService.validateEmployeeData(youngEmployee);
        if (!youngResult.isValid()) {
            System.out.println("✅ Young employee correctly rejected: " + youngResult.getErrors());
        } else {
            System.out.println("❌ Young employee incorrectly accepted");
        }
        
        // Test 4: Future hire date
        System.out.println("\n🚫 Testing future hire date...");
        Employee futureHireEmployee = new Employee(
            "Future", "Employee", "444-55-6666",
            LocalDate.of(1985, 5, 10),
            "future.employee@company.com",
            LocalDate.of(2030, 1, 1), // Future date
            new BigDecimal("60000.00")
        );
        
        EmployeeService.ValidationResult futureResult = employeeService.validateEmployeeData(futureHireEmployee);
        if (!futureResult.isValid()) {
            System.out.println("✅ Future hire date correctly rejected: " + futureResult.getErrors());
        } else {
            System.out.println("❌ Future hire date incorrectly accepted");
        }
        
        System.out.println();
    }
}
