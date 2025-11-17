package my_app.data;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import my_app.components.canvaComponent.CanvaComponent;

public interface ViewContract<T> {
    void appearance(Pane father, CanvaComponent canva);

    void settings(Pane father, CanvaComponent canva);

    void otherSettings(Pane father, CanvaComponent canva);

    T getData();

    void applyData(ComponentData data);

    Node getCurrentNode();
}
