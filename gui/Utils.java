public class Utils {
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
