package my_app.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import my_app.data.Commons;
import my_app.data.InputComponentData;
import my_app.data.contracts.ViewComponent;

public final class InputComponent extends TextField implements ViewComponent<InputComponentData> {
    public StringProperty name = new SimpleStringProperty();

    boolean isDeleted = false;

    public InputComponent(String content) {
        super(content);
        config();
    }

    public InputComponent() {
        config();
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
    public Node getNode() {
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
                location.fatherId(), focusColor, placeholderColor, noFocusColor,
                name.get(), isDeleted);
    }

}
