package my_app.screens.Home;

import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.components.Components;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.StateJson_v2;
import my_app.screens.Home.components.RightSide;
import my_app.screens.Home.components.leftside.LeftSide;
import my_app.windows.AllWindows;
import toolkit.Component;

import static my_app.components.shared.UiComponents.MenuBarPrimary;

public class Home extends BorderPane {
    @Component
    MenuBar menuBar = MenuBarPrimary();

    @Component
    public LeftSide leftSide;
    public CanvaComponent canva;

    @FunctionalInterface
    public interface VisualNodeCallback {
        void set(Node n);
    }

    HomeViewModel viewModel;

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public Home(Stage theirStage, ComponentsContext componentsContext, boolean openComponentScene) {
        this.viewModel = new HomeViewModel(componentsContext);

        this.canva = new CanvaComponent(componentsContext, this.viewModel);
        this.leftSide = new LeftSide(canva, componentsContext);


        setTop(menuBar);

        setLeft(this.leftSide);

        //center

        final var vbox = new VBox();

        final var hboxScreensBox = new HBox(5);

        viewModel.refreshScreensTabs.addListener((_, _, _) -> {
            final var updatedProjectData = FileManager.getProjectData();

            hboxScreensBox.getChildren().clear();
            for (StateJson_v2 screen : updatedProjectData.screens()) {
                MenuButton menu = new MenuButton(screen.name);
                MenuItem itemShowCode = new MenuItem(translation.optionsMenuMainScene().showCode());
                itemShowCode.setOnAction(ev -> {
                    AllWindows.showWindowForShowCode(componentsContext, canva);
                });

                menu.getItems().add(itemShowCode);

                hboxScreensBox.getChildren().add(menu);
            }

            hboxScreensBox.getChildren().add(Components.ButtonPrimary("+"));
        });


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
        //setCenter(editor);


        vbox.getChildren().addAll(hboxScreensBox, editor);
        setCenter(vbox);

        var rightSide = new RightSide(componentsContext, canva);
        StackPane rightWrapper = new StackPane(rightSide);
        rightWrapper.setMinWidth(Region.USE_PREF_SIZE);
        rightWrapper.setMaxWidth(Region.USE_PREF_SIZE);
        setRight(rightWrapper);

        //StackPane.setAlignment(rightSide, Pos.CENTER_RIGHT);


        //setRight(new RightSide(componentsContext, canva));

        getStyleClass().add("surface-color");

        viewModel.init(this, theirStage);
    }
}
