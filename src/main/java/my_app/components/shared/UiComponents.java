package my_app.components.shared;

import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;

public class UiComponents {
    public static Button ButtonPrimary(String text) {
        var b = new Button(text);
        b.getStyleClass().addAll("button-primary", "text-primary-color");
        return b;
    }

    public static Button ButtonSecondary(String text) {
        var b = new Button(text);
        b.getStyleClass().addAll("button-secondary", "text-primary-color");
        return b;
    }

    public static MenuBar MenuBarPrimary() {
        var mb = new MenuBar();
        mb.getStyleClass().add("background-color");
        return mb;
    }
}
