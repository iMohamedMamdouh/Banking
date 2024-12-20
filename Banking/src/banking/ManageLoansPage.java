package banking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageLoansPage {

    private JFrame frame;
    private String username;
    public ManageLoansPage(String username) {
        this.username = username;  // تعيين اسم المستخدم الذي سجل الدخول
        // إنشاء الإطار
        frame = new JFrame("Manage Loans");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // لوحة الجدول
        String[] columnNames = {"Loan ID", "Client Name", "Loan Type", "Amount", "Interest Rate"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable loansTable = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(loansTable);

        // لوحة الأزرار
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Loan");
        JButton btnEdit = new JButton("Edit Loan");
        JButton btnDelete = new JButton("Delete Loan");
        JButton btnBack = new JButton("Back to Home");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        // ترتيب العناصر في الإطار
        frame.setLayout(new BorderLayout());
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // تحميل القروض من قاعدة البيانات
        loadLoansFromDatabase(model);

        // وظائف الأزرار
        btnAdd.addActionListener(e -> {
            frame.dispose();
            new AddLoanPage(); // فتح صفحة إضافة قرض جديدة
        });
        btnBack.addActionListener(e -> {
            frame.dispose();
            new MainDashboard(username); // فتح الصفحة الرئيسية (إذا كان لديك صفحة رئيسية باسم HomePage).
        });
        btnEdit.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Edit Loan functionality"));
        btnDelete.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Delete Loan functionality"));

        // عرض الإطار
        frame.setVisible(true);
    }

    // تحميل القروض من قاعدة البيانات
    private void loadLoansFromDatabase(DefaultTableModel model) {
        String query = "SELECT LoanID, UserID, LoanType, Amount, InterestRate FROM loans";

        try (Connection connection = DatabaseConnection.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int loanID = rs.getInt("LoanID");
                int userID = rs.getInt("UserID");
                String loanType = rs.getString("LoanType");
                double amount = rs.getDouble("Amount");
                double interestRate = rs.getDouble("InterestRate");

                // إضافة البيانات إلى الجدول
                model.addRow(new Object[]{loanID, userID, loanType, "$" + amount, interestRate + "%"});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading loans from database.");
        }
    }
}
