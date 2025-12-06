package my_app.components.imageComponent;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import my_app.contexts.TranslationContext;
import my_app.themes.Typography;
import toolkit.Component;

import java.io.File;

public class ImageBackgroundComponentv2 extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    Text title = new Text("Background:");

    @Component
    TextField tf = new TextField();

    @Component
    Button btnChooseImg = new Button(translation.chooseImage());
    @Component
    VBox column = new VBox(tf, btnChooseImg);

    public ImageBackgroundComponentv2(ImageComponentv2 node) {

        config();

        var stage = node.stage;

        tf.textProperty().addListener((_, _, newVal) -> {

            if (newVal.isBlank() || !newVal.startsWith("http"))
                return;

            try {
                // Carrega a imagem com cache desativado e carregamento síncrono
                Image image = new Image(newVal, true);
                node.setImage(image);
                node.errorContainer.getChildren().clear();

                // Escuta se deu erro após o carregamento em background
                image.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        var message = "Erro ao carregar imagem: " + image.getException().getMessage();
                        System.err.println(message);

                        var delay = new PauseTransition(Duration.millis(700));
                        var errorText = Typography.error(message);
                        errorText.setWrapText(true);

                        delay.setOnFinished(_ -> node.errorContainer.getChildren().add(errorText));
                        delay.play();
                    } else {
                        node.errorContainer.getChildren().clear();
                    }
                });

            } catch (Exception err) {
                var message = "Erro ao carregar imagem: " + err.getMessage();
                System.err.println(message);

                PauseTransition delay = new PauseTransition(Duration.millis(700));

                var errorText = Typography.error(message);
                errorText.setWrapText(true);

                delay.setOnFinished(_ -> node.errorContainer.getChildren().add(errorText));
                delay.play();
            }

        });

        btnChooseImg.setOnAction(_ -> searchImageLocally(node, stage));

        getChildren().addAll(title, column);
    }

    void searchImageLocally(ImageComponentv2 node, Stage stage) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open as");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(stage);

        if (file == null)
            return;

        final var imagePath = file.toURI().toString();
        node.setImage(new Image(imagePath));
        tf.setText(imagePath);
    }

    void config() {
        title.setFont(Font.font(14));
        title.setFill(Color.WHITE);
        setSpacing(10);
    }
}
