/**
 * The {@code Main} class serves as the entry point for the application.
 * It initializes and displays the {@code LoginScreen} frame.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class Main {
    
    /**
     * The main method that starts the application.
     * It creates an instance of {@code LoginScreen} and makes it visible.
     * 
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        LoginScreen startFrame = new LoginScreen();
        startFrame.setVisible(true);
    }
}
