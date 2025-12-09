package my_app.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.data.Commons;
import my_app.screens.DataTableScreen.DataTableScreen;
import my_app.screens.Home.Home;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreen;
import my_app.screens.ScreenCreateProject.ScreenCreateProject;
import my_app.screens.ShowCodeScreen.ShowCodeScreen;
import my_app.screens.SplashScreen.SplashScreen;
import my_app.themes.ThemeManager;

import java.nio.file.Path;

public class AppScenes {

    final static String componentsCssFile = Path.of("/components.css").toFile().getAbsolutePath();
    final static String tabCssFile = Path.of("/tab.css").toFile().getAbsolutePath();
    final static String screenCreateProjectCssFile = Path.of("/screen-create-project.css").toFile().getAbsolutePath();


    public static void SwapScene(Stage currentWindow, Scene sceneToGoTo) {
        currentWindow.setScene(sceneToGoTo);
    }

    public static Scene SplashScene(Stage theirStage) {
        var scene = new Scene(new SplashScreen(theirStage));

        var screenSize = Commons.ScreensSize._900x500;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.setResizable(false);
        theirStage.centerOnScreen();

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        return scene;
    }


    public static Scene CreateProjectScene(Stage theirStage) {
        var scene = new Scene(new ScreenCreateProject(theirStage));

        var screenSize = Commons.ScreensSize._900x560;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.setResizable(true);
        theirStage.centerOnScreen();

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);

        scene.getStylesheets().add(screenCreateProjectCssFile);
        return scene;
    }

    public static Scene HomeScene(Stage theirStage) {
        var scene = new Scene(new Home(theirStage, false));

        var screenSize = Commons.ScreensSize._1500x900;
        theirStage.setWidth(screenSize.width);
        theirStage.setHeight(screenSize.heigh);
        theirStage.centerOnScreen();
        theirStage.setResizable(true);

        Commons.UseDefaultStyles(scene);
        ThemeManager.Instance().addScene(scene);
        scene.getStylesheets().add(tabCssFile);

        return scene;
    }

    public static Scene DataTableScene(Stage theirStage) {
        var scene = new Scene(new DataTableScreen());

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

    public static Scene ShowCodeFormScene(HomeViewModel homeViewModel,
                                          Stage theirStage,
                                          CanvaComponentV2 canvaComponent) {
        var scene = new Scene(new ShowCodeScreen(homeViewModel, canvaComponent));
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
