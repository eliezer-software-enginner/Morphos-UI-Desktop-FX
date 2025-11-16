package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import my_app.components.Components;
import my_app.components.LayoutPositionComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.ButtonComponentData;
import my_app.data.Commons;
import my_app.data.IconData;
import my_app.data.ViewContract;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.Ikonli;
import org.kordamp.ikonli.javafx.FontIcon;

public class ButtonComponent extends Button implements ViewContract<ButtonComponentData> {

    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();
    ComponentsContext componentsContext;
    TranslationContext.Translation translation = TranslationContext.instance().get();
    public StringProperty name = new SimpleStringProperty();

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
                "-fx-background-color:%s;-fx-padding:%s;-fx-font-weight:%s;-fx-background-radius:%s;-fx-border-radius:%s;-fx-border-color:%s;-fx-text-fill:%s;-fx-font-size:%s;-fx-border-width:%s;"
                        .formatted(
                                Commons.ButtonBgColorDefault,
                                Commons.ButtonPaddingDefault,
                                Commons.ButtonFontWeightDefault,
                                Commons.ButtonRadiusDefault,
                                Commons.ButtonRadiusDefault,
                                Commons.ColorTransparent,
                                Commons.ButtonTextColorDefault,
                                Commons.ButtonFontSizeDefault,
                                Commons.ButtonRadiusWidth
                        ));

        currentState.set(this);
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
                "-fx-background-color:%s;-fx-padding:%s;-fx-font-weight:%s;-fx-background-radius:%s;-fx-border-radius:%s;-fx-text-fill:%s;-fx-font-size:%s;-fx-border-width:%s;-fx-border-color:%s;"
                        .formatted(
                                data.bgColor(),
                                paddings,
                                data.fontWeight(),
                                data.borderRadius(),
                                data.borderRadius(),
                                data.color(),
                                data.fontSize(),
                                data.borderWidth(),
                                data.border_color()));

        node.setLayoutX(data.x());
        node.setLayoutY(data.y());
        this.name.set(data.name());
        final var ic = data.icon();


        //AntDesignIcons-Filled;ANDROID
        if (ic != null) {
//            var ikon = Ikonli.valueOf(ic.name());
//            this.setGraphic(FontIcon.of(ikon, ic.size(), Color.web(ic.color())));

            try {
                Class<?> clazz = Class.forName(ic.pack());
                Ikon ikon = (Ikon) Enum.valueOf((Class<Enum>) clazz, ic.name());
                this.setGraphic(FontIcon.of(ikon, ic.size(), Color.web(ic.color())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.ColorPickerRow(translation.fontColor(), this, "-fx-background-color"),
                Components.LabelWithInput(translation.padding(), this, "-fx-padding"),
                new ButtonBorderRadius(currentState),
                //new ButtonBorderWidth(currentState),
                Components.LabelWithInput(translation.borderWidth(), this, "-fx-border-width"),
                Components.ColorPickerRow(translation.borderColor(), this, "-fx-border-color"),
                Components.LabelWithInput(translation.fontWeight(), this, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), this, "-fx-text-fill"),
                Components.LabelWithTextContent(translation.textContent(), getText(), this::setText),
                Components.LabelWithInput(translation.fontSize(), this, "-fx-font-size"),
                Components.ButtonChooseGraphicContent(this),
                Components.spacerVertical(10),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                new LayoutPositionComponent(currentState),
                Components.ToogleSwithItemRow("Centralize horizontally", this, canva));
    }

    @Override
    public void otherSettings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent("Variable name", name.get(), v -> name.set(v)));
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
        String borderColor = Commons.getValueOfSpecificField(style, "-fx-border-color");

        var location = Commons.NodeInCanva(this);

        IconData iconData = null;
        if (getGraphic() != null) {
            if (getGraphic() instanceof FontIcon icon)
                iconData = new IconData(
                        icon.getIconCode().getClass().getName(),
                        icon.getIconCode().toString(), icon.getIconSize(), Commons.ColortoHex((Color) icon.getIconColor()));
        }

        return new ButtonComponentData(
                "button",
                text, fontSize, fontWeight, color, borderWidth, borderRadius, bgColor,
                x, y, paddingTop, paddingRight, paddingBottom, paddingLeft, this.getId(),
                location.inCanva(),
                location.fatherId(),
                borderColor,
                name.get(),
                iconData
        );

    }

}
