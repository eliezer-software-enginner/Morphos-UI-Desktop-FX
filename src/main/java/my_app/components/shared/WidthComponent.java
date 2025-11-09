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

public class WidthComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.width() + ":");
    TextField tf = new TextField();

    public WidthComponent(Node node) {
        config();

        if (node instanceof ImageComponent imgview) {
            tf.setText(String.valueOf(imgview.getFitWidth()));
        }

        if (node instanceof CanvaComponent c) {
            tf.setText(String.valueOf(c.getPrefWidth()));
        }

        tf.textProperty().addListener((_, _, newVal) -> {
            if (newVal.isBlank())
                return;

            try {
                double v = Double.parseDouble(newVal);
                if (node instanceof ImageComponent imgview) {
                    imgview.setFitWidth(v);
                } else if (node instanceof CanvaComponent c) {
                    c.setPrefWidth(v);
                }

            } catch (NumberFormatException err) {
                System.out.println(err.getMessage());
                if (node instanceof CanvaComponent) {
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
