/**
 * The {@code Utils} class provides utility methods for string formatting.
 * It includes methods for converting text from snake_case to a more readable format.
 * 
 * This class is primarily used for formatting product type names retrieved from a database.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class Utils {

    /**
     * Converts a snake_case string to a human-readable format.
     * - Replaces underscores with spaces.
     * - Capitalizes the first letter of each word.
     * 
     * @param s The snake_case string to convert.
     * @return The formatted string in title case, or {@code null} if the input is {@code null}.
     */
    public static String snakeToReadable(String s) {
        if (s == null)
            return null;
        s = s.replace('_', ' ');

        s = s.substring(0, 1).toUpperCase() + s.substring(1);

        for (int i = 1; i < s.length() - 1; i++) {
            if (s.charAt(i) == ' ') {
                s = s.substring(0, i + 1) + s.substring(i + 1, i + 2).toUpperCase() + s.substring(i + 2);
            }
        }

        return s;
    }
}
