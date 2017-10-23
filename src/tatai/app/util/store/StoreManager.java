package tatai.app.util.store;

import javafx.scene.layout.Pane;
import tatai.app.Main;
import tatai.app.questions.generators.GeneratorManager;
import tatai.app.util.DisplaysAchievements;

import java.util.ArrayList;

/**
 * Manages the monetery balance and also items unlocked in the store
 */
public class StoreManager {
    private int balance = 0;

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
        lastApplied = items.get(5);
        restoreItem();
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
        if (!Main.achievementManager.getAchievements().get("Piggy Bank").isCompleted() && balance > 100) {
            Main.achievementManager.getAchievements().get("Piggy Bank").setCompleted(screen, achievementsPane);
        }
        if (!Main.achievementManager.getAchievements().get("Savings Account").isCompleted() && balance > 100) {
            Main.achievementManager.getAchievements().get("Savings Account").setCompleted(screen, achievementsPane);
        }
        if (!Main.achievementManager.getAchievements().get("Ballin'").isCompleted() && balance > 100) {
            Main.achievementManager.getAchievements().get("Ballin'").setCompleted(screen, achievementsPane);
        }
        if (!Main.achievementManager.getAchievements().get("You Know This Isn't Real Money Right?").isCompleted() && balance > 100) {
            Main.achievementManager.getAchievements().get("You Know This Isn't Real Money Right?").setCompleted(screen, achievementsPane);
        }
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
     * @return
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

    public int numberItems() {
        return items.size();
    }



}
