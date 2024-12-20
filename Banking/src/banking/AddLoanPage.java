package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddLoanPage {

    private JFrame frame;
    private JComboBox<String> userComboBox;  // إضافة قائمة منسدلة للمستخدمين

    public AddLoanPage() {
        frame = new JFrame("Add Loan");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // إعداد العناصر في الصفحة
        JLabel lblUser = new JLabel("Select User:");
        userComboBox = new JComboBox<>();  // إعداد القائمة المنسدلة للمستخدمين

        JLabel lblLoanType = new JLabel("Loan Type:");
        JTextField txtLoanType = new JTextField();
        JLabel lblAmount = new JLabel("Amount:");
        JTextField txtAmount = new JTextField();
        JLabel lblInterestRate = new JLabel("Interest Rate:");
        JTextField txtInterestRate = new JTextField();

        JButton btnSave = new JButton("Save Loan");

        // تنظيم العناصر في اللوحة
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(lblUser);
        panel.add(userComboBox);  // إضافة القائمة المنسدلة للمستخدمين
        panel.add(lblLoanType);
        panel.add(txtLoanType);
        panel.add(lblAmount);
        panel.add(txtAmount);
        panel.add(lblInterestRate);
        panel.add(txtInterestRate);
        panel.add(new JLabel());
        panel.add(btnSave);

        frame.add(panel, BorderLayout.CENTER);

        // تحميل المستخدمين من قاعدة البيانات
        loadUsers();

        // حفظ القرض في قاعدة البيانات
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loanType = txtLoanType.getText();
                String amount = txtAmount.getText();
                String interestRate = txtInterestRate.getText();
                String selectedUser = (String) userComboBox.getSelectedItem();  // الحصول على المستخدم المختار
                String query = "INSERT INTO loans (LoanType, Amount, InterestRate, UserID) VALUES (?, ?, ?, ?)";

                // استخراج معرف المستخدم من الاسم
                int userID = getUserIDByName(selectedUser);

                // إضافة القرض إلى قاعدة البيانات
                try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

                    stmt.setString(1, loanType);
                    stmt.setDouble(2, Double.parseDouble(amount));
                    stmt.setDouble(3, Double.parseDouble(interestRate));
                    stmt.setInt(4, userID);  // تعيين معرف المستخدم

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Loan Added Successfully");
                    frame.dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error saving loan to database.");
                }
            }
        });

        frame.setVisible(true);
    }

    // تحميل المستخدمين من قاعدة البيانات
    private void loadUsers() {
        String query = "SELECT UserID, Username FROM users";  // استعلام لاختيار المستخدمين

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("Username");
                userComboBox.addItem(username);  // إضافة اسم المستخدم إلى القائمة المنسدلة
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading users.");
        }
    }

    // استرجاع معرف المستخدم بناءً على اسم المستخدم
    private int getUserIDByName(String username) {
        String query = "SELECT UserID FROM users WHERE Username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;  // إذا لم يتم العثور على المستخدم
    }
}
