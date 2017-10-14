package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class XPWallpaperLogo extends StoreItem {
    XPWallpaperLogo() {
        itemname = "Bliss Wallpaper w/Logo";
        itemdescription = "Change your background image to the famous default wallpaper from Microsoft Windows XP, including the \"Windows xD\" logo. " +
                "Faithfully reproduced in Microsoft Paint by Henry Li";

        icon = FontAwesomeIcon.IMAGE;
        cost = 250;
    }

    public void applyChanges() {
        if (purchased) {
            String imageFile = "tatai/app/resources/blissLogo.png";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
