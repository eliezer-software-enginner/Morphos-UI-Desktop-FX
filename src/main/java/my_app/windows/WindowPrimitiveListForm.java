package my_app.windows;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.data.Commons;
import my_app.scenes.AppScenes;
import my_app.themes.ThemeManager;

public class WindowPrimitiveListForm extends Stage {
    Scene scene = AppScenes.PrimitiveListFormScene();

    public WindowPrimitiveListForm() {
        setScene(scene);
        setWidth(800);
        setHeight(500);

        setup();
    }

    public void setup() {
        ThemeManager.Instance().addScene(scene);
        Commons.UseDefaultStyles(scene);
    }
}
