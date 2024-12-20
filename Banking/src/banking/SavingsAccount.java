/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

public class SavingsAccount extends Account {

    public SavingsAccount(int userId, double initialBalance) {
        super(0,userId,"Saving", initialBalance);
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Savings Account Info:");
        System.out.println("User ID: " + userId);
        System.out.println("Balance: " + balance);
    }
}

