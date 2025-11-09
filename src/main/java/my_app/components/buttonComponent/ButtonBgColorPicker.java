package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;

public class ButtonBgColorPicker extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();

    ColorPicker colorPicker = new ColorPicker(Color.WHITE);
    Text fontColorText = new Text(translation.backgroundColor() + ":");

    public ButtonBgColorPicker(ObjectProperty<Node> selectedNode) {

        config();

        ButtonComponent node = (ButtonComponent) selectedNode.get();

        // Obtém a cor de fundo atual
        String bgColor = Commons.getValueOfSpecificField(node.getStyle(), "-fx-background-color");

        // Se não encontrar, usa "blue" como padrão
        if (bgColor.isEmpty()) {
            bgColor = Commons.ButtonBgColorDefault;
        }

        // Inicializa o ColorPicker com a cor de fundo
        colorPicker.setValue(Color.web(bgColor));

        colorPicker.setOnAction(e -> {
            Color c = colorPicker.getValue();

            System.out.println("color picker: " + Commons.ColortoHex(c));

            // Obtém o estilo atual do botão
            String existingStyle = node.getStyle();

            System.out.println("existingStyle: " + existingStyle);

            // Atualiza apenas a cor de fundo
            String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-background-color",
                    Commons.ColortoHex(c));

            // Aplica o novo estilo
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
