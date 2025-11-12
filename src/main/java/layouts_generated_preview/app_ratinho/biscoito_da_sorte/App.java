package layouts_generated_preview.app_ratinho.biscoito_da_sorte;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

class Screen extends Pane {
    ImageView imgV1 = new ImageView();
    Button button1 = new Button("Próxima");

    {

        getChildren().addAll(
                imgV1,
                button1
        );
        setup();
        styles();
    }

    void setup() {
        this.setPrefSize(700, 600);
        final var url = "file:/home/eliezer/Documents/my-projects/Morphos-UI-Desktop-FX/src/main/resources/assets/images/ratinhos/mouse.jpg";
        imgV1.setFitWidth(200);
        imgV1.setFitHeight(200);
        imgV1.setImage(new Image(url));
        imgV1.setLayoutX(250.000000);
        imgV1.setLayoutY(53.000000);
        button1.setLayoutX(295.660156);
        button1.setLayoutY(315.661792);
    }

    void styles() {
        setStyle("-fx-background-color:#ffffff;");
        imgV1.setStyle("");
        button1.setStyle("-fx-background-color: #2ecc71;-fx-padding: 10 25;-fx-font-weight:normal;-fx-background-radius: 8;-fx-border-radius: 8;-fx-text-fill:white;-fx-font-size: 16;-fx-border-width: 0;");
    }

}

// here has the purpose of be an entrypoint of code genereated
public class App extends Application {

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("Basic Desktop Builder");
        this.primaryStage.setScene(new Scene(new Screen()));

        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
