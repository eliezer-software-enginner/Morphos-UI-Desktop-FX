package my_app.components;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.converter.NumberStringConverter;
import my_app.FileManager;
import my_app.components.buttonComponent.ButtonComponentv2;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.scenes.IconsScene.IconsScene;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

import java.util.function.Consumer;

public class Components {

    @Component
    public static Button ButtonPrimaryOutline(String text) {
        var btn = new Button(text);
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        return btn;
    }

    @Component
    public static Region spacerVertical(int insets) {
        var region = new Region();
        region.setMinHeight(insets);
        region.setMaxHeight(insets);
        region.setPrefHeight(insets);
        return region;
    }

    @Component
    public static Button ButtonPrimary() {
        var btn = new Button();
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        return btn;
    }

    @Component
    public static Button ButtonPrimary(String text) {
        var btn = new Button(text);
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        return btn;
    }

    @Component
    public static Button ButtonPrimary(String text, Runnable onClick) {
        var btn = new Button(text);
        btn.getStyleClass().addAll("button-primary", "body-typo", "text-primary-color");

        btn.setOnMouseClicked(_ -> onClick.run());

        return btn;
    }


    @Component
    public static Button buttonRemove(String text) {
        var btn = new Button(text);
        btn.getStylesheets().add("btn-remove");
        return btn;
    }

    @Component
    private static final TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    public static Node ButtonChooseGraphicContent(ButtonComponentv2 nodeTarget) {
        var btn = ButtonPrimary(translation.chooseIcon());
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

            is.iconItemSelected().addListener((_, _, icon) -> {
                if (icon != null) {
                    var ic = FontIcon.of(icon.getIconCode(), 16, Color.WHITE);

                    if (root.getChildren().size() == 2) {
                        root.getChildren().set(1, ic);
                    } else {
                        root.getChildren().add(ic);
                    }

                    ic = FontIcon.of(icon.getIconCode(), 14, Color.WHITE);
                    nodeTarget.setGraphic(ic);
                }
            });
        });

        return root;
    }

    @Component
    public static Node LayoutXYComponent(Node node) {
        TextField tfX = new TextField();
        var xSide = ItemRow(tfX, "X");

        TextField tfY = new TextField();
        var ySide = ItemRow(tfY, "Y");

        var root = new HBox(xSide, ySide);

        // vincula TextField <-> layoutX
        Bindings.bindBidirectional(
                tfX.textProperty(),
                node.layoutXProperty(),
                new NumberStringConverter());

        Bindings.bindBidirectional(
                tfY.textProperty(),
                node.layoutYProperty(),
                new NumberStringConverter());

        return root;
    }

    @Component
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

    @Component
    public static Node LabelWithInputAndButton(
            String name, String btnTitle,
            Node node, String fieldCss, Runnable doAction) {

        TextField tf = new TextField();
        final var btn = ButtonPrimary(btnTitle);

        HBox root = ItemRow(new HBox(tf, btn), name);

        if (fieldCss.equals("screen-name")) {
            if (node instanceof CanvaComponentV2 component) {
                tf.setText(component.name);
            }
        }

        tf.textProperty().addListener((_, _, newVal) -> {
            if (!newVal.isBlank()) {
                try {
                    if (fieldCss.equals("screen-name")) {
                        if (node instanceof CanvaComponentV2 component) {
                            component.name = newVal.trim();
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        btn.setOnMouseClicked(ev -> doAction.run());

        return root;
    }

    //se o campo recebido não for um css então é java, mexeremos no node!
    @Component
    public static Node LabelWithInput(String name, Node node, String fieldCss) {
        TextField tf = new TextField();

        HBox root = ItemRow(tf, name);

        if (fieldCss.equals("text-wrapping-width")) {
            if (node instanceof TextComponentv2 component) {
                tf.setText(String.valueOf(component.getWrappingWidth()));
            }
        } else if (fieldCss.equals("screen-name")) {
            if (node instanceof CanvaComponentV2 component) {
                tf.setText(component.name);
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
                        if (node instanceof TextComponentv2 component) {
                            //validate if is number
                            component.setWrappingWidth(Double.parseDouble(newVal.trim()));
                        }
                    } else if (fieldCss.equals("screen-name")) {
                        if (node instanceof CanvaComponentV2 component) {
                            //validate if is number
                            component.name = newVal.trim();
                            //FileManager.updateScreenNameInProject(component.screenFatherId, newVal.trim());
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


    @Component
    public static HBox ToogleSwithItemRow(String title, Node selectedNode, Pane canvaFather) {
        Button btn = new Button("Centralize");

        HBox root = ItemRow(btn, title);

        btn.setOnAction(_ -> Commons.CentralizeComponent(selectedNode, canvaFather));

        return root;
    }

    @Component
    public static HBox LabelWithComboBox(String title, Node selectedNode, String cssField) {
        ComboBox<String> comboBox = new ComboBox<>();

        HBox root = ItemRow(comboBox, title);

        switch (cssField) {
            case "clip-image-as-circle" -> {
                if (selectedNode instanceof ImageComponentv2 component) {
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
            case "positioning-icon" -> {
                if (selectedNode instanceof ButtonComponentv2 component) {
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
            case "data-list" -> {
                if (selectedNode instanceof ColumnComponent component) {
                    FileManager.getVariableNamesInDataTable().forEach(IO::println);
                    comboBox.setItems(FXCollections.observableArrayList(FileManager.getVariableNamesInDataTable()));

                    //se já possui o data list aplicado
                    if (component.dataTableVariableName != null) comboBox.setValue(component.dataTableVariableName);

                    comboBox.setOnAction(_ -> {

                        component.setDataTableVariableName(comboBox.getValue());
                        comboBox.setValue(comboBox.getValue());
                    });
                }
            }
        }

        return root;
    }


    @Component
    public static HBox ColorPickerRow(String title, Node selectedNode, String cssField) {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);

        // Botão de transparência
        Button transparentBtn = new Button(TranslationContext.instance().get().transparent());
        transparentBtn.getStyleClass().add("transparent-btn");

        // Layout (ColorPicker + Botão)
        HBox root = ItemRow(new HBox(10, colorPicker, transparentBtn), title);

        //quando button foi montado vemos se já tem icone
        if (cssField.equals("icon-color")) {
            var btn = (ButtonComponentv2) selectedNode;
            var loadedIcon = (FontIcon) btn.getGraphic();
            if (loadedIcon != null) {
                colorPicker.setValue((Color) loadedIcon.getIconColor());

                //se tiver icone aqui ja funciona
                colorPicker.setOnAction(e -> {
                    IO.println("aqui");
                    Color c = colorPicker.getValue();
                    IO.println("color took: " + c);

                    IO.println("current icon: " + loadedIcon);

                    var currentIc = (FontIcon) btn.getGraphic();
                    currentIc.setIconColor(c);
                });
            }

            btn.graphicProperty().addListener((_, _, newIcon) -> {
                if (newIcon != null) {
                    var currentIc = (FontIcon) newIcon;
                    currentIc.setIconColor(colorPicker.getValue());

                    colorPicker.setOnAction(e -> {
                        IO.println("aqui");
                        Color c = colorPicker.getValue();
                        IO.println("color took: " + c);

                        IO.println("current icon: " + currentIc);
                        currentIc.setIconColor(c);
                    });
                }
            });


        } else {
            String color = "transparent";

            if (selectedNode instanceof InputComponentv2 node) {
                color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            } else if (selectedNode instanceof ButtonComponentv2 node) {
                color = Commons.getValueOfSpecificField(node.getStyle(), cssField);
            } else if (selectedNode instanceof TextComponentv2 node) {
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

    @Component
    private static HBox ItemRow(Node node, String text) {
        Label title = Typography.caption(text + ":");

        HBox root = new HBox(title, node);
        root.setSpacing(10);
        return root;
    }


}
