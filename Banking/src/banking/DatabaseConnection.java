/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankingsystem"; // Adjust your DB URL
    private static final String USER = "root";  // Database username
    private static final String PASS = "";  // Database password
    private static Connection connection;

    // تحميل السائق (Driver) عند بدء التطبيق
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // تحميل السائق الخاص بـ MySQL
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // الحصول على الاتصال (Singleton)
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    // إغلاق الاتصال عند الانتهاء
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


