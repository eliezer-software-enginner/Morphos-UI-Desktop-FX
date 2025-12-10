package my_app.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import my_app.components.buttonComponent.ButtonComponentv2;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import toolkit.Component;

import java.util.ArrayList;

public class CustomComponent extends Pane implements ViewContractv2<CustomComponentData> {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public ComponentsContext mainComponentsContext;
    @Component
    public CanvaComponentV2 canva;

    boolean isDeleted = false;
    private final HomeViewModel viewModel;

    public CustomComponent(HomeViewModel viewModel, CanvaComponentV2 canva) {
        this.viewModel = viewModel;
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

            if (node instanceof ButtonComponentv2 component) {
                btnComponentsData.add(component.getData());
            }

            if (node instanceof ImageComponentv2 component) {
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
                customComponentsData, isDeleted);
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

//        for (ButtonComponentData data_ : data.button_components) {
//            var node = new ButtonComponentv2(data_.text(), componentsContext);
//            node.applyData(data_);
//         //   node.setOnMouseClicked((e) -> componentsContext.selectNodePartially(node));
//            getChildren().add(node);
//        }

//        for (TextComponentData data_ : data.text_components) {
//            var node = new TextComponentv2(data_.text(), componentsContext, canva);
//            node.applyData(data_);
//            node.setOnMouseClicked((e) -> {
//                // ESSENCIAL: Consome o evento para evitar que o pai (CustomComponent) o veja.
//                e.consume();
//            //    componentsContext.selectNodePartially(node);
//            });
//            getChildren().add(node);
//        }
//
//        for (ImageComponentData data_ : data.image_components) {
//            var node = new ImageComponentv2(data_.url(), componentsContext);
//            node.applyData(data_);
//          //  node.setOnMouseClicked((e) -> componentsContext.selectNodePartially(node));
//            getChildren().add(node);
//        }
//


        //this.name.set(data.name());
        isDeleted = data.isDeleted();
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                //  Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                new ButtonRemoverComponent(this, this.viewModel));
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(Components.LayoutXYComponent(this));
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {

    }

}
