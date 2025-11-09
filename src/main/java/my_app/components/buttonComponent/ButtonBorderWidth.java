package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;

public class ButtonBorderWidth extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    Text title = new Text(translation.borderWidth() + ":");
    TextField tf = new TextField();

    public ButtonBorderWidth(ObjectProperty<Node> selectedNode) {

        config();

        ButtonComponent node = (ButtonComponent) selectedNode.get();

        // Obtém a largura da borda atual, se não houver valor, usa "0px" como padrão
        String currentBorderWidth = Commons.getValueOfSpecificField(node.getStyle(), "-fx-border-width");

        if (currentBorderWidth.isEmpty()) {
            currentBorderWidth = "0px"; // Define o valor padrão como "0px"
        }

        // Inicializa o TextField com o valor atual de border-width
        tf.setText(currentBorderWidth);

        tf.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isBlank()) {
                try {
                    // Valida se o valor inserido possui uma unidade de medida
                    if (!newVal.matches("\\d+(px|em|pt)?")) { // Permite valores numéricos seguidos de px, em ou pt
                        return; // Se não for válido, ignora a alteração
                    }

                    String existingStyle = node.getStyle();

                    // Atualiza o estilo com a nova largura da borda
                    String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-border-width", newVal);

                    // Aplica o novo estilo ao botão
                    node.setStyle(newStyle);

                } catch (NumberFormatException ignored) {
                }
            }
        });

        getChildren().addAll(title, tf);
    }

    void config() {
        title.setFont(Font.font(14));
        title.setFill(Color.WHITE);
        setSpacing(10);
    }

}
