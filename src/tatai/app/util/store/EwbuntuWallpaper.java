package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class EwbuntuWallpaper extends StoreItem {
    EwbuntuWallpaper() {
        itemname = "Ewbuntu Wallpaper";
        itemdescription = "The logo of the world's most popular linux distribution, reproduced in Microsoft Paint by Henry Li";

        icon = FontAwesomeIcon.IMAGE;
        cost = 350;
    }

    public void applyChanges() {
        if (purchased) {
            String imageFile = "tatai/app/resources/ewbuntu.png";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
