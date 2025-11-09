package my_app.scenes.MainScene;

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
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.screens.Home.Home;
import my_app.screens.ShowCode.ShowCode;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.Locale;

import static my_app.components.shared.UiComponents.MenuBarPrimary;

public class MainScene extends Scene {

    ComponentsContext componentsContext = new ComponentsContext();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    MenuBar menuBar = MenuBarPrimary();

    @Component
    Home home = new Home(componentsContext, false);
    Stage stage = new Stage();

    @Component
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
        menuBar.getMenus().setAll(createMenuOptions(), createMenuSettings());
        mainView = new VBox(menuBar, home);

        HBox.setHgrow(home, Priority.ALWAYS);
        VBox.setVgrow(home, Priority.ALWAYS);

        setRoot(mainView);
        ThemeManager.Instance().addScene(this);
    }

    void styles() {
        mainView.getStyleClass().add("background-color");
        Commons.UseDefaultStyles(this);
    }

    @Component
    Menu createMenuOptions() {

        Menu menu = new Menu();
        Label menuText = Typography.caption(translation.common().option());
        menu.setGraphic(menuText);

        MenuItem itemSalvar = new MenuItem(translation.common().save());
        MenuItem itemSaveAs = new MenuItem(translation.common().saveAs());
        MenuItem itemLoad = new MenuItem(translation.common().load());
        MenuItem itemShowCode = new MenuItem(translation.optionsMenuMainScene().showCode());
        MenuItem itemContribute = new MenuItem(translation.optionsMenuMainScene().becomeContributor());
        MenuItem translate_test = new MenuItem("Translate - test");
        menu.getItems().addAll(itemSalvar, itemSaveAs, itemLoad, itemShowCode, itemContribute, translate_test);


        translate_test.setOnAction(_ -> TranslationContext.instance().changeLanguage(Locale.of("pt-br")));
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

    @Component
    Menu createMenuSettings() {
        Menu menu = new Menu();
        Label menuText = Typography.caption(translation.settings());
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(_ -> controller.handleClickMenuSettings(stage));

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    private void handleShowJavaCode(CanvaComponent canvaComponent) {
        new ShowCode(canvaComponent)
                .abrir();

    }

}
