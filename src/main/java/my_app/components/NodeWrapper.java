package my_app.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.data.ViewContract;

public class NodeWrapper {
    private ViewContract<Node> currentNode;

    public NodeWrapper(ViewContract<Node> node) {
        this.currentNode = node;
    }

    public void renderRightSideContainer(Pane father, BooleanProperty appearenceIsSelected, CanvaComponent canva) {
        if (appearenceIsSelected.get()) {
            this.currentNode.appearance(father, canva);
        } else {
            this.currentNode.settings(father, canva);
        }
    }

}
