package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class AucklandParallax extends StoreItem {
    AucklandParallax() {
        itemname = "TEST: Auckland Satellite Parallax Wallpaper";
        itemdescription = "Test parallax image";

        icon = FontAwesomeIcon.MAP_ALT;
        cost = 0;
    }

    public void applyChanges() {
        if (purchased) {
            Main.parallaxMode = true;
            String frontImageFile = "tatai/app/resources/aucklandParallax/front.png";
            String backImageFile = "tatai/app/resources/aucklandParallax/back.png";
            Main.parralaxFront = new Image(Main.class.getClassLoader().getResourceAsStream(frontImageFile));
            Main.parralaxBack = new Image(Main.class.getClassLoader().getResourceAsStream(backImageFile));
        }
    }
}
