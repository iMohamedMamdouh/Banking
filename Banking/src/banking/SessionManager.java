/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

public class SessionManager {

    private static SessionManager instance;
    private String loggedInUser;

    // منع التكرار في التهيئة
    private SessionManager() {
    }

    // الحصول على المثيل الوحيد من الجلسة
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // بدء الجلسة
    public void startSession(String username) {
        this.loggedInUser = username;
        System.out.println("Session started for user: " + username);
    }

    // إنهاء الجلسة
    public void endSession() {
        this.loggedInUser = null;
        System.out.println("Session ended.");
    }

    // التحقق مما إذا كانت هناك جلسة نشطة
    public boolean isSessionActive() {
        return loggedInUser != null;
    }
}
