package my_app.screens.Home;

import javafx.geometry.Pos;
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
    public ScrollPane editor = new ScrollPane();

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

    public Home(Stage theirStage, boolean openComponentScene) {
        this.viewModel = new HomeViewModel();

        this.screensTabs.setAlignment(Pos.CENTER_LEFT);

        viewModel.init(this, theirStage);

        setTop(menuBar);
        setLeft(this.leftSide);

        //center
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

    //chamado no init()
    public void updateCanvaInEditor(CanvaComponentV2 newCanva) {
        this.currentCanva = newCanva; // Atualiza a referência
        if (this.leftSide == null) {
            this.leftSide = new LeftSide(newCanva, this.viewModel);
            this.leftSide.updateCanva(newCanva);
        } else {
            this.leftSide.updateCanva(newCanva); // Talvez o LeftSide precise ser atualizado também
        }

        this.editor.setContent(newCanva); // <<< ESSA É A LINHA QUE FALTAVA
    }
}
