package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class MtCookParallax extends StoreItem {
    MtCookParallax() {
        itemname = "Aoraki Mt Cook Parallax Wallpaper";
        itemdescription = "A photo from the slops of Aoraki / Mt Cook. Supports Parallax effect (beta).";

        icon = FontAwesomeIcon.MAP_ALT;
        cost = 0;
    }

    public void applyChanges() {
        if (purchased) {
            Main.parallaxMode = true;
            String frontImageFile = "tatai/app/resources/mtCookParallax/front.png";
            String backImageFile = "tatai/app/resources/mtCookParallax/back.png";
            Main.parralaxFront = new Image(Main.class.getClassLoader().getResourceAsStream(frontImageFile));
            Main.parralaxBack = new Image(Main.class.getClassLoader().getResourceAsStream(backImageFile));
        }
    }
}
