package my_app.components.imageComponent;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import my_app.contexts.TranslationContext;

public class PreserveRatioComponent extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    Text title = new Text(translation.preserveRatio() + ":");
    ChoiceBox<String> choiceBox = new ChoiceBox<>(
            FXCollections.observableArrayList("True", "False"));

    public PreserveRatioComponent(ImageComponent node) {
        config();

        // Define o valor inicial com base no estado atual da imagem
        choiceBox.setValue(String.valueOf(node.isPreserveRatio()));

        // Listener de mudanças
        choiceBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null)
                return;

            boolean preserve = newVal.equalsIgnoreCase("true");
            node.setPreserveRatio(preserve);
        });

        getChildren().addAll(title, choiceBox);
    }

    void config() {
        title.setFont(Font.font(14));
        title.setFill(Color.WHITE);
        setSpacing(10);
    }
}