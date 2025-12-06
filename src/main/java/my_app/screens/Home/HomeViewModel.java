package my_app.screens.Home;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.mappers.CanvaMapper;
import my_app.scenes.SettingsScene;
import my_app.themes.Typography;
import my_app.windows.AllWindows;
import toolkit.Component;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import static my_app.data.Commons.loadPrefs;

public class HomeViewModel {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public StringProperty uiPathProperty = new SimpleStringProperty();

    private Home home;
    private Stage stage;

    BooleanProperty refreshScreensTabs = new SimpleBooleanProperty();

    public void init(Home home, Stage theirStage) {
        this.home = home;
        this.stage = theirStage;

        fillMenuBar(home.menuBar);
        loadScreenAndApplyToCanva();

        toggleRefreshScreenTabs();
    }

    public void toggleRefreshScreenTabs() {
        refreshScreensTabs.set(!refreshScreensTabs.get());
    }

    private void loadScreenAndApplyToCanva() {
        final var prefsData = FileManager.loadDataInPrefs();

        //acessar o arqivo de projeto
        final var absolutePath = prefsData.last_project_saved_path();
        final var projectFile = new File(absolutePath);
        uiJsonFile = projectFile;
        uiPathProperty.set(uiJsonFile.getAbsolutePath());
        //conteudo do arquivo é um json Project

        //final var projectData = om.readValue(projectFile, Project.class);
        componentsContext.loadJsonState_(uiJsonFile, home.canva, stage);
    }

    public void handleSave(Home home, Stage stage) {
        //updateUiJsonFilePathOnAppData(uiJsonFile);
        FileManager.updateProject(CanvaMapper.toStateJson(home.canva, componentsContext));
        //componentsContext.saveStateInJsonFile_v2(uiJsonFile, home.canva);
    }

    public void handleClickMenuSettings(Stage stage) {
        new SettingsScene().show();
    }

    public void fillMenuBar(MenuBar menuBar) {
        menuBar.getMenus().setAll(createMenuOptions(), createMenuSettings(), createMenuDataTable(), createMenuUiPath());
    }

    @Component
    Menu createMenuOptions() {
        Menu menu = new javafx.scene.control.Menu();
        Label menuText = Typography.caption(translation.common().option());
        menu.setGraphic(menuText);

        MenuItem itemNovo = new MenuItem(translation.new_());
        MenuItem itemSalvar = new MenuItem(translation.common().save());
        MenuItem itemSaveAs = new MenuItem(translation.common().saveAs());
        MenuItem itemLoad = new MenuItem(translation.common().load());
        MenuItem itemShowCode = new MenuItem(translation.optionsMenuMainScene().showCode());
        MenuItem itemContribute = new MenuItem(translation.optionsMenuMainScene().becomeContributor());

        menu.getItems().addAll(itemNovo, itemSalvar, itemSaveAs, itemLoad, itemShowCode, itemContribute);

        //itemNovo.setOnAction(_ -> handleNew(home, stage));
        itemSalvar.setOnAction(_ -> handleSave(home, stage));
        itemSaveAs.setOnAction(_ -> {

            try {
                handleSaveAs_(home, stage);
            } catch (RuntimeException e) {
                home.leftSide.notifyError(e.getMessage());
            }
        });

        // itemLoad.setOnAction(_ -> handleClickLoad(home, stage));

        //itemShowCode.setOnAction(_ -> handleShowJavaCode(home.canva));

        itemContribute.setOnAction(_ -> handleBecomeContributor());

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    @Component
    Menu createMenuSettings() {
        Menu menu = new Menu();
        Label menuText = Typography.caption(translation.settings());
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(_ -> handleClickMenuSettings(this.stage));

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    @Component
    Menu createMenuDataTable() {
        var menu = new Menu();
        Label menuText = Typography.caption("Data table");
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(ev -> {
            AllWindows.showWindowForDataTable(componentsContext);
        });
        return menu;
    }

    @Component
    Menu createMenuUiPath() {
        Menu menu = new Menu();
        Label menuText = Typography.caption("path of ui file");
        menuText.textProperty().bind(this.uiPathProperty);

        menu.setGraphic(menuText);

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }


    public void handleNew(Home home, Stage stage) {
        home.canva.getChildren().clear();
        componentsContext.reset();
        uiPathProperty.set("");
    }


    @Deprecated
    public record PrefsData(String last_project_saved_path, String language) {
    }

    public HomeViewModel(ComponentsContext componentsContext) {
        this.componentsContext = componentsContext;
    }

    private File uiJsonFile;

    public void handleClickLoad(Home home, Stage stage) {
        var fc = new FileChooser();

        fc.setTitle("open selected project");
        fc.getExtensionFilters().add(
                new ExtensionFilter("ui extension", "*.json"));

        var uiFile = fc.showOpenDialog(stage);
        if (uiFile != null) {
            uiJsonFile = uiFile;
            componentsContext.loadJsonState(uiFile, home.canva, stage);
            uiPathProperty.set(uiFile.getAbsolutePath());
        }
    }


    private File loadUiFileFromAppData() {
        String appData = loadPrefs();

        var appFolder = new File(appData, Commons.AppNameAtAppData);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        var prefsJsonFile = new File(appFolder, "prefs.json");

        if (!prefsJsonFile.exists()) {
            // cria arquivo padrão na primeira execução
            var defaultPrefs = new PrefsData("", TranslationContext.instance().currentLanguage());
            Commons.WriteJsonInDisc(prefsJsonFile, defaultPrefs);
            return null; // ainda não há projeto salvo
        }

        try (var stream = new FileInputStream(prefsJsonFile)) {
            var om = new ObjectMapper();
            final var path = om.readValue(stream, PrefsData.class).last_project_saved_path;
            return path == null || path.isBlank() ? null : new File(path);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível carregar prefs.json", e);
        }
    }


    public void handleSaveAs_(Home home, Stage stage) {
        home.leftSide.removeError();

        //...
    }


    private void updateUiJsonFilePathOnAppData(File file) {

        String appData = loadPrefs();

        var appFolder = new File(appData, Commons.AppNameAtAppData);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        var fileInCurrentDirectory = new File(appFolder, "prefs.json");

        var pref = new PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
        Commons.WriteJsonInDisc(fileInCurrentDirectory, pref);
    }

//    private String loadPrefs() {
//        String appData = System.getenv("LOCALAPPDATA"); // C:\Users\<user>\AppData\Local
//        if (appData == null) {
//            appData = System.getProperty("user.home") + "\\AppData\\Local";
//        }
//        return appData;
//    }


    public void handleBecomeContributor() {
        // https://buymeacoffee.com/plantfall

        Thread.ofVirtual().start(() -> {
            try {
                Desktop.getDesktop().browse(
                        new URI("https://buymeacoffee.com/plantfall"));
            } catch (Exception e) {
                throw new RuntimeException("Was not possible to go to donation");
            }
        });
    }

}
