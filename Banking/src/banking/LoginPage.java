package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage {
    private JFrame frame; // النافذة الرئيسية لتسجيل الدخول

    public LoginPage() {
        // إنشاء الإطار الرئيسي
        frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // لجعل النافذة تظهر في منتصف الشاشة

        // لوحة العنوان
        JLabel lblTitle = new JLabel("Banking System Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(lblTitle, BorderLayout.NORTH);

        // لوحة الإدخال
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();

        inputPanel.add(lblUsername);
        inputPanel.add(txtUsername);
        inputPanel.add(lblPassword);
        inputPanel.add(txtPassword);

        frame.add(inputPanel, BorderLayout.CENTER);

        // لوحة الأزرار
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // وظائف الأزرار
        btnLogin.addActionListener((var e) -> {
            String username = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            
            // التحقق من البيانات من قاعدة البيانات
            if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose(); // إغلاق نافذة تسجيل الدخول
                new MainDashboard(username); // استدعاء Main Dashboard
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExit.addActionListener(e -> System.exit(0));

        // عرض الإطار
        frame.setVisible(true);
    }

    // وظيفة للتحقق من اسم المستخدم وكلمة المرور من قاعدة البيانات
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE Username = ? AND Password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // بعد التحقق من صحة بيانات المستخدم، نبدأ الجلسة
                SessionManager.getInstance().startSession(username);
                return true;
            }
            return false; // إذا لم يتم العثور على المستخدم
        } catch (SQLException e) {
            return false;
        }
    }
}
