package my_app.scenes.SplashScene;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import my_app.contexts.TranslationContext;
import my_app.scenes.MainScene.MainScene;

import java.util.Objects;

public class SplashScene extends Scene {

    TranslationContext translation = TranslationContext.instance();

    ImageView logo = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/m.png"))));
    Text title = new Text(translation.get().splashTranslation().title());
    Text description = new Text(translation.get().splashTranslation().description());
    Text footer = new Text(translation.get().splashTranslation().footer());

    VBox layout = new VBox();
    VBox titleAndDescriptionContainer = new VBox(title, description);
    Stage stage = new Stage();

    public SplashScene(Stage stage) {
        super(new VBox(), 500, 400);

        this.stage = stage;

        layout.getChildren().addAll(logo, titleAndDescriptionContainer, footer);

        setRoot(layout);

        setup();
        styles();
        defineAnimation();
    }

    void setup() {
        stage.setResizable(false);

        logo.setFitHeight(200);
        logo.setFitWidth(200);

        layout.setAlignment(Pos.CENTER);
        description.setWrappingWidth(300);

        titleAndDescriptionContainer.setMaxWidth(description.getWrappingWidth());

        VBox.setMargin(footer, new Insets(30, 0, 0, 0));
    }

    void styles() {
        layout.setStyle("-fx-background-color:#15161A;");
        title.setStyle("-fx-font-size:40px;-fx-fill:white;");
        description.setStyle("-fx-font-size:17px;-fx-fill:white;");
        footer.setStyle("-fx-font-size:11px;-fx-fill:#92CFA7;");
    }

    void defineAnimation() {

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
            stage.setResizable(true);

            stage.setScene(new MainScene());
            stage.centerOnScreen();
        });
    }

}
