package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class Beta1Wallpaper extends StoreItem {
    Beta1Wallpaper() {
        itemname = "Beta1 Default Wallpaper";
        itemdescription = "The default wallpaper from Tātai Beta 1, Hooker Lake";

        icon = FontAwesomeIcon.IMAGE;
        cost = 0;
    }

    public void applyChanges() {
        if (purchased) {
            String imageFile = "tatai/app/resources/bkgndb1.jpg";
            Main.background = new Image(Main.class.getClassLoader().getResourceAsStream(imageFile));
        }
    }
}
