package my_app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import my_app.data.Commons;
import my_app.scenes.SplashScene.SplashScene;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(Commons.AppName + " " + Commons.AppVersion);

        Scene splashScene = new SplashScene(primaryStage);
        primaryStage.setScene(splashScene);

        // Scene mainScene = new MainScene();
        // primaryStage.setScene(mainScene);

        // themeManager.addScene(mainScene);

        // getStylesheets().add(getClass().getResource("/global_styles.css").toExternalForm());
        primaryStage.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            javafx.scene.Node node = event.getPickResult().getIntersectedNode();
            if (node != null) {
                System.out.println("Clique em: " + node.getClass().getSimpleName());
                // node.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0, 0, 0);");

                if (node.getId() != null)
                    System.out.println("ID: " + node.getId());
            }
        });

        // Cria a DataScene passando a referência da mainScene e do primaryStage
        // DataScene dataScene = new DataScene(primaryStage, mainScene);

        // Botão muda para DataScene
        // componentData.setOnAction(e -> primaryStage.setScene(dataScene));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/app_ico_window_32_32.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
