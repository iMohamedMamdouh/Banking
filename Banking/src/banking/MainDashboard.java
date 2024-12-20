package banking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainDashboard {

    private JFrame frame;
    private String username;  // لتخزين اسم المستخدم الذي سجل الدخول

    public MainDashboard(String username) {
        this.username = username;  // تعيين اسم المستخدم الذي سجل الدخول

        // إنشاء الإطار الرئيسي
        frame = new JFrame("Main Dashboard - Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 250);
        frame.setLocationRelativeTo(null);

        // شريط القوائم
        JMenuBar menuBar = new JMenuBar();

        JMenu accountMenu = new JMenu("Accounts");
        JMenuItem manageAccounts = new JMenuItem("Manage Accounts");
        JMenuItem addAccount = new JMenuItem("Add Account");
        accountMenu.add(manageAccounts);
        accountMenu.add(addAccount);

        JMenu loanMenu = new JMenu("Loans");
        JMenuItem manageLoans = new JMenuItem("Manage Loans");
        loanMenu.add(manageLoans);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        JMenuItem exit = new JMenuItem("Exit");
        helpMenu.add(about);
        helpMenu.add(exit);

        menuBar.add(accountMenu);
        menuBar.add(loanMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);

        // اللوحة الجانبية - ملخص النظام
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(1, 4, 10, 10));
        sidePanel.setBorder(BorderFactory.createTitledBorder("System Summary"));
        sidePanel.setPreferredSize(new Dimension(250, 0));

        // استرجاع البيانات من قاعدة البيانات
        JLabel lblAccounts = new JLabel("Total Accounts: " + getTotalAccounts());
        JLabel lblLoans = new JLabel("Total Loans: " + getTotalLoans());
        JLabel lblBalance = new JLabel("Total Balance: " + getTotalBalance());

        sidePanel.add(lblAccounts);
        sidePanel.add(lblLoans);
        sidePanel.add(lblBalance);

        // اللوحة الرئيسية مع الأزرار
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 4, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnManageAccounts = createDashboardButton("Manage Accounts");
        JButton btnManageLoans = createDashboardButton("Manage Loans");
        JButton btnTransactions = createDashboardButton("Transaction");
        JButton btnMyProfile = createDashboardButton("My Profile");

        mainPanel.add(btnManageAccounts);
        mainPanel.add(btnManageLoans);
        mainPanel.add(btnTransactions);
        mainPanel.add(btnMyProfile);

        // شريط الحالة
        JLabel lblStatus = new JLabel("Welcome, " + username, JLabel.RIGHT);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // إضافة المكونات إلى الإطار
        frame.setLayout(new BorderLayout());
        frame.add(sidePanel, BorderLayout.CENTER);
        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(lblStatus, BorderLayout.SOUTH);

        // إجراءات القوائم
        btnManageAccounts.addActionListener(e -> {
            frame.dispose();
            new ManageAccountsPage(username);
        });
        btnManageLoans.addActionListener(e -> {
            frame.dispose();
            new ManageLoansPage(username);
        });

        btnTransactions.addActionListener(e -> {
            frame.dispose();
            new TransactionPage();
        });
        addAccount.addActionListener(e -> {
            frame.dispose();
            new AddAccountPage(); // فتح صفحة إضافة الحساب
        });

        exit.addActionListener(e -> {
            frame.dispose();
            new LoginPage();
        });
        about.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Banking System\nVersion 1.0", "About", JOptionPane.INFORMATION_MESSAGE));

        // عرض الإطار
        frame.setVisible(true);
    }

    private JButton createDashboardButton(String buttonType) {
        if (buttonType.equals("Manage Accounts")) {
            return new JButton("Manage Accounts");
        } else if (buttonType.equals("Manage Loans")) {
            return new JButton("Manage Loans");
        } else if (buttonType.equals("Transaction")) {
            return new JButton("Transaction");
        } else {
            return new JButton("My Profile");
        }
    }

    // استرجاع إجمالي الحسابات من قاعدة البيانات
    private int getTotalAccounts() {
        int totalAccounts = 0;
        String query = "SELECT COUNT(*) FROM accounts";
        try (Connection connection = DatabaseConnection.getConnection(); Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                totalAccounts = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalAccounts;
    }

    // استرجاع إجمالي القروض من قاعدة البيانات
    private int getTotalLoans() {
        int totalLoans = 0;
        String query = "SELECT COUNT(*) FROM loans";
        try (Connection connection = DatabaseConnection.getConnection(); Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                totalLoans = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalLoans;
    }

    // استرجاع إجمالي الرصيد من قاعدة البيانات
    private double getTotalBalance() {
        double totalBalance = 0.0;
        String query = "SELECT SUM(Balance) FROM accounts";
        try (Connection connection = DatabaseConnection.getConnection(); Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                totalBalance = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalBalance;
    }
}
