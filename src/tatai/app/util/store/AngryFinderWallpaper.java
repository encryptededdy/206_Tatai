package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class AngryFinderWallpaper extends StoreItem {
    AngryFinderWallpaper() {
        itemname = "Angry Finder Wallpaper";
        itemdescription = "Change your background image to a very angry version of the macOS Finder Logo. " +
                "Drawn in (no other than) Microsoft Paint by Henry Li";

        icon = FontAwesomeIcon.IMAGE;
        cost = 350;
    }

    public void applyChanges() {
        if (purchased) {
            String imageFile = "tatai/app/resources/angryfinder.png";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
