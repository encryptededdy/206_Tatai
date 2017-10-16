package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class Beta2Wallpaper extends StoreItem {
    Beta2Wallpaper() {
        itemname = "Beta2 Default Wallpaper";
        itemdescription = "The default wallpaper from Tātai Beta 2, Tasman Lake";

        icon = FontAwesomeIcon.IMAGE;
        cost = 0;
    }

    public void applyChanges() {
        if (purchased) {
            String imageFile = "tatai/app/resources/bkgndb2.jpg";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
