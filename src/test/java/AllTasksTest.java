/**
 * All Tasks Test Runner
 * Comprehensive test suite for all 4 completed tasks with consistent naming
 */
public class AllTasksTest {
    
    public static void main(String[] args) {
        System.out.println("🚀 Employee Management System - Complete Test Suite");
        System.out.println("===================================================");
        System.out.println();
        
        try {
            // Task 1: User Authentication System
            runTask1();
            
            // Task 2: Employee Search Functionality  
            runTask2();
            
            // Task 3: Employee CRUD Operations
            runTask3();
            
            // Task 4: Salary Update by Percentage
            runTask4();
            
            System.out.println("\n🎉 All Tasks (1-4) Completed Successfully!");
            System.out.println("📊 Project Status: 80% Complete (4/5 tasks done)");
            System.out.println("🔄 Remaining: Task 5 - Report Generation System");
            
        } catch (Exception e) {
            System.err.println("❌ Test suite failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runTask1() {
        System.out.println("🔐 Running Task 1: User Authentication System...");
        System.out.println("=" + "=".repeat(60));
        AuthenticationSystemTest.main(new String[]{});
        System.out.println("✅ Task 1 Complete\n");
    }
    
    private static void runTask2() {
        System.out.println("🔍 Running Task 2: Employee Search Functionality...");
        System.out.println("=" + "=".repeat(60));
        EmployeeSearchTest.main(new String[]{});
        System.out.println("✅ Task 2 Complete\n");
    }
    
    private static void runTask3() {
        System.out.println("📝 Running Task 3: Employee CRUD Operations...");
        System.out.println("=" + "=".repeat(60));
        EmployeeCRUDTest.main(new String[]{});
        System.out.println("✅ Task 3 Complete\n");
    }
    
    private static void runTask4() {
        System.out.println("💰 Running Task 4: Salary Update by Percentage...");
        System.out.println("=" + "=".repeat(60));
        SalaryUpdateTest.main(new String[]{});
        System.out.println("✅ Task 4 Complete\n");
    }
}
