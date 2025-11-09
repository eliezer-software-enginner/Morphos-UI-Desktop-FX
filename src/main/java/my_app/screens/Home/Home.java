package my_app.screens.Home;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.Home.components.RightSide;
import my_app.screens.Home.components.leftside.LeftSide;
import toolkit.Component;
import toolkit.theme.MaterialTheme;

public class Home extends BorderPane {

    @Component
    public LeftSide leftSide;
    public CanvaComponent canva;

    @FunctionalInterface
    public interface VisualNodeCallback {
        void set(Node n);
    }

    public Home(ComponentsContext componentsContext, boolean openComponentScene) {
        this.leftSide = new LeftSide(this, componentsContext);
        setLeft(this.leftSide);

        canva = new CanvaComponent(componentsContext);

        ScrollPane editor = new ScrollPane();

        editor.setContent(canva);
        editor.setFitToWidth(false);
        editor.setFitToHeight(false);

        editor.setStyle("-fx-background-color:%s;-fx-background: %s"
                .formatted(MaterialTheme.getInstance().getSurfaceColorStyle(),
                        MaterialTheme.getInstance().getSurfaceColorStyle()));

        if (openComponentScene) {
            canva.setPrefSize(370, 250);
            var style = canva.getStyle();
            var updated = Commons.UpdateEspecificStyle(style, "-fx-background-color", "transparent");

            editor.setStyle("""
                        -fx-background-color: transparent;
                        -fx-background: transparent;
                    """);

            canva.setStyle(updated);
        }
        // scrollPane mostra o canva com barras se for maior que a janela

        // setCenter(this.canva);
        setCenter(editor);
        setRight(new RightSide(componentsContext));

        setStyle("-fx-background-color:%s;".formatted(MaterialTheme.getInstance().getSurfaceColorStyle()));
    }

    public void notifyError(String message) {
        leftSide.notifyError(message);
    }
}
