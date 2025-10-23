package my_app.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.FontColorPicker;
import my_app.components.shared.FontSizeComponent;
import my_app.components.shared.FontWeightComponent;
import my_app.components.shared.TextContentComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.data.TextComponentData;
import my_app.data.ViewContract;

public class TextComponent extends Text implements ViewContract<TextComponentData> {
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();

    ComponentsContext componentsContext;

    public TextComponent(String content, ComponentsContext componentsContext) {

        super(content);

        this.componentsContext = componentsContext;

        setStyle("-fx-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    @Override
    public void appearance(Pane father) {
        father.getChildren().setAll(
                new FontWeightComponent(currentState),
                new FontColorPicker(currentState),
                new TextContentComponent(currentState),
                new FontSizeComponent(currentState),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father) {
        father.getChildren().setAll(
                new LayoutPositionComponent(currentState));
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
                location.fatherId());
    }

    @Override
    public void applyData(TextComponentData data) {

        this.setText(data.text());
        this.setId(data.identification());

        this.setStyle("-fx-fill:%s;-fx-font-size:%s;-fx-font-weight:%s;"
                .formatted(data.color(), data.fontSize(), data.font_weight()));

        this.setLayoutX(data.layout_x());
        this.setLayoutY(data.layout_y());

    }
}
