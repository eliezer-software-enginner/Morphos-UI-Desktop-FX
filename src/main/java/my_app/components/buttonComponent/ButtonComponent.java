package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import my_app.components.Components;
import my_app.components.LayoutPositionComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.FontColorPicker;
import my_app.components.shared.FontSizeComponent;
import my_app.components.shared.FontWeightComponent;
import my_app.components.shared.TextContentComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.ButtonComponentData;
import my_app.data.Commons;
import my_app.data.ViewContract;

public class ButtonComponent extends Button implements ViewContract<ButtonComponentData> {

    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();
    ComponentsContext componentsContext;

    public ButtonComponent(ComponentsContext componentsContext) {

        super();
        this.componentsContext = componentsContext;
        config();
    }

    public ButtonComponent(String content, ComponentsContext componentsContext) {
        super(content);
        this.componentsContext = componentsContext;
        config();
    }

    void config() {

        setId(String.valueOf(System.currentTimeMillis()));

        setStyle(
                "-fx-background-color:%s;-fx-padding:%s;-fx-font-weight:%s;-fx-background-radius:%s;-fx-border-radius:%s;-fx-text-fill:%s;-fx-font-size: %s;-fx-border-width: %s;"
                        .formatted(
                                Commons.ButtonBgColorDefault,
                                Commons.ButtonPaddingDefault,
                                Commons.ButtonFontWeightDefault,
                                Commons.ButtonRadiusDefault,
                                Commons.ButtonRadiusDefault,
                                Commons.ButtonTextColorDefault,
                                Commons.ButtonFontSizeDefault,
                                Commons.ButtonRadiusWidth
                                //
                        ));

        currentState.set(this); // 👈 sempre aponta para o próprio botão
        // Home.idOfComponentSelected.addListener((_a, _b, newId) -> {
        // System.out.println("newId: " + newId);

        // if (newId.equals(this.getId())) {
        // String novoEstilo = Commons.UpdateEspecificStyle(this.getStyle(),
        // "-fx-background-color", "red");
        // this.setStyle(novoEstilo);
        // }

        // });
    }

    @Override
    public void applyData(ButtonComponentData data) {
        var node = (Button) currentState.get();

        node.setId(data.identification());
        node.setText(data.text());

        String paddings = "%s %s %s %s"
                .formatted(data.padding_top(), data.padding_right(), data.padding_bottom(),
                        data.padding_left());

        this.setPadding(
                new Insets(data.padding_top(), data.padding_right(), data.padding_bottom(),
                        data.padding_left()));

        node.setStyle(
                "-fx-background-color:%s;-fx-padding:%s;-fx-font-weight:%s;-fx-background-radius:%s;-fx-border-radius:%s;-fx-text-fill:%s;-fx-font-size: %s;-fx-border-width: %s;"
                        .formatted(
                                data.bgColor(),
                                paddings,
                                data.fontWeight(),
                                data.borderRadius(),
                                data.borderRadius(),
                                data.color(),
                                data.fontSize(),
                                data.borderWidth()));

        node.setLayoutX(data.x());
        node.setLayoutY(data.y());

    }

    @Override
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                new ButtonBgColorPicker(currentState),
                new ButtonPaddingComponent(currentState),
                new ButtonBorderRadius(currentState),
                new ButtonBorderWidth(currentState),
                new ButtonBorderColorPicker(currentState),
                new FontWeightComponent(currentState),
                new FontColorPicker(currentState),
                new TextContentComponent(currentState),
                new FontSizeComponent(currentState),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                new LayoutPositionComponent(currentState),
                Components.ToogleSwithItemRow("Centralize horizontally", this, canva));

    }

    @Override
    public ButtonComponentData getData() {
        String style = this.getStyle();

        Insets padding = this.getPadding();

        String text = this.getText();
        // Extraindo informações sobre o estilo do botão
        String fontSize = Commons.getValueOfSpecificField(style, "-fx-font-size");
        String fontWeight = Commons.getValueOfSpecificField(style, "-fx-font-weight");
        String color = Commons.getValueOfSpecificField(style, "-fx-text-fill");
        String borderWidth = Commons.getValueOfSpecificField(style, "-fx-border-width");

        String bgColor = Commons.getValueOfSpecificField(style, "-fx-background-color");

        double x = this.getLayoutX();
        double y = this.getLayoutY();

        int paddingTop = (int) padding.getTop();
        int paddingRight = (int) padding.getRight();
        int paddingBottom = (int) padding.getBottom();
        int paddingLeft = (int) padding.getLeft();
        String borderRadius = Commons.getValueOfSpecificField(style, "-fx-border-radius");

        var location = Commons.NodeInCanva(this);

        return new ButtonComponentData(
                "button",
                text, fontSize, fontWeight, color, borderWidth, borderRadius, bgColor,
                x, y, paddingTop, paddingRight, paddingBottom, paddingLeft, this.getId(),
                location.inCanva(),
                location.fatherId());

    }

}
