package my_app.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import my_app.data.ButtonComponentData;
import my_app.data.Commons;
import my_app.data.IconData;
import my_app.data.contracts.ViewComponent;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

final public class ButtonComponent extends Button implements ViewComponent<ButtonComponentData> {

    public StringProperty name = new SimpleStringProperty();
    public String nameOfOnClickMethod;
    boolean isDeleted = false;

    public ButtonComponent() {
        super();
        config();
    }

    public ButtonComponent(String content) {
        super(content);
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

    }

    @Override
    public void applyData(ButtonComponentData data) {

        setId(data.identification());
        setText(data.text());
        this.nameOfOnClickMethod = data.nameOfOnClickMethod();

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
    public Node getNode() {
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
                nameOfOnClickMethod,
                location.fatherId(),
                borderColor,
                name.get(),
                iconData,
                isDeleted
        );

    }

}
