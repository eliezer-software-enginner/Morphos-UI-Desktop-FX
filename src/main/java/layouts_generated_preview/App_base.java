package layouts_generated_preview;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Component1 extends Pane {
    Button button1 = new Button("Im new here");
    Text text1 = new Text("Im new here");
    {

        getChildren().addAll(
                button1,
                text1);
        setup();
        styles();
    }

    void setup() {
        this.setPrefSize(370, 60);
        button1.setLayoutX(262.459381);
        button1.setLayoutY(8.000000);
        text1.setLayoutX(0.000000);
        text1.setLayoutY(35.000000);
    }

    void styles() {
        setStyle("-fx-background-color:transparent;");
        button1.setStyle(
                "-fx-background-color:#664db3;-fx-padding:10 10 10 10;-fx-font-weight:normal;-fx-background-radius:3;-fx-border-radius:3;-fx-text-fill:white;-fx-font-size: 16;-fx-border-width: 0;");
        text1.setStyle("-fx-fill:black;-fx-font-size:16;-fx-font-weight:normal;");
    }

}

class Screen extends Pane {
    Button button1 = new Button("Im new here");
    Component1 component1 = new Component1();
    {

        getChildren().addAll(
                button1,
                component1);
        setup();
        styles();
    }

    void setup() {
        this.setPrefSize(800, 600);
        button1.setLayoutX(97.000000);
        button1.setLayoutY(51.000000);
        component1.setLayoutX(112.000000);
        component1.setLayoutY(143.000000);
    }

    void styles() {
        setStyle("-fx-background-color:#d6d2e4ff;");
        button1.setStyle(
                "-fx-background-color:#664db3;-fx-padding:10 10 10 10;-fx-font-weight:normal;-fx-background-radius:3;-fx-border-radius:3;-fx-text-fill:white;-fx-font-size: 16;-fx-border-width: 0;");
        component1.setStyle("-fx-background-color:transparent;");
    }

}

// here has the purpose of be an entrypoint of code genereated
public class App_base extends Application {

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
