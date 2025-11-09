package my_app.components.imageComponent;

import java.io.File;

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
import my_app.contexts.TranslationContext;
import toolkit.Component;

public class ImageBackgroundComponent extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    Text title = new Text("Background:");

    @Component
    TextField tf = new TextField();

    @Component
    Button btnChooseImg = new Button(translation.chooseImage());
    @Component
    VBox column = new VBox(tf, btnChooseImg);

    public ImageBackgroundComponent(ImageComponent node) {

        config();

        var stage = node.stage;

        tf.textProperty().addListener((_, _, newVal) -> {

            if (newVal.isBlank() || !newVal.startsWith("http"))
                return;

            try {

                // Carrega a imagem com cache desativado e carregamento síncrono
                Image image = new Image(newVal, false);
                node.setImage(image);

                if (image.isError()) {
                    System.err.println("Erro ao carregar imagem: " + image.getException());
                }

            } catch (Exception err) {
                System.err.println("Falha ao carregar imagem: " + err.getMessage());
            }

        });

        btnChooseImg.setOnAction(_ -> searchImageLocally(node, stage));

        getChildren().addAll(title, column);
    }

    void searchImageLocally(ImageComponent node, Stage stage) {
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
