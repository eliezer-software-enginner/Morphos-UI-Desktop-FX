package my_app.scenes.MainScene;

import static my_app.components.shared.UiComponents.MenuBarPrimary;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.screens.Home.Home;
import my_app.screens.ShowCode.ShowCode;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import toolkit.Component;

public class MainScene extends Scene {

    ComponentsContext componentsContext = new ComponentsContext();

    MenuBar menuBar = MenuBarPrimary();
    Home home = new Home(componentsContext, false);
    Stage stage = new Stage();
    VBox mainView;

    MainSceneController controller = new MainSceneController(componentsContext);

    public MainScene() {
        super(new VBox(), 1200, 650);

        setup();
        styles();

        try {
            controller.loadSceneFromJsonFile(home, stage);
        } catch (RuntimeException _) {
        }

    }

    void setup() {
        menuBar.getMenus().setAll(createMenuOptions());
        mainView = new VBox(menuBar, home);

        HBox.setHgrow(home, Priority.ALWAYS);
        VBox.setVgrow(home, Priority.ALWAYS);

        setRoot(mainView);
        ThemeManager.Instance().addScene(this);
        Commons.UseDefaultStyles(this);
    }

    void styles() {
        mainView.getStyleClass().add("background-color");
    }

    @Component
    Menu createMenuOptions() {

        Menu menu = new Menu();
        Label menuText = Typography.caption("Option");
        menu.setGraphic(menuText);

        MenuItem itemSalvar = new MenuItem("Save");
        MenuItem itemSaveAs = new MenuItem("Save as");
        MenuItem itemLoad = new MenuItem("Load");
        MenuItem itemShowCode = new MenuItem("Show code");
        MenuItem itemContribute = new MenuItem("Become a contributor");
        menu.getItems().addAll(itemSalvar, itemSaveAs, itemLoad, itemShowCode, itemContribute);

        itemSalvar.setOnAction(_ -> controller.handleSave(home, stage));
        itemSaveAs.setOnAction(_ -> {

            try {
                controller.handleSaveAs(home, stage);
            } catch (RuntimeException e) {
                home.leftSide.notifyError(e.getMessage());
            }

        });

        itemLoad.setOnAction(_ -> controller.handleClickLoad(home, stage));

        itemShowCode.setOnAction(_ -> handleShowJavaCode(home.canva));

        itemContribute.setOnAction(_ -> controller.handleBecomeContributor());

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    private void handleShowJavaCode(CanvaComponent canvaComponent) {
        new ShowCode(canvaComponent)
                .abrir();

    }

}
