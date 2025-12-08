package my_app.data;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import my_app.screens.Home.components.canvaComponent.CanvaComponent;

public interface ViewContract<T extends ComponentData> {
    void appearance(VBox father, CanvaComponent canva);

    void settings(VBox father, CanvaComponent canva);

    void otherSettings(VBox father, CanvaComponent canva);

    T getData();

    void applyData(T data);

    Node getCurrentNode();

    boolean isDeleted();

    void delete();
}
