package tatai.app.util.store;

import javafx.scene.layout.Pane;
import tatai.app.Main;
import tatai.app.questions.generators.GeneratorManager;
import tatai.app.util.DisplaysAchievements;
import tatai.app.util.achievements.AchievementManager;

import java.util.ArrayList;

/**
 * Manages the monetery balance and also items unlocked in the store
 *
 * @author Edward
 */
public class StoreManager {
    private int balance = 0;

    private ArrayList<StoreItem> items = new ArrayList<>();

    // Stores the question generators, accessed independently.
    public GeneratorManager generators = new GeneratorManager();

    // Stores achievements
    public AchievementManager achievements;


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
        lastApplied = items.get(5);
        restoreItem();
        achievements = new AchievementManager(generators);
    }

    public StoreItem lastApplied;

    public void restoreItem() {
        if (lastApplied != null) {
            lastApplied.applyChanges();
        } else {
            items.get(5).applyChanges();
        }
    }

    /**
     * Credit the bcoin balance
     * @param creditAmount Amount to credit
     */
    public void credit(int creditAmount, DisplaysAchievements screen, Pane achievementsPane) {
        balance += creditAmount;
        System.out.println("Store was credited: "+creditAmount);
        if (!Main.store.achievements.getAchievements().get("Piggy Bank").isCompleted() && balance > 1000) {
            Main.store.achievements.getAchievements().get("Piggy Bank").setCompleted(screen, achievementsPane);
        }
        if (!Main.store.achievements.getAchievements().get("Savings Account").isCompleted() && balance > 5000) {
            Main.store.achievements.getAchievements().get("Savings Account").setCompleted(screen, achievementsPane);
        }
        if (!Main.store.achievements.getAchievements().get("Ballin'").isCompleted() && balance > 15000) {
            Main.store.achievements.getAchievements().get("Ballin'").setCompleted(screen, achievementsPane);
        }
        if (!Main.store.achievements.getAchievements().get("You Know This Isn't Real Money Right?").isCompleted() && balance > 50000) {
            Main.store.achievements.getAchievements().get("You Know This Isn't Real Money Right?").setCompleted(screen, achievementsPane);
        }
    }

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

    // ACHIEVEMENTS RELATED THINGS

    /**
     * returns the number of store items that have been purchased by the user
     */
    public int numberPurchased() {
        int count = 0;
        for (StoreItem item : items) {
            if (item.isPurchased()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the number of items in the store
     */
    public int numberItems() {
        return items.size();
    }



}
