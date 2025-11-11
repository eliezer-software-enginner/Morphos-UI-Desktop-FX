package my_app.components;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;


public class Components {
    public static Region spacerVertical(int insets) {
        var region = new Region();
        region.setMinHeight(insets);
        region.setMaxHeight(insets);
        region.setPrefHeight(insets);
        return region;
    }

    public static Button buttonRemove(String text) {
        var btn = new Button(text);
        btn.getStylesheets().add("btn-remove");
        return btn;
    }
}
