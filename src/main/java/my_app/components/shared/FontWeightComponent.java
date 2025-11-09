package my_app.components.shared;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.Typography;

public class FontWeightComponent extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    Label title = Typography.caption(translation.fontWeight() + ":");

    public FontWeightComponent(ObjectProperty<Node> selectedNode) {

        var combo = new ComboBox<String>();
        Node node = selectedNode.get();

        config();

        // na renderização atualiza combobox

        // Carregar valores possíveis no ComboBox
        combo.getItems().addAll(fontWeightList);

        // 🔹 Inicializa o ComboBox com base no no selecionado

        String currentFontWeight = Commons.getValueOfSpecificField(node.getStyle(), "-fx-font-weight");

        if (currentFontWeight.isEmpty()) {
            currentFontWeight = fontWeightList.get(0);
        }

        combo.setValue(currentFontWeight);

        // clica no combo atualiza textComponent

        // Listener: String -> FontWeight
        combo.valueProperty().addListener((obs, old, newVal) -> {

            String existingStyle = node.getStyle();

            String newStyle = Commons.UpdateEspecificStyle(existingStyle, "-fx-font-weight", newVal);

            node.setStyle(newStyle);

        });

        getChildren().addAll(title, combo);

    }

    List<String> fontWeightList = List.of(
            "normal",
            "bold",
            "thin",
            "light",
            "medium",
            "black");

    void config() {

        setSpacing(10);
    }
}
