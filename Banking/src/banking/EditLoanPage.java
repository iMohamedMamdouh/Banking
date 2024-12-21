package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditLoanPage {
    private JFrame frame;
    private JTextField txtLoanID, txtUserID, txtLoanType, txtAmount, txtInterestRate;
    private int loanID;
    private String username;
    public EditLoanPage(int loanID, int userID, String loanType, double amount, double interestRate,String username) {
        this.loanID = loanID;
        this.username = username;
        frame = new JFrame("Edit Loan");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2, 10, 10));
        frame.setLocationRelativeTo(null);

        // Labels and Input Fields
        frame.add(new JLabel("Loan ID:"));
        txtLoanID = new JTextField(String.valueOf(loanID));
        txtLoanID.setEditable(false);
        frame.add(txtLoanID);

        frame.add(new JLabel("User ID:"));
        txtUserID = new JTextField(String.valueOf(userID));
        frame.add(txtUserID);

        frame.add(new JLabel("Loan Type:"));
        txtLoanType = new JTextField(loanType);
        frame.add(txtLoanType);

        frame.add(new JLabel("Amount ($):"));
        txtAmount = new JTextField(String.valueOf(amount));
        frame.add(txtAmount);

        frame.add(new JLabel("Interest Rate (%):"));
        txtInterestRate = new JTextField(String.valueOf(interestRate));
        frame.add(txtInterestRate);

        // Buttons
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");

        frame.add(btnSave);
        frame.add(btnCancel);

        // Button actions
        btnSave.addActionListener(e -> saveLoanChanges());
        btnCancel.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void saveLoanChanges() {
        try {
            int userID = Integer.parseInt(txtUserID.getText());
            String loanType = txtLoanType.getText();
            double amount = Double.parseDouble(txtAmount.getText());
            double interestRate = Double.parseDouble(txtInterestRate.getText());

            String query = "UPDATE loans SET UserID = ?, LoanType = ?, Amount = ?, InterestRate = ? WHERE LoanID = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, userID);
                stmt.setString(2, loanType);
                stmt.setDouble(3, amount);
                stmt.setDouble(4, interestRate);
                stmt.setInt(5, loanID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(frame, "Loan updated successfully.");
                    frame.dispose();
                    new ManageLoansPage(username);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update loan.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating loan.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please check all fields.");
        }
    }
}
