package com.employeemgmt.ui.console.Employee;

import com.employeemgmt.models.User;

/*
   PayHistoryScreen
   ----------------
   Right now this is just a stub, because the full payroll
   history feature is usually handled by PayrollDAO + reports.

   Keeping it simple so it doesn't break anything.
*/
public class PayHistoryScreen {

    public void show(User user) {
        System.out.println("\n--- Pay History ---");
        System.out.println("This would show detailed pay statements for " + user.getUsername() + ".");
        System.out.println("In the real build, we'd query PayrollDAO and format each pay period here.");
    }
}