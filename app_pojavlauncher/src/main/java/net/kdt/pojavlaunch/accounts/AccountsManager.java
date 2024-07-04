package net.kdt.pojavlaunch.accounts;

import android.util.Log;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.value.MinecraftAccount;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountsManager {
    private static final List<MinecraftAccount> accounts = new ArrayList<>();

    public void reload() {
        accounts.clear();
        File accountsPath = new File(Tools.DIR_ACCOUNT_NEW);
        if (accountsPath.exists() && accountsPath.isDirectory()) {
            File[] files = accountsPath.listFiles();
            if (files != null) {
                for (File accountFile : files) {
                    try {
                        String jsonString = Tools.read(accountFile);
                        MinecraftAccount account = MinecraftAccount.parse(jsonString);
                        if (account != null) accounts.add(account);
                    } catch (IOException e) {
                        Log.e("AccountsManager", String.format("File %s is not recognized as a profile for an account", accountFile.getName()));
                    }
                }
            }
        }
        Log.i("AccountsManager", "Reload complete.");
    }

    public void remove(String userName) {
        if (userName == null) return;
        for (MinecraftAccount account : accounts) {
            if (account.username.equals(userName)) {
                accounts.remove(account);
                break;
            }
        }
    }

    public void add(MinecraftAccount account) {
        if (account == null) return;
        for (MinecraftAccount minecraftAccount : accounts) {
            if (minecraftAccount.username.equals(account.username)) {
                accounts.remove(minecraftAccount);
                break;
            }
        }
        accounts.add(account);
    }

    public static List<MinecraftAccount> getAllAccount() {
        return new ArrayList<>(accounts);
    }

    public static boolean haveMicrosoftAccount() {
        for (MinecraftAccount account : accounts) {
            if (account.isMicrosoft) {
                return true;
            }
        }
        return false;
    }
}
