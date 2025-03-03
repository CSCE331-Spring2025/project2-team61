import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@code PasswordHash} class provides a utility method to hash passwords using the SHA-256 algorithm.
 * This ensures secure storage of passwords by generating a fixed-length cryptographic hash.
 * 
 * The hashing process:
 * - Converts the password into a byte array using UTF-8 encoding.
 * - Applies the SHA-256 hashing algorithm.
 * - Converts the resulting byte array into a hexadecimal string.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class PasswordHash {

    /**
     * Hashes a given password using the SHA-256 algorithm.
     * 
     * @param pw The plaintext password to be hashed.
     * @return A hexadecimal string representing the hashed password.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    static String hash(String pw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(pw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
