package my_app.screens.ScreenPreviewUI;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class ScreenPreviewUI extends StackPane {

    public ScreenPreviewUI(CanvaComponentV2 canvaComponentV2) {
        canvaComponentV2.disableAnimation();
        for (Node child : canvaComponentV2.getChildren()) {
            //remover todos os eventos de click
        }
        getChildren().add(canvaComponentV2);
        getStylesheets().removeAll();
        canvaComponentV2.getStylesheets().removeAll();
    }

}
