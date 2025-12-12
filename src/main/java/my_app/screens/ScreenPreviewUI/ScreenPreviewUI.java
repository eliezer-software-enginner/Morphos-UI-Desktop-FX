package my_app.screens.ScreenPreviewUI;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class ScreenPreviewUI extends StackPane {

    public ScreenPreviewUI(CanvaComponentV2 canvaComponentV2) {
        canvaComponentV2.disableAnimation();
        for (Node child : canvaComponentV2.getChildren()) {
            //remover todos os eventos de click
            if (child instanceof Button || child instanceof Text ||
                    child instanceof TextField || // Adicionado TextField
                    child instanceof ImageView) { // A
                child.setOnMouseClicked(Event::consume);
                child.setOnMouseDragged(ev -> ev.consume());
            }

        }
        getChildren().add(canvaComponentV2);
        getStylesheets().removeAll();
        canvaComponentV2.getStylesheets().removeAll();
    }

}
