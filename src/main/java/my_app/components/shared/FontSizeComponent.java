package my_app.components.shared;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.Typography;

public class FontSizeComponent extends HBox {

    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.fontSize() + ":");
    TextField tf = new TextField();

    public FontSizeComponent(ObjectProperty<Node> selectedNode) {

        Node node = selectedNode.get();

        config();

        loadNodeFontSizeInTextField(node);

        tf.textProperty().addListener((obs, old, newVal) -> {

            String existingStyle = node.getStyle();

            try {
                String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-font-size", newVal.trim());
                node.setStyle(newStyle);
            } catch (NumberFormatException err) {
            }

        });

        getChildren().addAll(title, tf);

    }

    private void loadNodeFontSizeInTextField(Node node) {

        String currentFontSize = Commons.getValueOfSpecificField(node.getStyle(), "-fx-font-size");

        if (currentFontSize.isEmpty()) {
            currentFontSize = Commons.ItemTextFontSizeDefault;
        }

        tf.setText(currentFontSize);

    }

    void config() {

        setSpacing(10);
    }
}
