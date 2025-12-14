package my_app.data.contracts;

import javafx.scene.layout.VBox;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public interface Inspector<T extends ViewComponent<?>> {
    void appearance(VBox container, CanvaComponentV2 canva);

    void layout(VBox container, CanvaComponentV2 canva);

    void settings(VBox container, CanvaComponentV2 canva);
}
