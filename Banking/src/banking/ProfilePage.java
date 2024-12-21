package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProfilePage {

    private JFrame frame;

    public ProfilePage(String username) {
        frame = new JFrame("My Profile");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel lblUserId = new JLabel("User ID:");
        JLabel lblUserName = new JLabel("Name:");
        JLabel lbl = new JLabel();
        JLabel lblBalance = new JLabel("Balance:");

        JLabel txtUserId = new JLabel();
        JLabel txtUserName = new JLabel();
        JLabel txtBalance = new JLabel();
        
        JButton btnBack = new JButton("Back");

        panel.add(lblUserId);
        panel.add(txtUserId);
        panel.add(lblUserName);
        panel.add(txtUserName);
        panel.add(lblBalance);
        panel.add(txtBalance);        
        panel.add(lbl);
        panel.add(btnBack);

        frame.add(panel);

        loadUserProfile(username, txtUserId, txtUserName, txtBalance);


        btnBack.addActionListener(e -> {frame.dispose();new MainDashboard(username);});

        frame.setVisible(true);
    }

    private void loadUserProfile(String username, JLabel txtUserId, JLabel txtUserName, JLabel txtBalance) {
        String query = "SELECT * FROM users WHERE Username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtUserId.setText(String.valueOf(rs.getInt("UserID")));
                txtUserName.setText(rs.getString("Username"));
                txtBalance.setText(String.valueOf(rs.getDouble("Balance")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading profile data.");
        }
    }
}
