package my_app.screens.SplashScreen;

import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import my_app.FileManager;
import my_app.contexts.ComponentsContext;
import my_app.scenes.AppScenes;

import java.io.File;

public class SplashScreenViewModel {

    private final ComponentsContext mainComponentsContext;
    private final Stage stage;

    public SplashScreenViewModel(ComponentsContext mainComponentsContext, Stage theirStage) {
        this.mainComponentsContext = mainComponentsContext;
        this.stage = theirStage;
    }

    void animateLogo(ImageView logo) {

        ScaleTransition scale = new ScaleTransition(Duration.seconds(1));
        scale.setNode(logo);
        scale.setFromX(1);
        scale.setFromY(1);

        scale.setToX(0.5);
        scale.setToY(0.5);

        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();

        scale.setOnFinished(_ -> {
            try {
                //acessar o arquivo de projeto
                final var prefsData = FileManager.loadDataInPrefs();
                final var absolutePath = prefsData.last_project_saved_path();
                final var projectFile = new File(absolutePath);
                if (!projectFile.exists()) {
                    stage.setScene(AppScenes.CreateProjectScene(stage));
                    stage.centerOnScreen();
                    return;
                }

                stage.setScene(AppScenes.HomeScene(stage));
            } catch (Exception e) {
                stage.setScene(AppScenes.CreateProjectScene(stage));
                stage.centerOnScreen();
            }
        });
    }
}
