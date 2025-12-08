package my_app.components;

import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.VBox;
import my_app.screens.Home.components.canvaComponent.CanvaComponent;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.data.ComponentData;
import my_app.data.ViewContract;

public class NodeWrapper {
    private final ViewContract<ComponentData> currentNodeWrapper;

    public NodeWrapper(ViewContract<ComponentData> node) {
        this.currentNodeWrapper = node;
    }

    public void renderRightSideContainer(VBox father, IntegerProperty optionSelected, CanvaComponent canva) {
        father.setSpacing(5);

        if (optionSelected.get() == 1) {
            this.currentNodeWrapper.appearance(father, canva);
        } else if (optionSelected.get() == 2) {
            this.currentNodeWrapper.settings(father, canva);
        } else {
            this.currentNodeWrapper.otherSettings(father, canva);
        }
    }

    public void renderRightSideContainer(VBox father, IntegerProperty optionSelected, CanvaComponentV2 canva) {
        father.setSpacing(5);

        if (optionSelected.get() == 1) {
            // this.currentNodeWrapper.appearance(father, canva);
        } else if (optionSelected.get() == 2) {
            //  this.currentNodeWrapper.settings(father, canva);
        } else {
            //   this.currentNodeWrapper.otherSettings(father, canva);
        }
    }

}
