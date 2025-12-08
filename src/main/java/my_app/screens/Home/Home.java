package my_app.screens.Home;

import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.Home.components.RightSidev2;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.screens.Home.components.leftside.LeftSide;
import toolkit.Component;

import static my_app.components.shared.UiComponents.MenuBarPrimary;

public class Home extends BorderPane {
    @Component
    MenuBar menuBar = MenuBarPrimary();

    @Component
    public LeftSide leftSide;

    @Component
    public VBox canvaHolder = new VBox(5);
    @Component
    public HBox screensTabs = new HBox(5);
    public CanvaComponentV2 currentCanva;

    @FunctionalInterface
    public interface VisualNodeCallback {
        void set(Node n);
    }

    HomeViewModel viewModel;

    public Home(Stage theirStage, ComponentsContext componentsContext, boolean openComponentScene) {
        this.viewModel = new HomeViewModel(componentsContext);

        viewModel.init(this, theirStage);

        //this.canva = new CanvaComponent(componentsContext, this.viewModel);
        this.leftSide = new LeftSide(currentCanva, this.viewModel);

        setTop(menuBar);
        setLeft(this.leftSide);

        //center
        ScrollPane editor = new ScrollPane();

        editor.setContent(currentCanva);
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
            currentCanva.setPrefSize(370, 250);
            var style = currentCanva.getStyle();
            var updated = Commons.UpdateEspecificStyle(style, "-fx-background-color", "transparent");

            editor.setStyle("""
                        -fx-background-color: transparent;
                        -fx-background: transparent;
                    """);

            currentCanva.setStyle(updated);
        }
        // scrollPane mostra o canva com barras se for maior que a janela

        // setCenter(this.canva);
        //setCenter(editor);


        canvaHolder.getChildren().addAll(screensTabs, editor);
        setCenter(canvaHolder);

        var rightSide = new RightSidev2(currentCanva, this.viewModel);
        StackPane rightWrapper = new StackPane(rightSide);
        rightWrapper.setMinWidth(Region.USE_PREF_SIZE);
        rightWrapper.setMaxWidth(Region.USE_PREF_SIZE);
        setRight(rightWrapper);

        //StackPane.setAlignment(rightSide, Pos.CENTER_RIGHT);


        //setRight(new RightSide(componentsContext, canva));

        getStyleClass().add("surface-color");


    }
}
