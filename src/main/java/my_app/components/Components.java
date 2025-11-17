package my_app.components;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.scenes.IconsScene;
import my_app.themes.Typography;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

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
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        return btn;
    }

    public static Button ButtonPrimary(String text) {
        var btn = new Button(text);
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        return btn;
    }

    public static Button ButtonPrimary(String text, Runnable onClick) {
        var btn = new Button(text);
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        btn.setOnMouseClicked(_ -> onClick.run());

        return btn;
    }


    public static Button buttonRemove(String text) {
        var btn = new Button(text);
        btn.getStylesheets().add("btn-remove");
        return btn;
    }

    public static Node ButtonChooseGraphicContent(ButtonComponent nodeTarget) {
        var btn = ButtonPrimary("Choose icon");
        HBox root = new HBox(5, btn);

        var loadedIcon = nodeTarget.getGraphic();

        if (loadedIcon instanceof FontIcon ic) {
            root.getChildren().add(FontIcon.of(ic.getIconCode(), ic.getIconSize(), (Color) ic.getIconColor()));
        }

        root.setSpacing(10);
        root.setAlignment(Pos.CENTER_LEFT);

        btn.setOnMouseClicked(ev -> {
            var is = new IconsScene();
            is.show();

            is.iconItemSelected.addListener((_, _, icon) -> {
                if (icon != null) {
                    var ic = FontIcon.of(icon.getIconCode(), 16, Color.WHITE);
                    if (loadedIcon != null) root.getChildren().set(1, ic);
                    else root.getChildren().add(ic);

                    ic = FontIcon.of(icon.getIconCode(), 14, Color.WHITE);
                    nodeTarget.setGraphic(ic);
                }

            });
        });

        return root;
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

        btn.setOnAction(_ -> Commons.CentralizeComponent(selectedNode, canvaFather));

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
        } else if (cssField.equals("positioning-icon")) {
            if (selectedNode instanceof ButtonComponent component) {
                comboBox.setItems(FXCollections.observableArrayList("Left", "Right"));

                //se tem type de clip definido
                var positioning = component.getContentDisplay();
                if (positioning != null) comboBox.setValue(
                        positioning.equals(ContentDisplay.LEFT) ? "Left" : "Right"
                );

                comboBox.setOnAction(ev -> {
                    var value = comboBox.getValue();
                    if (value.equals("Left")) {
                        component.setContentDisplay(ContentDisplay.LEFT);
                    }
                    if (value.equals("Right")) {
                        component.setContentDisplay(ContentDisplay.RIGHT);
                    }
                });
            }
        }

        return root;
    }


    public static HBox ColorPickerRow(String title, Node selectedNode, String cssField) {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);

        // Botão de transparência
        Button transparentBtn = new Button(TranslationContext.instance().get().transparent());
        transparentBtn.getStyleClass().add("transparent-btn");

        // Layout (ColorPicker + Botão)
        HBox root = ItemRow(new HBox(10, colorPicker, transparentBtn), title);

        if (cssField.equals("icon-color")) {
            var btn = (ButtonComponent) selectedNode;
            var ic = (FontIcon) btn.getGraphic();
            if (ic != null) {
                colorPicker.setValue((Color) ic.getIconColor());

                colorPicker.setOnAction(e -> {
                    Color c = colorPicker.getValue();
                    ic.setIconColor(c);
                });
            }

        } else {
            String color = "transparent";

            if (selectedNode instanceof InputComponent node) {
                color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            } else if (selectedNode instanceof ButtonComponent node) {
                color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            } else if (selectedNode instanceof TextComponent node) {
                color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            }

            // Só aplica cor se NÃO for transparent
            if (!color.equalsIgnoreCase("transparent")) {
                try {
                    colorPicker.setValue(Color.web(color));
                } catch (Exception ignored) {
                }
            }

            // ▼ ColorPicker: aplica cor escolhida
            colorPicker.setOnAction(e -> {
                Color c = colorPicker.getValue();
                String newStyle = Commons.UpdateEspecificStyle(
                        selectedNode.getStyle(),
                        cssField,
                        Commons.ColortoHex(c)
                );

                selectedNode.setStyle(newStyle);
            });

            // ▼ Botão “Transparente”
            transparentBtn.setOnAction(e -> {
                String newStyle = Commons.UpdateEspecificStyle(
                        selectedNode.getStyle(),
                        cssField,
                        "transparent"
                );

                selectedNode.setStyle(newStyle);

                // visual feedback opcional (não obrigatório)
                transparentBtn.setStyle("-fx-opacity: 0.6;");
            });
        }


        return root;
    }

    private static HBox ItemRow(Node node, String text) {
        Label title = Typography.caption(text + ":");

        HBox root = new HBox(title, node);
        root.setSpacing(10);
        return root;
    }


}
