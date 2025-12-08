package my_app.data;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public interface ViewContractv2<T extends ComponentData> {
    void appearance(VBox father, CanvaComponentV2 canva);

    void settings(VBox father, CanvaComponentV2 canva);

    void otherSettings(VBox father, CanvaComponentV2 canva);

    T getData();

    void applyData(T data);

    Node getCurrentNode();

    boolean isDeleted();

    void delete();
}
