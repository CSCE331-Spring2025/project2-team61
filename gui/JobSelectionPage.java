import javax.swing.*;
import java.awt.Font;

public class JobSelectionPage extends JFrame {

    static final int textFieldWidth = 200;
    static final int textFieldHeight = 50;

    static final Font selectionButtonFont = new Font("Arial", Font.BOLD, 32);

    Db db;

    public JobSelectionPage(Db db) {
        super("Job Selection");
        this.db = db;
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(null);
        int windowWidth = FrameStyle.screenWidth;
        int windowHeight = FrameStyle.screenHeight;
        int loginButtonYPosition = windowHeight / 2;

        JButton job1Button = new JButton("Cashier");
        job1Button.setFont(selectionButtonFont);
        job1Button.setBounds(windowWidth / 2 - textFieldWidth - 100, loginButtonYPosition, textFieldWidth, textFieldHeight);

        JButton job2Button = new JButton("Manager");
        job2Button.setFont(selectionButtonFont);
        job2Button.setBounds(windowWidth / 2, loginButtonYPosition, textFieldWidth, textFieldHeight);

        job1Button.addActionListener(e -> {
            System.out.printf("Cashier Selected!\n");
            this.dispose();
            new CashierPage(db).setVisible(true);
        });

        // job2Button.addActionListener(e -> {
        //     System.out.printf("Manager Selected!\n");
        //     this.dispose();
        //     new ManagerPage().setVisible(true);
        // });

        add(job1Button);
        add(job2Button);

        setSize(windowWidth, windowHeight);
    }
}
