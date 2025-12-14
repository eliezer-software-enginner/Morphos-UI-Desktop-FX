package my_app.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.text.Text;
import my_app.data.Commons;
import my_app.data.TextComponentData;
import my_app.data.contracts.ViewComponent;

public final class TextComponent extends Text implements ViewComponent<TextComponentData> {
    public StringProperty name = new SimpleStringProperty();
    boolean isDeleted = false;

    public TextComponent(String content) {

        super(content);

        setStyle("-fx-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
    }

    public TextComponent() {
        setStyle("-fx-fill:black;-fx-font-size:%s;-fx-font-weight:normal;"
                .formatted(
                        Commons.FontSizeDefault
                        //
                ));

        setId(String.valueOf(System.currentTimeMillis()));
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
}
