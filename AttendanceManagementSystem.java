// Import necessary packages
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

// Main class for Attendance Management System
public class AttendanceManagementSystem extends JFrame {
    private JTable table; // Table to display attendance records
    private DefaultTableModel tableModel; // Model to manage table data
    private JTextField studentNameField; // Input field for student names
    private JButton addStudentButton; // Button to add a student
    private JButton markPresentButton; // Button to mark a student present
    private JButton markAbsentButton; // Button to mark a student absent
    private JButton saveButton; // Button to save attendance to file

    // Constructor to set up the UI
    public AttendanceManagementSystem() {
        setTitle("Attendance Management System"); // Window title
        setSize(800, 500); // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setLocationRelativeTo(null); // Center the window

        // Initialize table with columns
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Student Name");
        tableModel.addColumn("Date");
        tableModel.addColumn("Attendance Status");

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        // Load students from file
        loadStudentNames();

        // Input field for student names
        studentNameField = new JTextField(15);

        // Initialize buttons with styles
        addStudentButton = createStyledButton("Add Student");
        markPresentButton = createStyledButton("Mark Present");
        markAbsentButton = createStyledButton("Mark Absent");
        saveButton = createStyledButton("Save to File");

        // Panel for managing attendance
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Manage Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(studentNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(addStudentButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(markPresentButton, gbc);
        gbc.gridx = 2;
        inputPanel.add(markAbsentButton, gbc);
        gbc.gridx = 3;
        inputPanel.add(saveButton, gbc);

        // Add listeners to buttons
        addStudentButton.addActionListener(new AddStudentAction());
        markPresentButton.addActionListener(new MarkAttendanceAction("Present"));
        markAbsentButton.addActionListener(new MarkAttendanceAction("Absent"));
        saveButton.addActionListener(new SaveAttendanceAction());

        // Layout setup
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    // Create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    // Load student names from a file
    private void loadStudentNames() {
        File file = new File("resources/students.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String name;
            while ((name = reader.readLine()) != null) {
                Vector<Object> row = new Vector<>();
                row.add(name);
                row.add(""); // Date column empty initially
                row.add(""); // Attendance status column empty initially
                tableModel.addRow(row);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading student names: " + e.getMessage());
        }
    }

    // Save student names to file
    private void saveStudentNames(String studentName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/students.txt", true))) {
            writer.write(studentName);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student name: " + e.getMessage());
        }
    }

    // Add a new student
    private class AddStudentAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String studentName = studentNameField.getText().trim();
            if (studentName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a student name.");
                return;
            }
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(studentName)) {
                    JOptionPane.showMessageDialog(null, "Student already exists.");
                    return;
                }
            }
            Vector<Object> row = new Vector<>();
            row.add(studentName);
            row.add("");
            row.add("");
            tableModel.addRow(row);
            saveStudentNames(studentName);
            studentNameField.setText("");
        }
    }

    // Mark attendance
    private class MarkAttendanceAction implements ActionListener {
        private String status;

        public MarkAttendanceAction(String status) {
            this.status = status;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(null, "Please select students.");
                return;
            }
            for (int row : selectedRows) {
                tableModel.setValueAt(currentDate, row, 1);
                tableModel.setValueAt(status, row, 2);
            }
        }
    }

    // Save attendance to file
    private class SaveAttendanceAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (FileWriter writer = new FileWriter("resources/attendance.csv")) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j).toString() + ",");
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(null, "Attendance saved.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttendanceManagementSystem frame = new AttendanceManagementSystem();
            frame.setVisible(true);
        });
    }
}
