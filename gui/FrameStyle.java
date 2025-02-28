import javax.swing.*;
import java.awt.*;

public class FrameStyle {

    public static final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

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
