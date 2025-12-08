package my_app.components;

import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.VBox;
import my_app.data.ComponentData;
import my_app.data.ViewContractv2;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class NodeWrapperv2 {
    private final ViewContractv2<ComponentData> currentNodeWrapper;

    public NodeWrapperv2(ViewContractv2<ComponentData> node) {
        this.currentNodeWrapper = node;
    }

    public void renderRightSideContainer(VBox father, IntegerProperty optionSelected, CanvaComponentV2 canva) {
        father.setSpacing(5);

        if (optionSelected.get() == 1) {
            this.currentNodeWrapper.appearance(father, canva);
        } else if (optionSelected.get() == 2) {
            this.currentNodeWrapper.settings(father, canva);
        } else {
            this.currentNodeWrapper.otherSettings(father, canva);
        }
    }

}
