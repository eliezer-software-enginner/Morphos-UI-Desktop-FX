package my_app.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.Home.Home;
import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreen;
import my_app.screens.ScreenCreateProject.ScreenCreateProject;
import my_app.themes.ThemeManager;

import java.nio.file.Path;

public class AppScenes {
    public static Scene PrimitiveListFormScene() {
        return new Scene(new PrimitiveListFormScreen());
    }

    public static Scene CreateProjectScene(Stage theirStage) {
        var scene = new Scene(new ScreenCreateProject(theirStage));
        theirStage.setWidth(700);
        theirStage.setHeight(500);
        theirStage.setResizable(false);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        
        scene.getStylesheets().add(Path.of("/components.css").toFile().getAbsolutePath());
        return scene;
    }

    public static Scene HomeScene(ComponentsContext mainComponentsContext, Stage theirStage) {
        var scene = new Scene(new Home(theirStage, mainComponentsContext, false));

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }
}
