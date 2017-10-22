package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import tatai.app.Main;

public class SpaceParallax extends StoreItem {
    SpaceParallax() {
        itemname = "NZ from Space Parallax Wallpaper";
        itemdescription = "The South Island of New Zealand as seen from the International Space Station. Supports Parallax effect (beta).";

        icon = FontAwesomeIcon.MAP_ALT;
        cost = 700;
    }

    public void applyChanges() {
        if (purchased) {
            Main.parallaxMode = true;
            String frontImageFile = "tatai/app/resources/spaceParallax/front.png";
            String backImageFile = "tatai/app/resources/spaceParallax/back.png";
            Main.parralaxFront = new Image(Main.class.getClassLoader().getResourceAsStream(frontImageFile));
            Main.parralaxBack = new Image(Main.class.getClassLoader().getResourceAsStream(backImageFile));
        }
    }
}
