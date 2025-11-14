package my_app.screens.Home;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.Home.components.RightSide;
import my_app.screens.Home.components.leftside.LeftSide;
import toolkit.Component;

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

        //background-color is border
//        editor.setStyle("-fx-background-color:%s;-fx-background: %s"
//                .formatted("red",
//                        "yellow"));

//        editor.setStyle("-fx-background-color:%s;-fx-background: %s"
//                .formatted(MaterialTheme.getInstance().getSurfaceColorStyle(),
//                        MaterialTheme.getInstance().getSurfaceColorStyle()));

        editor.getStyleClass().setAll("surface-color");
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

        var rightSide = new RightSide(componentsContext, canva);
        StackPane rightWrapper = new StackPane(rightSide);
        rightWrapper.setMinWidth(Region.USE_PREF_SIZE);
        rightWrapper.setMaxWidth(Region.USE_PREF_SIZE);
        setRight(rightWrapper);

        //StackPane.setAlignment(rightSide, Pos.CENTER_RIGHT);


        //setRight(new RightSide(componentsContext, canva));

        getStyleClass().add("surface-color");
    }
}
