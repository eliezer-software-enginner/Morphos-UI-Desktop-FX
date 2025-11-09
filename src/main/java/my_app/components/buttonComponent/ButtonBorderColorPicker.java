package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;

public class ButtonBorderColorPicker extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ColorPicker colorPicker = new ColorPicker(Color.WHITE);
    Text fontColorText = new Text(translation.borderColor() + ":");

    public ButtonBorderColorPicker(ObjectProperty<Node> selectedNode) {

        config();

        ButtonComponent node = (ButtonComponent) selectedNode.get();

        String borderColor = Commons.getValueOfSpecificField(node.getStyle(), "-fx-border-color");

        // Se não encontrar uma cor de borda, usa "black" como padrão
        if (borderColor.isEmpty()) {
            borderColor = "transparent";
        }

        // Inicializa o ColorPicker com a cor da borda atual
        colorPicker.setValue(Color.web(borderColor));

        colorPicker.setOnAction(e -> {
            Color c = colorPicker.getValue();

            // Obtém o estilo atual do botão
            String existingStyle = node.getStyle();

            // Atualiza o estilo com a nova cor da borda
            String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-border-color",
                    Commons.ColortoHex(c));

            // Aplica o novo estilo no botão
            node.setStyle(newStyle);
        });

        getChildren().addAll(fontColorText, colorPicker);

    }

    void config() {
        fontColorText.setFont(Font.font(14));
        fontColorText.setFill(Color.WHITE);

        setSpacing(10);
    }
}
