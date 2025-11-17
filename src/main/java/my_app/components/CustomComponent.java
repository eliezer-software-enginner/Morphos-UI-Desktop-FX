package my_app.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import toolkit.Component;

import java.util.ArrayList;

public class CustomComponent extends Pane implements ViewContract<CustomComponentData> {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public ComponentsContext mainComponentsContext;
    @Component
    public CanvaComponent canva;

    public CustomComponent(ComponentsContext componentsContext, CanvaComponent canva) {
        super();
        this.componentsContext = componentsContext;
        this.canva = canva;

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
    public void applyData(ComponentData data) {
        var cast = (CustomComponentData) data;
        this.setId(data.identification());

        this.setLayoutX(cast.x);
        this.setLayoutY(cast.y);

        // Aplicando as informações extraídas ao CanvaComponent
        this.setPrefWidth(cast.width);
        this.setPrefHeight(cast.height);

        // Ajustando o padding
        this.setPadding(
                new Insets(cast.padding_top,
                        cast.padding_right,
                        cast.padding_bottom,
                        cast.padding_left));

        var bgType = cast.bg_type;
        var bgContent = cast.bgContent;
        // Definindo o fundo com base no tipo
        if (bgType.equals("color")) {
            this.setStyle("-fx-background-color:%s;".formatted(
                    bgContent));
        } else if (bgType.equals("image")) {
            // Para imagem, você pode fazer algo como isso:
            this.setStyle("-fx-background-image: url('" + bgContent + "');" +
                    "-fx-background-size: cover; -fx-background-position: center;");
        }

        for (ButtonComponentData data_ : cast.button_components) {
            var node = new ButtonComponent(data_.text(), componentsContext);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> componentsContext.selectNodePartially(node));
            getChildren().add(node);
        }

        for (TextComponentData data_ : cast.text_components) {
            var node = new TextComponent(data_.text(), componentsContext, canva);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> {
                // ESSENCIAL: Consome o evento para evitar que o pai (CustomComponent) o veja.
                e.consume();
                componentsContext.selectNodePartially(node);
            });
            getChildren().add(node);
        }

        for (ImageComponentData data_ : cast.image_components) {
            var node = new ImageComponent(data_.url(), componentsContext);
            node.applyData(data_);
            node.setOnMouseClicked((e) -> componentsContext.selectNodePartially(node));
            getChildren().add(node);
        }

        //this.name.set(data.name());
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

    @Override
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(Components.LayoutXYComponent(this));
    }

    @Override
    public void otherSettings(Pane father, CanvaComponent canva) {

    }

}
