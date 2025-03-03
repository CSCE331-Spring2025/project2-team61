import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;

/**
 * The {@code EmployeePanel} class provides a graphical interface for managing employees.
 * It allows viewing, adding, and removing employees from the database.
 * 
 * Features:
 * - Displays a table of employees with their ID, name, admin status, and a remove button.
 * - Allows adding new employees with hashed passwords.
 * - Supports removing employees from the database.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class EmployeePanel extends JPanel {
    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;
    private Connection connection;
    private JButton addEmployeeButton;

    /**
     * Constructs the {@code EmployeePanel} with a database connection.
     * Initializes the UI components and loads employee data.
     * 
     * @param connection The database connection used to fetch and modify employee records.
     */
    public EmployeePanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.decode("#E0F6F1"));

        // Title
        JLabel employeeLabel = new JLabel("Employee Management");
        employeeLabel.setFont(new Font("Arial", Font.BOLD, 50));
        employeeLabel.setForeground(Color.BLACK);
        employeeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(employeeLabel, BorderLayout.NORTH);

        // Employee Table
        String[] employeeColumns = { "ID", "Name", "Admin", "Remove" };
        employeeTableModel = new DefaultTableModel(employeeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only allow editing the "Remove" column
            }
        };
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 20));
        employeeTable.setRowHeight(40);

        // Configure the Remove button column
        employeeTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer("Remove"));
        employeeTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), "Remove", this));

        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        add(employeeScrollPane, BorderLayout.CENTER);

        // Add Employee Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addEmployeeButton = new JButton("Add Employee +");
        addEmployeeButton.setFont(new Font("Arial", Font.BOLD, 18));
        addEmployeeButton.setBackground(new Color(76, 175, 80)); // Green color
        addEmployeeButton.setForeground(Color.WHITE);
        addEmployeeButton.setFocusPainted(false);
        addEmployeeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        buttonPanel.add(addEmployeeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load employee data
        loadEmployeeData();
    }

    /**
     * Loads employee data from the database and populates the employee table.
     */
    public void loadEmployeeData() {
        employeeTableModel.setRowCount(0);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, name, admin FROM employee ORDER BY id;");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean isAdmin = rs.getBoolean("admin");
                employeeTableModel.addRow(new Object[] { id, name, isAdmin ? "Yes" : "No", "Remove" });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading employee data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays a dialog for adding a new employee to the database.
     * Allows entering the employee's name, password, and admin privileges.
     */
    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Employee", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Employee Name:");
        JTextField nameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JLabel adminLabel = new JLabel("Admin Privileges:");
        JCheckBox adminCheckBox = new JCheckBox();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(adminLabel);
        formPanel.add(adminCheckBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean isAdmin = adminCheckBox.isSelected();

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Name and password cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash the password using the existing PasswordHash class
            String hashedPassword = PasswordHash.hash(password);

            if (addEmployee(name, hashedPassword, isAdmin)) {
                dialog.dispose();
                loadEmployeeData(); // Reload the table
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Adds a new employee to the database.
     * 
     * @param name          The employee's name.
     * @param passwordHash  The hashed password.
     * @param isAdmin       Whether the employee has admin privileges.
     * @return {@code true} if the employee was added successfully, {@code false} otherwise.
     */
    private boolean addEmployee(String name, String passwordHash, boolean isAdmin) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO employee (name, password, admin) VALUES (?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, passwordHash);
            pstmt.setBoolean(3, isAdmin);
            pstmt.executeUpdate();
            pstmt.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Employee added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error adding employee: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Removes an employee from the database.
     * 
     * @param employeeId The ID of the employee to remove.
     */
    public void removeEmployee(int employeeId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this employee?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = connection.prepareStatement(
                        "DELETE FROM employee WHERE id = ?");
                pstmt.setInt(1, employeeId);
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Employee removed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Employee not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Error removing employee: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Button Renderer for the Remove button column
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Button Editor for the Remove button column
    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;
        private String buttonText;
        private EmployeePanel panel;

        public ButtonEditor(JCheckBox checkBox, String buttonText, EmployeePanel panel) {
            super(checkBox);
            this.buttonText = buttonText;
            this.panel = panel;
            button = new JButton(buttonText);
            button.addActionListener(e -> {
                // Store the employeeId and trigger removal after editing stops
                int employeeId = (int) this.panel.employeeTableModel.getValueAt(selectedRow, 0);
                fireEditingStopped(); // Stop editing first
                panel.removeEmployee(employeeId); // Then remove the employee
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return buttonText; // Simply return the text, no removal logic here
        }
    }
}