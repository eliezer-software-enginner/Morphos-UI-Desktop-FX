package my_app.windows;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.scenes.AppScenes;

public class WindowPrimitiveListForm extends Stage {
    Scene scene = AppScenes.PrimitiveListFormScene();

    public WindowPrimitiveListForm() {
        setScene(scene);
        setWidth(800);
        setHeight(500);
    }
}
