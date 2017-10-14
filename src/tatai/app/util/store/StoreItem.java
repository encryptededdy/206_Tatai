package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import tatai.app.Main;

/**
 * Represents an item in the store for purchase
 */
public abstract class StoreItem {
    String itemname;
    String itemdescription;

    FontAwesomeIcon icon;

    int cost;
    boolean purchased = false;

    public boolean isPurchaseable() {
        return (Main.store.getBalance() >= cost && !purchased);
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void purchase() {
        if (!purchased && Main.store.debit(cost)) {
            purchased = true;
        }
    }

    public abstract void applyChanges();
}
