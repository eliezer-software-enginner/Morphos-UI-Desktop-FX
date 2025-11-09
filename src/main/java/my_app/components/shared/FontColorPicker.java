package my_app.components.shared;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.inputComponents.InputComponent;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.Typography;

public class FontColorPicker extends HBox {

    ColorPicker colorPicker = new ColorPicker(Color.WHITE);

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.fontColor() + ":");

    public FontColorPicker(ObjectProperty<Node> selectedNode) {

        config();

        Node node = selectedNode.get();

        String textFill = "";

        if (node instanceof ButtonComponent || node instanceof InputComponent) {
            textFill = Commons.getValueOfSpecificField(node.getStyle(), "-fx-text-fill");
        } else if (node instanceof TextComponent) {
            textFill = Commons.getValueOfSpecificField(node.getStyle(), "-fx-fill");
        }

        if (textFill.isEmpty()) {
            textFill = Commons.ButtonTextColorDefault;
        }

        colorPicker.setValue(Color.web(textFill));

        colorPicker.setOnAction(e -> {

            String existingStyle = node.getStyle();

            Color color = colorPicker.getValue();

            if (node instanceof ButtonComponent || node instanceof TextField) {

                String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-text-fill",
                        Commons.ColortoHex(color));

                node.setStyle(newStyle);

            } else if (node instanceof TextComponent) {
                String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-fill",
                        Commons.ColortoHex(color));

                node.setStyle(newStyle);
            }

        });

        getChildren().addAll(title, colorPicker);

    }

    void config() {

        setSpacing(10);
    }
}
