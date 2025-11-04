import com.employeemgmt.models.Employee;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Simple test for the updated Employee model
 */
public class EmployeeModelTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Employee Model...");
        
        try {
            // Test 1: Create employee with core fields
            Employee emp1 = new Employee(
                "John", "Smith", "123-45-6789", 
                LocalDate.of(1985, 3, 15),
                "john.smith@companyz.com",
                LocalDate.of(2020, 1, 15),
                new BigDecimal("75000.00")
            );
            
            System.out.println("✅ Employee created: " + emp1);
            System.out.println("   Full Name: " + emp1.getFullName());
            System.out.println("   Age: " + emp1.getAge());
            System.out.println("   Years of Service: " + emp1.getYearsOfService());
            System.out.println("   Formatted Salary: " + emp1.getFormattedSalary());
            System.out.println("   Is Valid: " + emp1.isValid());
            
            // Test 2: Create employee with full constructor
            Employee emp2 = new Employee(
                2, "Jane", "Doe", "987-65-4321", 
                LocalDate.of(1990, 7, 22),
                "jane.doe@companyz.com",
                LocalDate.of(2021, 3, 10),
                new BigDecimal("82000.00"),
                true, "456 Oak Ave", 2, "94102", "F", "Hispanic", "555-0102"
            );
            
            System.out.println("\n✅ Full Employee created: " + emp2);
            System.out.println("   Address: " + emp2.getStreet() + ", " + emp2.getZip());
            System.out.println("   Gender: " + emp2.getGender());
            System.out.println("   Race: " + emp2.getIdentifiedRace());
            System.out.println("   Mobile: " + emp2.getMobilePhone());
            
            // Test 3: Validation
            System.out.println("\n📋 Testing validation...");
            
            try {
                emp1.setFirstName(""); // Should throw exception
                System.out.println("❌ Validation failed - empty name allowed");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validation works: " + e.getMessage());
            }
            
            try {
                emp1.setSsn("invalid-ssn"); // Should throw exception
                System.out.println("❌ Validation failed - invalid SSN allowed");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validation works: " + e.getMessage());
            }
            
            try {
                emp1.setEmail("invalid-email"); // Should throw exception
                System.out.println("❌ Validation failed - invalid email allowed");
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validation works: " + e.getMessage());
            }
            
            // Test 4: Comparison (sorting)
            System.out.println("\n🔄 Testing comparison...");
            int comparison = emp1.compareTo(emp2);
            if (comparison > 0) {
                System.out.println("✅ " + emp1.getLastName() + " comes after " + emp2.getLastName());
            } else {
                System.out.println("✅ " + emp1.getLastName() + " comes before " + emp2.getLastName());
            }
            
            // Test 5: Equals and HashCode
            System.out.println("\n🔍 Testing equals...");
            Employee emp3 = new Employee();
            emp3.setEmpid(1);
            emp3.setSsn("123-45-6789");
            
            Employee emp4 = new Employee();
            emp4.setEmpid(1);
            emp4.setSsn("123-45-6789");
            
            System.out.println("✅ emp3.equals(emp4): " + emp3.equals(emp4));
            System.out.println("✅ emp3.hashCode() == emp4.hashCode(): " + (emp3.hashCode() == emp4.hashCode()));
            
            System.out.println("\n🎉 Employee model test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
