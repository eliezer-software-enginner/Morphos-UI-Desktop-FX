package my_app.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.inputComponents.InputComponent;
import my_app.data.Commons;


public class Components {
    public static Region spacerVertical(int insets) {
        var region = new Region();
        region.setMinHeight(insets);
        region.setMaxHeight(insets);
        region.setPrefHeight(insets);
        return region;
    }

    public static Button buttonRemove(String text) {
        var btn = new Button(text);
        btn.getStylesheets().add("btn-remove");
        return btn;
    }

    public static HBox ColorPickerRow(String title, Node selectedNode, String cssField) {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);

        HBox root = ItemRow(colorPicker, title);

        String color = "transparent";

        if (selectedNode instanceof InputComponent node) {
            color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            IO.println(cssField + ": " + color);
        } else if (selectedNode instanceof ButtonComponent node) {
            color = Commons.getValueOfSpecificField(node.getStyle(), "-fx-border-color");

            // Se não encontrar uma cor de borda, usa "black" como padrão

        }

        // Inicializa o ColorPicker com a cor da borda atual
        colorPicker.setValue(Color.web(color));

        colorPicker.setOnAction(e -> {
            Color c = colorPicker.getValue();
            String existingStyle = selectedNode.getStyle();

            // Atualiza o estilo com a nova cor da borda
            String newStyle = Commons.UpdateEspecificStyle(existingStyle, cssField,
                    Commons.ColortoHex(c));

            // Aplica o novo estilo no botão
            selectedNode.setStyle(newStyle);
        });

        return root;
    }

    private static HBox ItemRow(Node node, String text) {
        Text titleText = new Text();
        titleText.setText(text + ":");
        titleText.setFont(Font.font(14));
        titleText.setFill(Color.WHITE);

        HBox root = new HBox(titleText, node);
        root.setSpacing(10);
        return root;
    }
}
