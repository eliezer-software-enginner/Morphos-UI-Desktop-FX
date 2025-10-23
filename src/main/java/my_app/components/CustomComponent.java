package my_app.components;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.inputComponents.InputComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.ButtonComponentData;
import my_app.data.ColumnComponentData;
import my_app.data.Commons;
import my_app.data.CustomComponentData;
import my_app.data.ImageComponentData;
import my_app.data.InputComponentData;
import my_app.data.TextComponentData;
import my_app.data.ViewContract;
import my_app.themes.Typography;

public class CustomComponent extends Pane implements ViewContract<CustomComponentData> {

    ComponentsContext componentsContext;
    public ComponentsContext mainComponentsContext;

    public CustomComponent(ComponentsContext componentsContext) {
        super();
        this.componentsContext = componentsContext;

        this.setId(System.currentTimeMillis() + "");
    }

    @Override
    public CustomComponentData getData() {
        String canvastyle = this.getStyle();

        Insets padding = this.getPadding();
        int paddingTop = (int) padding.getTop();
        int paddingRight = (int) padding.getRight();
        int paddingBottom = (int) padding.getBottom();
        int paddingLeft = (int) padding.getLeft();

        double width = this.getPrefWidth();
        double height = this.getPrefHeight();

        String bgType = "";
        String bgContent = "";
        if (Commons.getValueOfSpecificField(canvastyle,
                "-fx-background-image").isEmpty()) {
            bgContent = Commons.getValueOfSpecificField(canvastyle,
                    "-fx-background-color");
            bgType = "color";
        } else {
            var bgImage = Commons.getValueOfSpecificField(canvastyle,
                    "-fx-background-image");// url('" + url +
            // "');

            var right = bgImage.split("(")[1];
            var left = right.split(")")[0];

            bgContent = left;
            bgType = "image";
        }

        var textComponentsData = new ArrayList<TextComponentData>();
        var btnComponentsData = new ArrayList<ButtonComponentData>();
        var imgComponentsData = new ArrayList<ImageComponentData>();
        var inputComponentsData = new ArrayList<InputComponentData>();
        var columnComponentsData = new ArrayList<ColumnComponentData>();
        var customComponentsData = new ArrayList<CustomComponentData>();

        for (Node node : getChildren()) {

            if (node instanceof TextComponent component) {
                textComponentsData.add(component.getData());
            }

            if (node instanceof ButtonComponent component) {
                btnComponentsData.add(component.getData());
            }

            if (node instanceof ImageComponent component) {
                imgComponentsData.add(component.getData());
            }

            if (node instanceof InputComponent component) {
                inputComponentsData.add(component.getData());
            }

            if (node instanceof CustomComponent component) {
                customComponentsData.add(component.getData());
            }
        }

        var location = Commons.NodeInCanva(this);

        return new CustomComponentData(paddingTop, paddingRight, paddingBottom, paddingLeft, width, height, bgType,
                bgContent, this.getId(), (int) getLayoutX(), (int) getLayoutY(), location.inCanva(),
                location.fatherId(),
                textComponentsData,
                btnComponentsData,
                imgComponentsData,
                inputComponentsData,
                columnComponentsData,
                customComponentsData);
    }

    @Override
    public void applyData(CustomComponentData data) {

        this.setId(data.identification());

        this.setLayoutX(data.x);
        this.setLayoutY(data.y);

        // Aplicando as informações extraídas ao CanvaComponent
        this.setPrefWidth(data.width);
        this.setPrefHeight(data.height);

        // Ajustando o padding
        this.setPadding(
                new Insets(data.padding_top,
                        data.padding_right,
                        data.padding_bottom,
                        data.padding_left));

        var bgType = data.bg_type;
        var bgContent = data.bgContent;
        // Definindo o fundo com base no tipo
        if (bgType.equals("color")) {
            this.setStyle("-fx-background-color:%s;".formatted(
                    bgContent));
        } else if (bgType.equals("image")) {
            // Para imagem, você pode fazer algo como isso:
            this.setStyle("-fx-background-image: url('" + bgContent + "');" +
                    "-fx-background-size: cover; -fx-background-position: center;");
        }

        for (ButtonComponentData data_ : data.button_components) {
            var node = new ButtonComponent(data_.text(), componentsContext);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> componentsContext.selectNode(node));
            getChildren().add(node);
        }

        for (TextComponentData data_ : data.text_components) {
            var node = new TextComponent(data_.text(), componentsContext);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> componentsContext.selectNode(node));
            getChildren().add(node);
        }

        for (ImageComponentData data_ : data.image_components) {
            var node = new ImageComponent(data_.url(), componentsContext);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> componentsContext.selectNode(node));
            getChildren().add(node);
        }
    }

    @Override
    public void appearance(Pane father) {

        // 1. Obter o valor da cor.
        String currentColor = Commons.getValueOfSpecificField(getStyle(), "-fx-background-color");

        // 2. Definir a cor inicial. Se a string for vazia, usa um valor seguro
        // (#ffffff).
        Color initialColor = Color.web(
                currentColor.isEmpty() ? "#ffffff" : currentColor);

        // Color Picker
        ColorPicker bgColorPicker = new ColorPicker(initialColor);
        bgColorPicker.setOnAction(e -> {
            Color c = bgColorPicker.getValue();

            var updated = Commons.UpdateEspecificStyle(getStyle(), "-fx-background-color",
                    Commons.ColortoHex(c));

            // setStyle("-fx-background-color:%s;".formatted(
            // Commons.ColortoHex(c)));

        });

        // Botão para escolher imagem do sistema
        // Button chooseImgBtn = new Button("Choose Image...");
        // chooseImgBtn.setOnAction(e -> {
        // final var fc = new FileChooser();

        // fc.getExtensionFilters().addAll(
        // new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        // var file = fc.showOpenDialog(null);
        // if (file != null) {
        // setStyle("-fx-background-image: url('" + file.toURI().toString() + "'); " +
        // "-fx-background-size: cover; -fx-background-position: center;");
        // }
        // });

        // // Campo para URL
        // TextField urlField = new TextField();
        // urlField.setPromptText("Paste Image Url");
        // Button applyUrl = new Button("Apply URL");
        // applyUrl.setOnAction(e -> {
        // String url = urlField.getText();
        // if (url != null && !url.isBlank()) {
        // setStyle("-fx-background-image: url('" + url + "'); " +
        // "-fx-background-size: cover; -fx-background-position: center;");
        // }
        // });

        // father.getChildren().setAll(bgColorPicker,
        // chooseImgBtn, urlField,
        // applyUrl,
        // new my_app.components.shared.WidthComponent(this),
        // new HeightComponent(this));

        father.getChildren().setAll(new ButtonRemoverComponent(this, mainComponentsContext));
    }

    @Override
    public void settings(Pane father) {
        father.getChildren().setAll(Typography.body("Empty"));
    }

}
