package my_app.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.data.ViewContract;

public class NodeWrapper {
    private final ViewContract<Node> currentNode;

    public NodeWrapper(ViewContract<Node> node) {
        this.currentNode = node;
    }

    public void renderRightSideContainer(Pane father, IntegerProperty optionSelected, CanvaComponent canva) {
        if (optionSelected.get() == 1) {
            this.currentNode.appearance(father, canva);
        } else if (optionSelected.get() == 2) {
            this.currentNode.settings(father, canva);
        } else {
            this.currentNode.otherSettings(father, canva);
        }
    }

}
