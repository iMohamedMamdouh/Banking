package banking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageLoansPage {

    private JFrame frame;
    private JTable loansTable;
    private DefaultTableModel tableModel;
    private String username;

    public ManageLoansPage(String username) {
        this.username = username;
        this.frame = new JFrame("Manage Loans");
        this.tableModel = new DefaultTableModel(new String[]{"Loan ID", "Client Name", "Loan Type", "Amount", "Interest Rate"}, 0);
        this.loansTable = new JTable(tableModel);

        // Setup UI components
        setupUI();

        // Load loan data
        loadLoansData();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JScrollPane tableScrollPane = new JScrollPane(loansTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Loan");
        JButton btnEdit = new JButton("Edit Loan");
        JButton btnDelete = new JButton("Delete Loan");
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
            new AddLoanPage(); // Navigate to Add Loan Page
        });

        btnBack.addActionListener(e -> {
            frame.dispose();
            new MainDashboard(username); // Navigate back to dashboard
        });

        btnEdit.addActionListener(e -> editLoan());
        btnDelete.addActionListener(e -> deleteLoan());

        frame.setVisible(true);
    }

    private void loadLoansData() {
        String query = "SELECT LoanID, UserID, LoanType, Amount, InterestRate FROM loans";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int loanID = rs.getInt("LoanID");
                int userID = rs.getInt("UserID");
                String loanType = rs.getString("LoanType");
                double amount = rs.getDouble("Amount");
                double interestRate = rs.getDouble("InterestRate");

                tableModel.addRow(new Object[]{loanID, userID, loanType, amount, interestRate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading loans from database.");
        }
    }

    private void editLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow != -1) {
            int loanID = (Integer) loansTable.getValueAt(selectedRow, 0);
            int userID = (Integer) loansTable.getValueAt(selectedRow, 1);
            String loanType = (String) loansTable.getValueAt(selectedRow, 2);
            double amount = (Double) loansTable.getValueAt(selectedRow, 3);
            double interestRate = (Double) loansTable.getValueAt(selectedRow, 4);

            frame.dispose();
            new EditLoanPage(loanID, userID, loanType, amount, interestRate,username);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a loan to edit.");
        }
    }

    private void deleteLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow != -1) {
            int loanID = (Integer) loansTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this loan?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteLoanFromDatabase(loanID);
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a loan to delete.");
        }
    }

    private void deleteLoanFromDatabase(int loanID) {
        String query = "DELETE FROM loans WHERE LoanID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, loanID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Loan deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Loan deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error occurred while deleting the loan.");
        }
    }
}
