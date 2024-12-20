/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

// Controller - Handling Logic for Account Management

import java.util.ArrayList;
import java.util.List;

class AccountController {
    private Account model;
    private ManageAccountsPage view;
    private List<AccountObserver> observers = new ArrayList<>();

    public AccountController(Account model, ManageAccountsPage view) {
        this.model = model;
        this.view = view;
    }

    public void updateAccountBalance(double newBalance) {
        model.setBalance(newBalance);
        notifyObservers();
        view.updateAccountTable(); // Notify the view to update the table
    }

    public void deleteAccount() {
        notifyObservers();
        view.updateAccountTable(); // Notify the view to remove the account from the table
    }

    public void addObserver(AccountObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AccountObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AccountObserver observer : observers) {
            observer.update(model);
        }
    }
}