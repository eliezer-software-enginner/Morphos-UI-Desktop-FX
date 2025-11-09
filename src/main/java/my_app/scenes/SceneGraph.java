package my_app.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneGraph {

    public Scene settingsScene() {
        Stage stage = new Stage();
        Scene scene = new Scene(new VBox());
        stage.show();

        return scene;
    }
}
