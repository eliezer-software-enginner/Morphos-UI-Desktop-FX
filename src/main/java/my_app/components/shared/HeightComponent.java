package my_app.components.shared;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.Typography;

public class HeightComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.height() + ":");
    TextField tf = new TextField();

    public HeightComponent(Node node) {
        config();

        if (node instanceof ImageComponent imgview) {
            tf.setText(String.valueOf(imgview.getFitHeight()));
        }

        if (node instanceof CanvaComponent c) {
            tf.setText(String.valueOf(c.getPrefHeight()));
        }

        // dentro teria um NumericInputHandler
        tf.textProperty().addListener((_, _, newVal) -> {

            if (newVal.isBlank())
                return;

            try {
                double v = Double.parseDouble(newVal);

                if (node instanceof ImageComponent imgview)
                    imgview.setFitHeight(v);
                if (node instanceof CanvaComponent c)
                    c.setPrefHeight(v);
            } catch (NumberFormatException err) {
                if (node instanceof CanvaComponent) {
                    setPrefHeight(Commons.CanvaHeightDefault);
                }
            }

        });

        getChildren().addAll(title, tf);
    }

    void config() {
        setSpacing(10);
    }
}
