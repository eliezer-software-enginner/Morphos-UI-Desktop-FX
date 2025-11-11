package my_app.components.inputComponents;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import my_app.components.Components;
import my_app.components.LayoutPositionComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.FontColorPicker;
import my_app.components.shared.FontSizeComponent;
import my_app.components.shared.FontWeightComponent;
import my_app.components.shared.TextContentComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.data.InputComponentData;
import my_app.data.ViewContract;

public class InputComponent extends TextField implements ViewContract<InputComponentData> {
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();
    ComponentsContext componentsContext;

    public InputComponent(String content, ComponentsContext componentsContext) {
        super(content);
        config();

        this.componentsContext = componentsContext;
    }

    public InputComponent(ComponentsContext componentsContext) {
        config();

        this.componentsContext = componentsContext;
    }

    void config() {
        setStyle("-fx-text-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    @Override
    public void applyData(InputComponentData data) {

        this.setId(data.identification());
        this.setText(data.text());

        this.setStyle("-fx-text-fill:%s;-fx-font-size:%s;-fx-font-weight:%s;"
                .formatted(data.color(), data.font_size(), data.font_weight()));

        this.setLayoutX(data.x());
        this.setLayoutY(data.y());
        this.setPromptText(data.placeholder());
    }

    @Override
    public void appearance(Pane father) {
        father.getChildren().setAll(
                new FontWeightComponent(currentState),
                new FontColorPicker(currentState),
                new TextContentComponent(currentState),
                new FontSizeComponent(currentState),
                new PromptTextComponent(currentState),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father) {
        father.getChildren().setAll(
                new LayoutPositionComponent(currentState));
    }

    @Override
    public InputComponentData getData() {

        String style = getStyle();

        String text = this.getText();
        String placeholder = this.getPromptText();

        String fontWeight = Commons.getValueOfSpecificField(style, "-fx-font-weight");

        String fontSize = Commons.getValueOfSpecificField(style, "-fx-font-size");
        String color = Commons.getValueOfSpecificField(style, "-fx-text-fill");

        double x = this.getLayoutX();
        double y = this.getLayoutY();

        var location = Commons.NodeInCanva(this);

        return new InputComponentData(
                "input", text, placeholder, fontWeight, fontSize, color, x, y, this.getId(),
                location.inCanva(),
                location.fatherId());
    }

}
