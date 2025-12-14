package my_app.data.contracts;

import javafx.scene.Node;
import my_app.components.*;
import my_app.components.imageComponent.ImageComponent;
import my_app.data.ComponentData;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public sealed interface ViewComponent<T extends ComponentData>
        permits ButtonComponent, ColumnComponent, CustomComponent, InputComponent, MenuComponent, TextComponent, ImageComponent, CanvaComponentV2 {
    Node getNode();

    T getData();

    void applyData(T data);

    boolean isDeleted();

    void delete();
}
