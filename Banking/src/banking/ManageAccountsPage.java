/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageAccountsPage {
    private JFrame frame;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private String username;
    private AccountController controller;

    public ManageAccountsPage(String username) {
        this.username = username;
        this.frame = new JFrame("Manage Accounts");
        this.tableModel = new DefaultTableModel(new String[]{"Account ID", "User ID", "Account Type", "Balance"}, 0);
        this.accountsTable = new JTable(tableModel);

        // Initialize controller with empty account for now
        Account dummyAccount = new Account(0, 0, "", 0) {
            @Override
            public void displayAccountInfo() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        this.controller = new AccountController(dummyAccount, this);

        // Setup the frame and UI components
        setupUI();

        // Load accounts data from the database
        loadAccountsData();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JScrollPane tableScrollPane = new JScrollPane(accountsTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Account");
        JButton btnEdit = new JButton("Edit Account");
        JButton btnDelete = new JButton("Delete Account");
        JButton btnBack = new JButton("Back to Home");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        // Layout setup
        frame.setLayout(new BorderLayout());
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        btnAdd.addActionListener(e -> {
            frame.dispose();
            new AddAccountPage();
        });

        btnBack.addActionListener(e -> {
            frame.dispose();
            new MainDashboard(username); // Assuming there's a MainDashboard class
        });

        btnEdit.addActionListener(e -> editAccount());
        btnDelete.addActionListener(e -> deleteAccount());

        frame.setVisible(true);
    }

    private void loadAccountsData() {
        String query = "SELECT AccountID, UserID, AccountType, Balance FROM accounts";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int accountId = rs.getInt("AccountID");
                int userId = rs.getInt("UserID");
                String accountType = rs.getString("AccountType");
                double balance = rs.getDouble("Balance");

                tableModel.addRow(new Object[]{accountId, userId, accountType, balance});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAccountTable() {
        tableModel.setRowCount(0); // Clear existing rows
        loadAccountsData(); // Reload the account data
    }

    private void editAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow != -1) {
            int accountId = (Integer) accountsTable.getValueAt(selectedRow, 0);
            int userId = (Integer) accountsTable.getValueAt(selectedRow, 1);
            String accountType = (String) accountsTable.getValueAt(selectedRow, 2);
            double balance = (Double) accountsTable.getValueAt(selectedRow, 3);

            // Open the Edit Account page and pass the selected account details
            frame.dispose();
            new EditAccountPage(accountId, userId, accountType, balance);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an account to edit.");
        }
    }
// Delete account functionality
    private void deleteAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow != -1) {
            int accountId = (Integer) accountsTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Delete account from the database
                deleteAccountFromDatabase(accountId);

                // Remove the account from the table
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an account to delete.");
        }
    }

    // Delete account from the database
    private void deleteAccountFromDatabase(int accountId) {
        String query = "DELETE FROM accounts WHERE AccountID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Account deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Account deletion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error occurred while deleting the account.");
        }
    }
}
