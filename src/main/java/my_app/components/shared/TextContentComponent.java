package my_app.components.shared;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.contexts.TranslationContext;
import my_app.themes.Typography;

public class TextContentComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.textContent() + ":");
    TextField tf = new TextField();

    public TextContentComponent(ObjectProperty<Node> selectedNode) {

        setSpacing(10);

        Node node = selectedNode.get();

        if (node instanceof ButtonComponent b) {
            this.tf.setText(b.getText());
        } else if (node instanceof TextField t) {
            this.tf.setText(t.getText());
        } else if (node instanceof TextComponent txt) {
            this.tf.setText(txt.getText());
        }

        tf.textProperty().addListener((obs, old, val) -> {
            Node n = selectedNode.get();

            if (n instanceof ButtonComponent b) {
                b.setText(val);
            } else if (n instanceof TextField t) {
                t.setText(val);
            } else if (n instanceof TextComponent txt) {
                txt.setText(val);
            }
        });

        getChildren().addAll(title, tf);

    }
}
