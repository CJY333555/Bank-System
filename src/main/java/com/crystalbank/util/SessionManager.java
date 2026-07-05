package com.crystalbank.util;

import com.crystalbank.model.Account;
import com.crystalbank.model.User;

// Holds the currently logged-in user's data across all screens
public class SessionManager {

    private static User currentUser;
    private static Account currentAccount;

    public static void setCurrentUser(User user)       { currentUser = user; }
    public static User getCurrentUser()                { return currentUser; }

    public static void setCurrentAccount(Account acc)  { currentAccount = acc; }
    public static Account getCurrentAccount()          { return currentAccount; }

    public static void logout() {
        currentUser    = null;
        currentAccount = null;
    }
}
