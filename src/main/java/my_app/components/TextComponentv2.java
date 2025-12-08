package my_app.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.TextComponentData;
import my_app.data.ViewContractv2;
import toolkit.Component;

public class TextComponentv2 extends Text implements ViewContractv2<TextComponentData> {
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();

    private final ComponentsContext componentsContext;

    TranslationContext.Translation translation = TranslationContext.instance().get();
    public StringProperty name = new SimpleStringProperty();

    @Component
    CanvaComponentV2 canvaFather;

    boolean isDeleted = false;

    public TextComponentv2(String content, ComponentsContext componentsContext, CanvaComponentV2 canvaComponent) {

        super(content);
        this.componentsContext = componentsContext;
        this.canvaFather = canvaComponent;

        setStyle("-fx-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    public TextComponentv2(ComponentsContext componentsContext, CanvaComponentV2 canvaComponent) {

        this.componentsContext = componentsContext;
        this.canvaFather = canvaComponent;

        setStyle("-fx-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithInput(translation.fontWeight(), this, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), this, "-fx-fill"),
                Components.LabelWithTextContent(translation.textContent(), getText(), this::setText),
                Components.LabelWithInput(translation.fontSize(), this, "-fx-font-size"),
                Components.LabelWithInput(translation.width(), this, "text-wrapping-width"),
                // Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow("Centralize horizontally", this, canva));
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent("Variable name", name.get(), v -> name.set(v)));
    }

    @Override
    public TextComponentData getData() {
        String style = getStyle();

        String text = this.getText();
        String fontWeight = Commons.getValueOfSpecificField(style, "-fx-font-weight");
        double x = this.getLayoutX();
        double y = this.getLayoutY();

        String fontSize = Commons.getValueOfSpecificField(style, "-fx-font-size");
        String textFill = Commons.getValueOfSpecificField(style, "-fx-fill");

        // usar aqui
        var location = Commons.NodeInCanva(this);

        return new TextComponentData(
                "text",
                text, x, y, fontSize, textFill, fontWeight, this.getId(),
                location.inCanva(),
                location.fatherId(), name.get(), this.getWrappingWidth(), isDeleted);
    }

    @Override
    public void applyData(TextComponentData data) {
        this.setText(data.text());
        this.setId(data.identification());

        this.setStyle("-fx-fill:%s;-fx-font-size:%s;-fx-font-weight:%s;"
                .formatted(data.color(), data.fontSize(), data.font_weight()));

        this.setLayoutX(data.layout_x());
        this.setLayoutY(data.layout_y());
        this.name.set(data.name());
        this.setWrappingWidth(data.wrapping_width());
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
}
