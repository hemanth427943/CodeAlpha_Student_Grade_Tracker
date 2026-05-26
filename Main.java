import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {

    JTextField nameField, idField, subjectField, gradeField;
    JButton addStudentBtn, addGradeBtn;
    JTable table;
    DefaultTableModel model;

    Connection con;

    public Main() {
        setTitle("Student Grade Tracker (DB)");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        connectDB();

        // Top Panel
        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        panel.add(nameField);

        panel.add(new JLabel("ID:"));
        idField = new JTextField(5);
        panel.add(idField);

        addStudentBtn = new JButton("Add Student");
        panel.add(addStudentBtn);

        panel.add(new JLabel("Subject:"));
        subjectField = new JTextField(10);
        panel.add(subjectField);

        panel.add(new JLabel("Grade:"));
        gradeField = new JTextField(5);
        panel.add(gradeField);

        addGradeBtn = new JButton("Add Grade");
        panel.add(addGradeBtn);

        add(panel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Average"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addStudentBtn.addActionListener(e -> addStudent());
        addGradeBtn.addActionListener(e -> addGrade());

        loadTable();

        setVisible(true);
    }

    void connectDB() {
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/student_db",
                "root",
                "password" // change this
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed");
        }
    }

    // ➕ Add Student (with duplicate handling)
    void addStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();

            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM students WHERE id=?"
            );
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // 🔥 If ID exists → ask for subject directly
                JOptionPane.showMessageDialog(this, "Student exists! Add subject.");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO students VALUES (?, ?)"
            );
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Added!");
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error!");
        }
    }

    // ➕ Add Grade
    void addGrade() {
        try {
            int id = Integer.parseInt(idField.getText());
            String subject = subjectField.getText();
            double grade = Double.parseDouble(gradeField.getText());

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO grades(student_id, subject, score) VALUES (?, ?, ?)"
            );
            ps.setInt(1, id);
            ps.setString(2, subject);
            ps.setDouble(3, grade);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Grade Added!");
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding grade!");
        }
    }

    // 🔄 Load Table with Average
    void loadTable() {
        try {
            model.setRowCount(0);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT s.id, s.name, AVG(g.score) as avg_score " +
                "FROM students s LEFT JOIN grades g " +
                "ON s.id = g.student_id " +
                "GROUP BY s.id, s.name"
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    String.format("%.2f", rs.getDouble("avg_score"))
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}