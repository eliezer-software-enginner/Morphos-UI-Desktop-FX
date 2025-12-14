package my_app.data;

import javafx.scene.Node;

public interface ViewContract<T extends ComponentData> {
    T getData();

    void applyData(T data);

    Node getCurrentNode();

    boolean isDeleted();

    void delete();
}
