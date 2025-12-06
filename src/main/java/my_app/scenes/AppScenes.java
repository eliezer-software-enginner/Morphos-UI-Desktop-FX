package my_app.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.DataTableScreen.DataTableScreen;
import my_app.screens.Home.Home;
import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreen;
import my_app.screens.ScreenCreateProject.ScreenCreateProject;
import my_app.screens.ShowCodeScreen.ShowCodeScreen;
import my_app.screens.SplashScreen.SplashScreen;
import my_app.themes.ThemeManager;

import java.nio.file.Path;

public class AppScenes {

    public static Scene SplashScene(ComponentsContext mainComponentsContext, Stage theirStage) {
        var scene = new Scene(new SplashScreen(mainComponentsContext, theirStage));

        var screenSize = Commons.ScreensSize._900x500;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.setResizable(false);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }


    public static Scene CreateProjectScene(ComponentsContext mainComponentsContext, Stage theirStage) {
        var scene = new Scene(new ScreenCreateProject(mainComponentsContext, theirStage));

        var screenSize = Commons.ScreensSize._700x500;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.setResizable(false);
        theirStage.centerOnScreen();

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);

        scene.getStylesheets().add(Path.of("/components.css").toFile().getAbsolutePath());
        return scene;
    }

    public static Scene HomeScene(ComponentsContext mainComponentsContext, Stage theirStage) {
        var scene = new Scene(new Home(theirStage, mainComponentsContext, false));

        var screenSize = Commons.ScreensSize.LARGE;
        theirStage.setWidth(1410);
        theirStage.setHeight(screenSize.heigh);
        theirStage.centerOnScreen();
        theirStage.setResizable(true);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }

    public static Scene DataTableScene(ComponentsContext mainComponentsContext, Stage theirStage) {
        var scene = new Scene(new DataTableScreen(mainComponentsContext));

        var screenSize = Commons.ScreensSize._1200x650;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.centerOnScreen();
        theirStage.setResizable(true);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }

    public static Scene PrimitiveListFormScene(Stage theirStage, Runnable callack) {
        var scene = new Scene(new PrimitiveListFormScreen(callack));
        var screenSize = Commons.ScreensSize._1200x650;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.centerOnScreen();
        theirStage.setResizable(true);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }

    public static Scene ShowCodeFormScene(ComponentsContext mainComponentsContext, Stage theirStage,
                                          CanvaComponent canvaComponent) {
        var scene = new Scene(new ShowCodeScreen(mainComponentsContext, canvaComponent));
        var screenSize = Commons.ScreensSize._1200x650;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.centerOnScreen();
        theirStage.setResizable(true);
        theirStage.setTitle("Showing code");

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }
}
