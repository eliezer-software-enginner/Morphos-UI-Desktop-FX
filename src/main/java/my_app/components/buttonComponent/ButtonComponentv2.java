package my_app.components.buttonComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import my_app.components.Components;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.TranslationContext;
import my_app.data.ButtonComponentData;
import my_app.data.Commons;
import my_app.data.IconData;
import my_app.data.ViewContractv2;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

public class ButtonComponentv2 extends Button implements ViewContractv2<ButtonComponentData> {

    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();
    TranslationContext.Translation translation = TranslationContext.instance().get();
    public StringProperty name = new SimpleStringProperty();

    boolean isDeleted = false;

    @Component
    CanvaComponentV2 currentCanva;
    private final HomeViewModel viewModel;

    public ButtonComponentv2(HomeViewModel viewModel, CanvaComponentV2 currentCanva) {
        super();
        this.viewModel = viewModel;
        this.currentCanva = currentCanva;
        config();
    }

    public ButtonComponentv2(String content, HomeViewModel viewModel) {
        super(content);
        this.viewModel = viewModel;
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

        setId(data.identification());
        setText(data.text());

        String paddings = "%s %s %s %s"
                .formatted(data.padding_top(), data.padding_right(), data.padding_bottom(),
                        data.padding_left());

        this.setPadding(
                new Insets(data.padding_top(), data.padding_right(), data.padding_bottom(),
                        data.padding_left()));


        setStyle(
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

        this.setLayoutX(data.x());
        this.setLayoutY(data.y());
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
                this.setContentDisplay(ic.position().equals("left") ? ContentDisplay.LEFT : ContentDisplay.RIGHT);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }

        isDeleted = data.isDeleted();
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.ColorPickerRow(translation.backgroundColor(), this, "-fx-background-color"),
                Components.LabelWithInput(translation.padding(), this, "-fx-padding"),
                new ButtonBorderRadius(currentState),
                //new ButtonBorderWidth(currentState),
                Components.LabelWithInput(translation.borderWidth(), this, "-fx-border-width"),
                Components.ColorPickerRow(translation.borderColor(), this, "-fx-border-color"),
                Components.LabelWithInput(translation.fontWeight(), this, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), this, "-fx-text-fill"),
                Components.LabelWithInput(translation.textContent(), this, "text-content"),
                Components.LabelWithInput(translation.fontSize(), this, "-fx-font-size"),
                //Components.ButtonChooseGraphicContent(this),
                Components.LabelWithComboBox(translation.iconPosition(), this, "positioning-icon"),
                Components.ColorPickerRow(translation.iconColor(), this, "icon-color"),
                //Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(10),
                new ButtonRemoverComponent(this, this.viewModel));
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva));
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {
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
        var iconPosition = getContentDisplay();
        if (getGraphic() != null) {
            if (getGraphic() instanceof FontIcon icon)
                iconData = new IconData(
                        icon.getIconCode().getClass().getName(),
                        icon.getIconCode().toString(), icon.getIconSize(),
                        Commons.ColortoHex((Color) icon.getIconColor()),
                        iconPosition.equals(ContentDisplay.LEFT) ? "left" : "right");
        }

        return new ButtonComponentData(
                "button",
                text, fontSize, fontWeight, color, borderWidth, borderRadius, bgColor,
                x, y, paddingTop, paddingRight, paddingBottom, paddingLeft, this.getId(),
                location.inCanva(),
                location.fatherId(),
                borderColor,
                name.get(),
                iconData,
                isDeleted
        );

    }

}
