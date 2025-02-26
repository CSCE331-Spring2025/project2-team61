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
        
        // Prevent resizing while maintaining full screen
        frame.setResizable(false);
        
        // Ensure frame takes up full screen even if resized
        frame.setMinimumSize(screenSize);
        frame.setMaximumSize(screenSize);
        
        // Set default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Optional: keep window focused and on top
        // frame.setAlwaysOnTop(true);
        frame.requestFocus();
    }
}
