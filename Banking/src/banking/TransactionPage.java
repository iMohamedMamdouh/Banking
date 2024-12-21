package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class TransactionPage {

    private JFrame frame;

    public TransactionPage() {
        frame = new JFrame("Transactions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        // إعداد العناصر في الصفحة
        JLabel lblSelectAccount = new JLabel("Select Account:");
        JComboBox<String> accountComboBox = new JComboBox<>();
        loadAccounts(accountComboBox); // تحميل الحسابات من قاعدة البيانات

        JLabel lblTransactionType = new JLabel("Transaction Type (Deposit/Withdraw):");
        JRadioButton rbDeposit = new JRadioButton("Deposit");
        JRadioButton rbWithdraw = new JRadioButton("Withdraw");
        ButtonGroup transactionTypeGroup = new ButtonGroup();
        transactionTypeGroup.add(rbDeposit);
        transactionTypeGroup.add(rbWithdraw);

        JLabel lblAmount = new JLabel("Amount:");
        JTextField txtAmount = new JTextField();

        JButton btnExecute = new JButton("Execute Transaction");
        JButton btnBack = new JButton("Back to Home");

        // تنظيم العناصر في اللوحة
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(lblSelectAccount);
        panel.add(accountComboBox);
        panel.add(lblTransactionType);
        panel.add(rbDeposit);
        panel.add(new JLabel());
        panel.add(rbWithdraw);
        panel.add(lblAmount);
        panel.add(txtAmount);
        panel.add(new JLabel());
        panel.add(btnExecute);
        panel.add(new JLabel());
        panel.add(btnBack);

        frame.add(panel, BorderLayout.NORTH);

        // إعداد الجدول لعرض العمليات السابقة
        String[] columnNames = {"Transaction ID", "User ID", "Transaction Type", "Amount", "Timestamp"};
        JTable transactionTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        transactionTable.setModel(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // استرجاع العمليات السابقة من قاعدة البيانات بناءً على الحساب المختار
        accountComboBox.addActionListener(e -> loadTransactions(model, (String) accountComboBox.getSelectedItem()));

        // تنفيذ العملية
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAccount = (String) accountComboBox.getSelectedItem();
                String amountStr = txtAmount.getText();
                String transactionType = rbDeposit.isSelected() ? "Deposit" : "Withdraw";

                // التحقق من صحة المدخلات
                if (selectedAccount == null || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);

                    // إضافة العملية إلى قاعدة البيانات وتحديث الرصيد
                    String query = "INSERT INTO transactions (UserID, TransactionType, Amount) VALUES (?, ?, ?)";
                    try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                        int userID = Integer.parseInt(selectedAccount.split(" - ")[0]); // استخراج ID المستخدم من السلسلة النصية.

                        stmt.setInt(1, userID);
                        stmt.setString(2, transactionType);
                        stmt.setDouble(3, amount);
                        stmt.executeUpdate();

                        // تحديث الرصيد في حساب العميل
                        updateAccountBalance(userID, amount, transactionType);

                        // تحديث الجدول بعد إضافة العملية
                        loadTransactions(model, selectedAccount);

                        JOptionPane.showMessageDialog(frame, "Transaction Executed Successfully");

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error executing transaction.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid amount.");
                }
            }
        });

        // زر الرجوع إلى الصفحة الرئيسية
        btnBack.addActionListener(e -> {
            frame.dispose();
            new MainDashboard("Admin"); // فتح الصفحة الرئيسية (إذا كان لديك صفحة رئيسية باسم HomePage).
        });

        frame.setVisible(true);
    }

    // تحميل الحسابات من قاعدة البيانات
    private void loadAccounts(JComboBox<String> accountComboBox) {
        String query = "SELECT UserID, Username FROM users";  // استعلام لاختيار المستخدمين
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("Username");
                accountComboBox.addItem(userID + " - " + name); // إضافة ID واسم المستخدم إلى القائمة
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // تحميل العمليات من قاعدة البيانات بناءً على الحساب المختار
    private void loadTransactions(DefaultTableModel model, String selectedAccount) {
        String query = "SELECT * FROM transactions WHERE UserID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            int userID = Integer.parseInt(selectedAccount.split(" - ")[0]);
            stmt.setInt(1, userID); // تمرير UserID المحدد
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // مسح البيانات القديمة

            while (rs.next()) {
                int transactionID = rs.getInt("TransactionID");
                int userIDFromDB = rs.getInt("UserID");
                String transactionType = rs.getString("TransactionType");
                double amount = rs.getDouble("Amount");
                Timestamp timestamp = rs.getTimestamp("Timestamp");

                model.addRow(new Object[]{transactionID, userIDFromDB, transactionType, amount, timestamp});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // تحديث رصيد العميل في جدول الحسابات
    private void updateAccountBalance(int userID, double amount, String transactionType) throws SQLException {
        String query = "SELECT Balance FROM accounts WHERE UserID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("Balance");
                double newBalance = transactionType.equals("Deposit") ? currentBalance + amount : currentBalance - amount;

                String updateQuery = "UPDATE accounts SET Balance = ? WHERE UserID = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setDouble(1, newBalance);
                    updateStmt.setInt(2, userID);
                    updateStmt.executeUpdate();
                }
            }
        }
    }
}
