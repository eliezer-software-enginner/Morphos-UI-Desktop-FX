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

public class WidthComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.width() + ":");
    TextField tf = new TextField();

    public WidthComponent(Node node) {
        config();

        if (node instanceof ImageComponentv2 imgview) {
            tf.setText(String.valueOf(imgview.getFitWidth()));
        }

        if (node instanceof CanvaComponentV2 c) {
            tf.setText(String.valueOf(c.getPrefWidth()));
        }

        tf.textProperty().addListener((_, _, newVal) -> {
            if (newVal.isBlank())
                return;

            try {
                double v = Double.parseDouble(newVal);
                if (node instanceof ImageComponentv2 imgview) {
                    imgview.setFitWidth(v);
                } else if (node instanceof CanvaComponentV2 c) {
                    c.setPrefWidth(v);
                }

            } catch (NumberFormatException err) {
                System.out.println(err.getMessage());
                if (node instanceof CanvaComponentV2) {
                    setPrefWidth(Commons.CanvaWidthDefault);
                }
            }

        });

        getChildren().addAll(title, tf);
    }

    void config() {
        setSpacing(10);
    }
}
