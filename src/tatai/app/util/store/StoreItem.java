package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import tatai.app.Main;

/**
 * Represents an item in the store for purchase
 *
 * @author Edward
 */
public abstract class StoreItem {
    String itemname;
    String itemdescription;

    FontAwesomeIcon icon;

    int cost;
    boolean purchased = false;

    /**
     * Checks whether this store item can be purchased (can afford it & isn't already purchased)
     * @return Whether it's purchasable
     */
    public boolean isPurchaseable() {
        return (Main.store.getBalance() >= cost && !purchased);
    }

    /**
     * Checks whether this item has already been purchased
     * @return Whether it's already purchased
     */
    public boolean isPurchased() {
        return purchased;
    }

    /**
     * Purchases the item. Won't purchase if insufficent funds
     */
    public void purchase() {
        if (!purchased && Main.store.debit(cost)) {
            purchased = true;
        }
    }

    /**
     * Applies this item to the current environment (eg. apply the wallpaper)
     */
    public abstract void applyChanges();
}
