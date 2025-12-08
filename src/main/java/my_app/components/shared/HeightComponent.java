package my_app.components.shared;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;

public class HeightComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.height() + ":");
    TextField tf = new TextField();

    public HeightComponent(Node node) {
        config();

        if (node instanceof ImageComponentv2 imgview) {
            tf.setText(String.valueOf(imgview.getFitHeight()));
        }

        if (node instanceof CanvaComponentV2 c) {
            tf.setText(String.valueOf(c.getPrefHeight()));
        }

        // dentro teria um NumericInputHandler
        tf.textProperty().addListener((_, _, newVal) -> {

            if (newVal.isBlank())
                return;

            try {
                double v = Double.parseDouble(newVal);

                if (node instanceof ImageComponentv2 imgview)
                    imgview.setFitHeight(v);
                if (node instanceof CanvaComponentV2 c)
                    c.setPrefHeight(v);
            } catch (NumberFormatException err) {
                if (node instanceof CanvaComponentV2) {
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
