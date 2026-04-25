import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorStandalone {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        String runnerPassword = "runner123";
        
        String encodedAdmin = encoder.encode(adminPassword);
        String encodedUser = encoder.encode(userPassword);
        String encodedRunner = encoder.encode(runnerPassword);
        
        System.out.println("=== BCrypt Encoded Passwords ===");
        System.out.println("admin123 -> " + encodedAdmin);
        System.out.println("user123 -> " + encodedUser);
        System.out.println("runner123 -> " + encodedRunner);
        System.out.println("================================");
        
        // 验证密码是否正确
        System.out.println("\n=== Verification ===");
        System.out.println("admin123 verification: " + encoder.matches(adminPassword, encodedAdmin));
        System.out.println("user123 verification: " + encoder.matches(userPassword, encodedUser));
        System.out.println("runner123 verification: " + encoder.matches(runnerPassword, encodedRunner));
    }
}
