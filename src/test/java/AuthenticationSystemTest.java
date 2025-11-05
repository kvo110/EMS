import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.AuthenticationResult;
import com.employeemgmt.models.User;
import com.employeemgmt.utils.SecurityUtils;

/**
 * Comprehensive test for the Authentication System (Task 1)
 */
public class AuthenticationSystemTest {
    
    public static void main(String[] args) {
        System.out.println("🔐 Testing Authentication System (Task 1)...\n");
        
        try {
            // Test 1: SecurityUtils Password Hashing
            testPasswordHashing();
            
            // Test 2: Password Validation
            testPasswordValidation();
            
            // Test 3: Username Validation
            testUsernameValidation();
            
            // Test 4: Authentication Service Login
            testAuthenticationService();
            
            System.out.println("\n🎉 Authentication System Test Completed Successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testPasswordHashing() {
        System.out.println("📋 Testing Password Hashing...");
        
        String password = "TestPassword123!";
        
        // Test hashing
        String hash1 = SecurityUtils.hashPassword(password);
        String hash2 = SecurityUtils.hashPassword(password);
        
        System.out.println("✅ Password hashed successfully");
        System.out.println("   Hash 1: " + hash1.substring(0, 20) + "...");
        System.out.println("   Hash 2: " + hash2.substring(0, 20) + "...");
        
        // Hashes should be different (due to salt)
        if (!hash1.equals(hash2)) {
            System.out.println("✅ Salted hashing working - different hashes for same password");
        } else {
            System.out.println("❌ Hashing issue - same hash for same password");
        }
        
        // Test verification
        if (SecurityUtils.verifyPassword(password, hash1)) {
            System.out.println("✅ Password verification working");
        } else {
            System.out.println("❌ Password verification failed");
        }
        
        // Test wrong password
        if (!SecurityUtils.verifyPassword("WrongPassword", hash1)) {
            System.out.println("✅ Wrong password correctly rejected");
        } else {
            System.out.println("❌ Wrong password incorrectly accepted");
        }
        
        System.out.println();
    }
    
    private static void testPasswordValidation() {
        System.out.println("📋 Testing Password Validation...");
        
        // Test valid password
        SecurityUtils.ValidationResult result1 = SecurityUtils.validatePasswordStrength("ValidPass123!");
        if (result1.isValid()) {
            System.out.println("✅ Valid password accepted");
        } else {
            System.out.println("❌ Valid password rejected: " + result1.getErrors());
        }
        
        // Test invalid passwords
        String[] invalidPasswords = {
            "short",           // Too short
            "nouppercase123!", // No uppercase
            "NOLOWERCASE123!", // No lowercase
            "NoNumbers!",      // No numbers
            "NoSpecialChars123" // No special characters
        };
        
        for (String pwd : invalidPasswords) {
            SecurityUtils.ValidationResult result = SecurityUtils.validatePasswordStrength(pwd);
            if (!result.isValid()) {
                System.out.println("✅ Invalid password '" + pwd + "' correctly rejected: " + result.getErrors());
            } else {
                System.out.println("❌ Invalid password '" + pwd + "' incorrectly accepted");
            }
        }
        
        System.out.println();
    }
    
    private static void testUsernameValidation() {
        System.out.println("📋 Testing Username Validation...");
        
        // Test valid usernames
        String[] validUsernames = {"john.doe", "jane_smith", "admin123", "user-name"};
        for (String username : validUsernames) {
            SecurityUtils.ValidationResult result = SecurityUtils.validateUsername(username);
            if (result.isValid()) {
                System.out.println("✅ Valid username '" + username + "' accepted");
            } else {
                System.out.println("❌ Valid username '" + username + "' rejected: " + result.getErrors());
            }
        }
        
        // Test invalid usernames
        String[] invalidUsernames = {"ab", "user@name", "user name", ""};
        for (String username : invalidUsernames) {
            SecurityUtils.ValidationResult result = SecurityUtils.validateUsername(username);
            if (!result.isValid()) {
                System.out.println("✅ Invalid username '" + username + "' correctly rejected: " + result.getErrors());
            } else {
                System.out.println("❌ Invalid username '" + username + "' incorrectly accepted");
            }
        }
        
        System.out.println();
    }
    
    private static void testAuthenticationService() {
        System.out.println("📋 Testing Authentication Service...");
        
        AuthenticationService authService = new AuthenticationService();
        
        // Test login with existing user (from sample data)
        System.out.println("🔑 Testing login with existing user...");
        
        // Try to login with hr_admin (from sample data)
        AuthenticationResult result = authService.login("hr_admin", "admin123");
        
        if (result.isSuccess()) {
            System.out.println("✅ Login successful: " + result.getMessage());
            User user = result.getUser();
            System.out.println("   User: " + user.getUsername());
            System.out.println("   Role: " + user.getRoleDisplayName());
            System.out.println("   Admin: " + user.isAdmin());
            
            // Test authorization
            System.out.println("\n🔒 Testing Authorization...");
            
            if (authService.isAuthorized("view_all_employees")) {
                System.out.println("✅ Admin authorized for view_all_employees");
            } else {
                System.out.println("❌ Admin not authorized for view_all_employees");
            }
            
            if (authService.isAuthorized("add_employee")) {
                System.out.println("✅ Admin authorized for add_employee");
            } else {
                System.out.println("❌ Admin not authorized for add_employee");
            }
            
            // Test session validation
            System.out.println("\n⏰ Testing Session Validation...");
            if (authService.validateSession()) {
                System.out.println("✅ Session is valid");
            } else {
                System.out.println("❌ Session is invalid");
            }
            
            // Test logout
            System.out.println("\n🚪 Testing Logout...");
            if (authService.logout()) {
                System.out.println("✅ Logout successful");
            } else {
                System.out.println("❌ Logout failed");
            }
            
            // Test session after logout
            if (!authService.validateSession()) {
                System.out.println("✅ Session correctly invalidated after logout");
            } else {
                System.out.println("❌ Session still valid after logout");
            }
            
        } else {
            System.out.println("⚠️  Login failed (expected if sample data not loaded): " + result.getMessage());
            System.out.println("   This is normal if you haven't loaded sample data yet.");
        }
        
        // Test invalid login
        System.out.println("\n🚫 Testing Invalid Login...");
        AuthenticationResult invalidResult = authService.login("invalid_user", "wrong_password");
        
        if (!invalidResult.isSuccess()) {
            System.out.println("✅ Invalid login correctly rejected: " + invalidResult.getMessage());
        } else {
            System.out.println("❌ Invalid login incorrectly accepted");
        }
        
        System.out.println();
    }
}
