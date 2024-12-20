package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddAccountPage {

    private JFrame frame;
    private JTextField txtAccountID, txtClientName, txtBalance;
    private JComboBox<String> comboAccountType;

    public AddAccountPage() {
        frame = new JFrame("Add New Account");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // Create fields
        JLabel lblAccountID = new JLabel("Account ID:");
        txtAccountID = new JTextField(15);

        JLabel lblClientName = new JLabel("Client Name:");
        txtClientName = new JTextField(15);

        JLabel lblAccountType = new JLabel("Account Type:");
        comboAccountType = new JComboBox<>(new String[]{"Savings", "Current"});

        JLabel lblBalance = new JLabel("Initial Balance:");
        txtBalance = new JTextField(15);

        // Buttons
        JButton btnAddAccount = new JButton("Add Account");     
        JButton btnBack = new JButton("Back");

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(lblAccountID);
        panel.add(txtAccountID);
        panel.add(lblClientName);
        panel.add(txtClientName);
        panel.add(lblAccountType);
        panel.add(comboAccountType);
        panel.add(lblBalance);
        panel.add(txtBalance);
        panel.add(new JLabel());
        panel.add(btnAddAccount);
        panel.add(new JLabel());
        panel.add(btnBack);

        frame.add(panel);

        // Event listeners
        btnAddAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAccountToDatabase();
            }
        });
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new MainDashboard("Admin");
            }
        });

        // Display frame
        frame.setVisible(true);
    }

    private void addAccountToDatabase() {
        String accountID = txtAccountID.getText().trim();
        String clientName = txtClientName.getText().trim();
        String accountType = (String) comboAccountType.getSelectedItem();
        String balanceText = txtBalance.getText().trim();

        // Validate input data
        if (accountID.isEmpty() || clientName.isEmpty() || balanceText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid balance. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create Account object using the AccountFactory
        Account account = AccountFactory.createAccount(accountType, Integer.parseInt(accountID), balance);

        // Insert account into the database
        String query = "INSERT INTO accounts (accountid, userid, accounttype, balance) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, account.getAccountId());
            stmt.setInt(2, account.getUserId());  // Assuming userId is provided somewhere or can be determined
            stmt.setString(3, account.getAccountType());
            stmt.setDouble(4, account.getBalance());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Account added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
