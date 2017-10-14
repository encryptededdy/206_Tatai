package tatai.app.util.store;

import java.util.ArrayList;

/**
 * Manages the value store in the
 */
public class StoreManager {
    private int balance = 500;

    private ArrayList<StoreItem> items = new ArrayList<>();

    public StoreManager() {
        // Populate items
        items.add(new XPWallpaper());
        items.add(new XPWallpaperLogo());
        items.add(new AngryFinderWallpaper());
    }

    public void credit(int creditAmount) {
        balance += creditAmount;
        System.out.println("Store was credited: "+creditAmount);
    }

    boolean debit(int debitAmount) {
        if (debitAmount <= balance) {
            System.out.println("Store was debited: "+debitAmount);
            balance -= debitAmount;
            return true;
        } else {
            return false;
        }
    }

    public int getBalance() {
        return balance;
    }

    public ArrayList<StoreItem> getItems() {
        return items;
    }

}
