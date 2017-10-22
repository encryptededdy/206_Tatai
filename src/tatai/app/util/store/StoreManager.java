package tatai.app.util.store;

import tatai.app.questions.generators.GeneratorManager;
import tatai.app.util.achievements.AchievementManager;

import java.util.ArrayList;

/**
 * Manages the monetery balance and also items unlocked in the store
 */
public class StoreManager {
    private int balance = 1000;

    private ArrayList<StoreItem> items = new ArrayList<>();

    // Stores the question generators, accessed independently.
    public GeneratorManager generators = new GeneratorManager();


    public StoreManager() {
        // Populate items
        items.add(new XPWallpaper());
        items.add(new XPWallpaperLogo());
        items.add(new AngryFinderWallpaper());
        items.add(new EwbuntuWallpaper());
        items.add(new Beta1Wallpaper());
        items.add(new Beta2Wallpaper());
        items.add(new AucklandParallax());
        items.add(new SpaceParallax());
        items.add(new MtCookParallax());
    }

    /**
     * Credit the bcoin balance
     * @param creditAmount Amount to credit
     */
    public void credit(int creditAmount) {
        balance += creditAmount;
        System.out.println("Store was credited: "+creditAmount);
    }

    /**
     * Debit the bcoin
     * @param debitAmount Amount to debit
     * @return Whether the debit was completed successfully
     */
    public boolean debit(int debitAmount) {
        if (debitAmount <= balance) {
            System.out.println("Store was debited: "+debitAmount);
            balance -= debitAmount;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the current bcoin balance
     * @return The current bcoin balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Gets the list of StoreItems supported by the store
     * @return ArrayList of StoreItems
     */
    public ArrayList<StoreItem> getItems() {
        return items;
    }

}
