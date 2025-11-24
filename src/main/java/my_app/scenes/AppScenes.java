package my_app.scenes;

import javafx.scene.Scene;
import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreen;

public class AppScenes {
    public static Scene PrimitiveListFormScene() {
        return new Scene(new PrimitiveListFormScreen());
    }
}
