package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class XPWallpaper extends StoreItem {
    XPWallpaper() {
        itemname = "Bliss Wallpaper";
        itemdescription = "Change your background image to the famous default wallpaper from Microsoft Windows XP. " +
                "Faithfully reproduced in Microsoft Paint by Henry Li";

        icon = FontAwesomeIcon.IMAGE;
        cost = 200;
    }

    public void applyChanges() {
        if (purchased) {
            Main.parallaxMode = false;
            String imageFile = "tatai/app/resources/bliss.png";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
