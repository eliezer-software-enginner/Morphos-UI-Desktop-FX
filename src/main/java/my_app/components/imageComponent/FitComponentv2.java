package my_app.components.imageComponent;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FitComponentv2 extends HBox {

    Text title = new Text("Object Fit:");
    ChoiceBox<String> choiceBox = new ChoiceBox<>(
            FXCollections.observableArrayList("contain", "cover", "fill"));

    public FitComponentv2(ImageComponentv2 node) {
        config();

        // Define o valor atual no ChoiceBox
        choiceBox.setValue(node.fitMode.get().name().toLowerCase());

        // Listener para mudança de valor
        choiceBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null)
                return;

            FitMode selectedMode = FitMode.valueOf(newVal.toUpperCase());
            node.fitMode.set(selectedMode);

            // Aplica o comportamento conforme o modo
            applyFitMode(node, selectedMode);
        });

        getChildren().addAll(title, choiceBox);
    }

    /**
     * Aplica o comportamento de visualização de acordo com o modo
     */
    private void applyFitMode(ImageComponentv2 img, FitMode mode) {
        double width = img.getFitWidth();
        double height = img.getFitHeight();

        img.setPreserveRatio(true);
        img.setClip(null); // reseta qualquer recorte anterior

        switch (mode) {
            case CONTAIN -> {
                img.setPreserveRatio(true);
            }
            case COVER -> {
                img.setPreserveRatio(true);
                img.setClip(new Rectangle(width, height)); // corta o excesso
            }
            case FILL -> {
                img.setPreserveRatio(false);
            }
        }
    }

    void config() {
        title.setFont(Font.font(14));
        title.setFill(Color.WHITE);
        setSpacing(10);
    }
}
