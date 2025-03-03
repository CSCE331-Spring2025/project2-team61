import javax.swing.*;
import java.awt.*;

/**
 * The {@code FrameStyle} class provides utility methods for styling JFrame windows.
 * It ensures that frames are properly sized, centered, and configured for fullscreen display.
 * 
 * This class defines:
 * - The screen width and height as constants.
 * - A method to apply a consistent styling to JFrame instances.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class FrameStyle {

    /**
     * The width of the screen in pixels.
     */
    public static final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;

    /**
     * The height of the screen in pixels.
     */
    public static final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    /**
     * Applies a standardized fullscreen style to the given JFrame.
     * 
     * This method:
     * - Sets the frame size to match the screen dimensions.
     * - Centers the frame on the screen.
     * - Removes window decorations for fullscreen mode.
     * - Ensures the frame cannot be resized beyond fullscreen size.
     * - Sets the default close operation to {@code JFrame.EXIT_ON_CLOSE}.
     * - Requests focus for proper user interaction.
     * 
     * @param frame The JFrame to style.
     */
    public static void StyleFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Set frame to full screen dimensions
        frame.setSize(screenSize);
        frame.setPreferredSize(screenSize);

        // Remove window decorations for true fullscreen
        frame.setUndecorated(false);

        // Center on screen
        frame.setLocationRelativeTo(null);

        // Ensure frame takes up full screen even if resized
        frame.setMinimumSize(screenSize);
        frame.setMaximumSize(screenSize);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.requestFocus();
    }
}

