package my_app.components.buttonComponent;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.components.ButtonComponent;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.Typography;
import toolkit.Component;

public class ButtonBorderRadius extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    Label title = Typography.caption(translation.borderRadius() + ":");
    @Component
    TextField tf = new TextField();

    public ButtonBorderRadius(ButtonComponent node) {
        setSpacing(10);

        String currentBorderRadius = Commons.getValueOfSpecificField(node.getStyle(), "-fx-border-radius");

        if (currentBorderRadius.isEmpty()) {
            currentBorderRadius = Commons.ButtonRadiusDefault;
        }

        // Inicializa o borderRadius do Button no TextField
        tf.setText(currentBorderRadius);

        tf.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isBlank()) {
                try {
                    String existingStyle = node.getStyle();

                    // Atualiza tanto o -fx-background-radius quanto o -fx-border-radius
                    String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-background-radius", newVal);
                    newStyle = Commons.UpdateEspecificStyle(newStyle, "-fx-border-radius", newVal); // Garante que a
                    // borda tenha o
                    // mesmo raio
                    // Aplica o estilo com a modificação
                    node.setStyle(newStyle);

                } catch (NumberFormatException ignored) {
                }
            }
        });

        getChildren().addAll(title, tf);
    }
}
