package my_app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.scenes.AppScenes;
import my_app.scenes.MainScene.MainScene;

import java.util.Locale;
import java.util.Objects;

public class App extends Application {

    public static Stage stage;
    TranslationContext translationContext;

    @Override
    public void init() {
        translationContext = TranslationContext.instance();
        translationContext.onEntryPoint(this);

        var prefsData = Commons.getPrefsData();

        if (prefsData != null) {
            translationContext.loadTranslation(Locale.of(prefsData.language()));
        } else {
            translationContext.loadTranslation(Locale.getDefault());
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        this.stage.setTitle(Commons.AppName + " " + Commons.AppVersion);
        this.stage.setMinWidth(Commons.ScreensSize.LARGE.width);

        //Scene splashScene = new SplashScene(primaryStage);
        //this.stage.setScene(splashScene);

        final var componentsContext = new ComponentsContext();
        //final var mainWindow = new MainWindow(componentsContext);
        //mainWindow.show();

        //final var mainScene = new MainScene();
        primaryStage.setScene(AppScenes.SplashScene(componentsContext, primaryStage));
        //primaryStage.setScene(AppScenes.CreateProjectScene(componentsContext, primaryStage));

        //this.stage.setScene(new IconsScene());

        // themeManager.addScene(mainScene);

        // getStylesheets().add(getClass().getResource("/global_styles.css").toExternalForm());
        this.stage.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            javafx.scene.Node node = event.getPickResult().getIntersectedNode();
            if (node != null) {
                System.out.println("Clique em: " + node.getClass().getSimpleName());
                // node.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0, 0, 0);");

                if (node.getId() != null)
                    System.out.println("ID: " + node.getId());
            }
        });

        // Cria a DataScene passando a referência da mainScene e do primaryStage
        //DataScene dataScene = new DataScene();
        //primaryStage.setScene(dataScene);
        //dataScene.show();
        // Botão muda para DataScene
        // componentData.setOnAction(e -> primaryStage.setScene(dataScene));
        this.stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/app_ico_window_32_32.png"))));

        primaryStage.show();

        // new WindowPrimitiveListForm().show();
    }

    public void changeLanguage(Locale locale) {
        translationContext.loadTranslation(locale);

        MainScene mainScene = new MainScene();
        stage.setScene(mainScene);
    }

    static void main(String[] args) {
        launch(args);
    }
}
