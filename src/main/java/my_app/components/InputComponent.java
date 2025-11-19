package my_app.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.shared.*;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import toolkit.Component;

public class InputComponent extends TextField implements ViewContract<InputComponentData> {
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();
    ComponentsContext componentsContext;
    TranslationContext.Translation translation = TranslationContext.instance().get();
    public StringProperty name = new SimpleStringProperty();

    boolean isDeleted = false;

    @Component
    CanvaComponent canvaFather;

    public InputComponent(String content, ComponentsContext componentsContext, CanvaComponent canva) {
        super(content);
        config();

        this.componentsContext = componentsContext;
        this.canvaFather = canva;
    }

    public InputComponent(ComponentsContext componentsContext, CanvaComponent canva) {
        config();

        this.componentsContext = componentsContext;
        this.canvaFather = canva;
    }

    void config() {
        setStyle("-fx-text-fill:black;-fx-font-weight:normal;-fx-text-box-border:black;-fx-font-size:%s;-fx-focus-color:%s;-fx-faint-focus-color:%s;-fx-prompt-text-fill:%s;"
                .formatted(
                        Commons.FontSizeDefault,
                        Commons.FocusColorDefault,
                        "transparent",
                        Commons.PlaceHolderColorDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    @Override
    public void applyData(InputComponentData data) {
        this.setId(data.identification());
        this.setText(data.text());

        this.setStyle("-fx-text-fill:%s;-fx-font-size:%s;-fx-font-weight:%s;-fx-prompt-text-fill:%s;-fx-focus-color:%s;-fx-text-box-border:%s;"
                .formatted(data.color(), data.font_size(), data.font_weight(),
                        data.placeholder_color(), data.focus_color(), data.no_focus_color()
                ));

        this.setLayoutX(data.x());
        this.setLayoutY(data.y());
        this.setPromptText(data.placeholder());
        this.name.set(data.name());
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
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LabelWithInput(translation.fontWeight(), this, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), this, "-fx-text-fill"),
                Components.LabelWithTextContent(translation.textContent(), getText(), this::setText),
                Components.LabelWithInput(translation.fontSize(), this, "-fx-font-size"),
                Components.LabelWithTextContent(translation.placeholder(), getPromptText(), this::setPromptText),
                Components.ColorPickerRow(translation.placeholderColor(), this, "-fx-prompt-text-fill"),
                Components.ColorPickerRow(translation.focusColor(), this, "-fx-focus-color"),
                Components.ColorPickerRow(translation.noFocusColor(), this, "-fx-text-box-border"),
                Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva)
        );
    }

    @Override
    public void otherSettings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent("Variable name", name.get(), v -> name.set(v)));
    }

    @Override
    public InputComponentData getData() {

        String style = getStyle();

        String text = this.getText();
        String placeholder = this.getPromptText();

        String fontWeight = Commons.getValueOfSpecificField(style, "-fx-font-weight");
        String fontSize = Commons.getValueOfSpecificField(style, "-fx-font-size");
        String color = Commons.getValueOfSpecificField(style, "-fx-text-fill");
        String focusColor = Commons.getValueOfSpecificField(style, "-fx-focus-color");
        String placeholderColor = Commons.getValueOfSpecificField(style, "-fx-prompt-text-fill");
        String noFocusColor = Commons.getValueOfSpecificField(style, "-fx-text-box-border");

        double x = this.getLayoutX();
        double y = this.getLayoutY();

        var location = Commons.NodeInCanva(this);

        return new InputComponentData(
                "input", text, placeholder, fontWeight, fontSize, color, x, y, this.getId(),
                location.inCanva(),
                location.fatherId(), focusColor, placeholderColor, noFocusColor,
                name.get(), isDeleted);
    }

}
