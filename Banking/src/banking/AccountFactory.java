/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

public class AccountFactory {
    public static Account createAccount(String accountType, int userId, double initialBalance) {
        switch (accountType) {
            case "Savings":
                return new SavingsAccount(userId, initialBalance);
            case "Current":
                return new CurrentAccount(userId, initialBalance);
            default:
                throw new IllegalArgumentException("Invalid account type.");
        }
    }
}

