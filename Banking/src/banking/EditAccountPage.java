/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditAccountPage {
    
    private JFrame frame;
    private JTextField tfAccountID, tfUserID, tfAccountType, tfBalance;
    private int accountId;

    public EditAccountPage(int accountId, int userId, String accountType, double balance) {
        this.accountId = accountId;  // تخزين الـ AccountID
        // إنشاء الإطار
        frame = new JFrame("Edit Account");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        
        // إنشاء لوحة الإدخال
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        
        // الحقول النصية لعرض وتعديل البيانات
        panel.add(new JLabel("Account ID:"));
        tfAccountID = new JTextField(String.valueOf(accountId));
        tfAccountID.setEditable(false);  // لا يمكن تعديل AccountID
        panel.add(tfAccountID);

        panel.add(new JLabel("User ID:"));
        tfUserID = new JTextField(String.valueOf(userId));
        panel.add(tfUserID);

        panel.add(new JLabel("Account Type:"));
        tfAccountType = new JTextField(accountType);
        panel.add(tfAccountType);

        panel.add(new JLabel("Balance:"));
        tfBalance = new JTextField((int) balance);
        panel.add(tfBalance);

        // إضافة زر لحفظ التعديلات
        JButton btnSave = new JButton("Save Changes");
        panel.add(new JLabel());  // لترك المساحة
        panel.add(btnSave);

        // إضافة اللوحة للإطار
        frame.add(panel, BorderLayout.CENTER);

        // زر حفظ التعديلات
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // تحديث البيانات في قاعدة البيانات
                updateAccountDetails();
            }
        });

        // عرض الإطار
        frame.setVisible(true);
    }

    // تحديث تفاصيل الحساب في قاعدة البيانات
    private void updateAccountDetails() {
        String userIdStr = tfUserID.getText();
        String accountType = tfAccountType.getText();
        String balanceStr = tfBalance.getText();
        
        // التحقق من صحة البيانات المدخلة
        if (userIdStr.isEmpty() || accountType.isEmpty() || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            double balance = Double.parseDouble(balanceStr);
            
            // استعلام لتحديث البيانات في قاعدة البيانات
            String query = "UPDATE accounts SET UserID = ?, AccountType = ?, Balance = ? WHERE AccountID = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                // تعيين القيم للـ PreparedStatement
                stmt.setInt(1, userId);
                stmt.setString(2, accountType);
                stmt.setDouble(3, balance);
                stmt.setInt(4, accountId);

                // تنفيذ الاستعلام
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(frame, "Account updated successfully.");
                    frame.dispose();  // إغلاق الصفحة بعد التحديث
                } else {
                    JOptionPane.showMessageDialog(frame, "Error updating account.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while updating the account.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for User ID and Balance.");
        }
    }
}

