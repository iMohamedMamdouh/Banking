/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;
class AccountNotifier implements AccountObserver {
    @Override
    public void update(Account account) {
        System.out.println("Account updated: " + account.getAccountId());
    }
}
