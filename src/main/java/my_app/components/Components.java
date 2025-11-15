package my_app.components;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.data.Commons;
import my_app.themes.Typography;

import java.util.function.Consumer;


public class Components {
    public static Region spacerVertical(int insets) {
        var region = new Region();
        region.setMinHeight(insets);
        region.setMaxHeight(insets);
        region.setPrefHeight(insets);
        return region;
    }

    public static Button ButtonPrimary() {
        var btn = new Button();
        btn.getStyleClass().addAll("button-primary", "body-typo");

        return btn;
    }

    public static Button buttonRemove(String text) {
        var btn = new Button(text);
        btn.getStylesheets().add("btn-remove");
        return btn;
    }


    public static Node LabelWithTextContent(String name, String currentTextContent, Consumer<String> consume) {
        TextField tf = new TextField();

        HBox root = ItemRow(tf, name);

        tf.setText(currentTextContent);
        tf.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isBlank()) {
                consume.accept(newVal.trim());
            }
        });

        return root;
    }

    //se o campo recebido não for um css então é java, mexeremos no node!
    public static Node LabelWithInput(String name, Node node, String fieldCss) {
        TextField tf = new TextField();

        HBox root = ItemRow(tf, name);

        if (fieldCss.equals("text-wrapping-width")) {
            if (node instanceof TextComponent component) {
                tf.setText(String.valueOf(component.getWrappingWidth()));
            }
        } else {
            String valueOfField = Commons.getValueOfSpecificField(node.getStyle(), fieldCss);
            tf.setText(valueOfField);
        }


        tf.textProperty().addListener((_, _, newVal) -> {
            if (!newVal.isBlank()) {
//                if (!newVal.matches("\\d+(px|em|pt)?")) { // Permite valores numéricos seguidos de px, em ou pt
//                    return; // Se não for válido, ignora a alteração
//                }
                try {
                    if (fieldCss.equals("text-wrapping-width")) {
                        if (node instanceof TextComponent component) {
                            //validate if is number
                            component.setWrappingWidth(Double.parseDouble(newVal.trim()));
                        }
                    } else {
                        String currentStyle = node.getStyle();
                        String newStyle = Commons.UpdateEspecificStyle(currentStyle,
                                fieldCss, newVal);
                        node.setStyle(newStyle);
                    }

                } catch (NumberFormatException ignored) {
                }
            }
        });

        return root;
    }


    public static HBox ToogleSwithItemRow(String title, Node selectedNode, Pane canvaFather) {
        Button btn = new Button("Centralize");

        HBox root = ItemRow(btn, title);

        btn.setOnAction(ev -> {
            Commons.CentralizeComponent(selectedNode, canvaFather);
        });

        return root;
    }

    public static HBox LabelWithComboBox(String title, Node selectedNode, String cssField) {
        ComboBox<String> comboBox = new ComboBox<>();

        HBox root = ItemRow(comboBox, title);

        if (cssField.equals("clip-image-as-circle")) {
            if (selectedNode instanceof ImageComponent component) {
                comboBox.setItems(FXCollections.observableArrayList("Circle"));

                //se tem type de clip definido
                if (component.clipType != null) comboBox.setValue(component.clipType);

                comboBox.setOnAction(ev -> {
                    var value = comboBox.getValue();
                    if (value.equals("Circle")) {
                        //validar se a largura e tamanho são o mesmo
                        //var size = component.getFitWidth() / 2;

                        var size = component.getFitWidth() / 2;
                        component.setClip(new Circle(size, size, size));
                        component.clipType = "Circle";
                    }
                });
            }
        }

        return root;
    }


    public static HBox ColorPickerRow(String title, Node selectedNode, String cssField) {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);

        HBox root = ItemRow(colorPicker, title);

        String color = "transparent";

        if (selectedNode instanceof InputComponent node) {
            color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            IO.println(cssField + ": " + color);
        } else if (selectedNode instanceof ButtonComponent node) {
            color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
        } else if (selectedNode instanceof TextComponent node) {
            color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
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
        Label title = Typography.caption(text + ":");

        HBox root = new HBox(title, node);
        root.setSpacing(10);
        return root;
    }


}
