package layouts_generated_preview.app_negocio_facil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


// -------------------------------------------------------------------
// VIEW MODEL - Contém a lógica de negócio e handlers de eventos
// -------------------------------------------------------------------
class testeViewModel {

    // Construtor, injeção de dependências, etc., podem ser adicionados aqui

    public void onClickSignin() {
        // Lógica de onClickSignin
        IO.println("ola mundo");
    }
}


class Screen extends Pane {

    testeViewModel testeViewModel = new testeViewModel();


    Text text1 = new Text("Seja bem vindo ao négocio fácil");
    Text text2 = new Text("Entre com suas informações abaixo para realizar o login");
    Button btn1 = new Button("Acessar");
    ImageView imgV1 = new ImageView();
    TextField input1 = new TextField("");
    TextField input2 = new TextField("");

    {

        getChildren().addAll(
                text1,
                text2,
                btn1,
                imgV1,
                input1,
                input2
        );
        setup();
        styles();

        // Lógica de Eventos de Clique (usando ViewModel)
        btn1.setOnMouseClicked(e -> testeViewModel.onClickSignin());

    }

    void setup() {
        this.setPrefSize(800, 600);
        text1.setLayoutX(287.039063);
        text1.setLayoutY(174.738281);
        text2.setLayoutX(200.343750);
        text2.setLayoutY(204.738281);
        btn1.setLayoutX(345.980469);
        btn1.setLayoutY(359.000000);
        final var url = "file:/home/eliezer/Documents/my-projects/Morphos-UI-Desktop-FX/target/classes/assets/images/mago.jpg";
        imgV1.setFitWidth(100);
        imgV1.setFitHeight(100);
        imgV1.setImage(new Image(url));
        imgV1.setLayoutX(350.000000);
        imgV1.setLayoutY(36.000000);
        input1.setPromptText("your login");
        input1.setLayoutX(300.390625);
        input1.setLayoutY(236.000000);
        input2.setPromptText("your password");
        input2.setLayoutX(300.390625);
        input2.setLayoutY(296.000000);
    }

    void styles() {
        setStyle("-fx-background-color:white;");
        text1.setStyle("-fx-fill:black;-fx-font-size:16;-fx-font-weight:normal;");
        text2.setStyle("-fx-fill:black;-fx-font-size:16;-fx-font-weight:normal;");
        btn1.setStyle("-fx-background-color:#664db3;-fx-padding:10 10 10 10;-fx-font-weight:normal;-fx-background-radius:3;-fx-border-radius:3;-fx-text-fill:white;-fx-font-size:16;-fx-border-width:0;-fx-border-color:transparent;");
        imgV1.setStyle("");
        input1.setStyle("-fx-text-fill:black;-fx-font-size:16;-fx-font-weight:normal;-fx-prompt-text-fill:gray;-fx-focus-color:black;-fx-text-box-border:black;");
        input2.setStyle("-fx-text-fill:black;-fx-font-size:16;-fx-font-weight:normal;-fx-prompt-text-fill:gray;-fx-focus-color:black;-fx-text-box-border:black;");
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

    static void main(String[] args) {
        launch(args);
    }
}
