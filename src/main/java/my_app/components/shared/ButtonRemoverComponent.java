package my_app.components.shared;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import toolkit.Component;

public class ButtonRemoverComponent extends HBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    @Component
    Button btn = new Button(translation.removeComponent());

    public ButtonRemoverComponent(Node node, ComponentsContext componentsContext) {
        System.out.println("node: " + node);

        config();

        getChildren().add(btn);

        btn.setOnAction(_ -> componentsContext.removeNode(node.getId()));
    }

    void config() {
        btn.setStyle("-fx-text-fill:white;");
        btn.getStyleClass().addAll("button-remove", "caption-typo");
    }
}
