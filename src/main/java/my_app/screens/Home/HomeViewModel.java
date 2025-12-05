package my_app.screens.Home;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import my_app.components.ColumnComponent;
import my_app.components.CustomComponent;
import my_app.components.InputComponent;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.StateJson_v2;
import my_app.data.ViewContract;
import my_app.scenes.SettingsScene;
import my_app.themes.Typography;
import toolkit.Component;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Path;

import static my_app.data.Commons.loadPrefs;

public class HomeViewModel {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public StringProperty uiPathProperty = new SimpleStringProperty();

    private Home home;
    private Stage stage;

    public void init(Home home, Stage theirStage) {
        this.home = home;
        this.stage = theirStage;

        fillMenuBar(home.menuBar);
    }

    public void handleClickMenuSettings(Stage stage) {
        new SettingsScene().show();
    }

    public void fillMenuBar(MenuBar menuBar) {
        //menuBar.getMenus().setAll(createMenuOptions(), createMenuSettings(), createMenuDataTable(), createMenuUiPath());
        menuBar.getMenus().setAll(createMenuOptions(), createMenuSettings());
    }

    @Component
    javafx.scene.control.Menu createMenuOptions() {
        javafx.scene.control.Menu menu = new javafx.scene.control.Menu();
        javafx.scene.control.Label menuText = Typography.caption(translation.common().option());
        menu.setGraphic(menuText);

        javafx.scene.control.MenuItem itemNovo = new javafx.scene.control.MenuItem(translation.new_());
        javafx.scene.control.MenuItem itemSalvar = new javafx.scene.control.MenuItem(translation.common().save());
        javafx.scene.control.MenuItem itemSaveAs = new javafx.scene.control.MenuItem(translation.common().saveAs());
        javafx.scene.control.MenuItem itemLoad = new javafx.scene.control.MenuItem(translation.common().load());
        javafx.scene.control.MenuItem itemShowCode = new javafx.scene.control.MenuItem(translation.optionsMenuMainScene().showCode());
        javafx.scene.control.MenuItem itemContribute = new javafx.scene.control.MenuItem(translation.optionsMenuMainScene().becomeContributor());
        menu.getItems().addAll(itemNovo, itemSalvar, itemSaveAs, itemLoad, itemShowCode, itemContribute);


        //itemNovo.setOnAction(_ -> handleNew(home, stage));
        //itemSalvar.setOnAction(_ -> handleSave(home, stage));
        itemSaveAs.setOnAction(_ -> {

            try {
                handleSaveAs_(home, stage);
            } catch (RuntimeException e) {
                home.leftSide.notifyError(e.getMessage());
            }

        });

        // itemLoad.setOnAction(_ -> handleClickLoad(home, stage));

        // itemShowCode.setOnAction(_ -> handleShowJavaCode(home.canva));

        itemContribute.setOnAction(_ -> handleBecomeContributor());

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    @Component
    javafx.scene.control.Menu createMenuSettings() {
        javafx.scene.control.Menu menu = new javafx.scene.control.Menu();
        Label menuText = Typography.caption(translation.settings());
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(_ -> handleClickMenuSettings(this.stage));

        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }


    public void handleNew(Home home, Stage stage) {
        home.canva.getChildren().clear();
        componentsContext.reset();
        uiPathProperty.set("");
    }

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

    public void loadSceneFromJsonFile(Home home, Stage stage) {
        try {
            uiJsonFile = loadUiFileFromAppData();
            componentsContext.loadJsonState(uiJsonFile, home.canva, stage);
            uiPathProperty.set(uiJsonFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            componentsContext.loadJsonState(null, home.canva, stage);
        }
    }

    public void handleSave(Home home, Stage stage) {
        // if (uiJsonFile == null) {
        // handleSaveAs(home, stage);
        // return;
        // }

        updateUiJsonFilePathOnAppData(uiJsonFile);

        componentsContext.saveStateInJsonFile_v2(uiJsonFile, home.canva);
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

        var fc = new FileChooser();

        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName("ui.json");

        try {
            var file = fc.showSaveDialog(stage);
            if (file != null) {
                // Gera o StateJson_v2 a partir dos Nodes e do CanvaComponent
                StateJson_v2 data = createStateData(home.canva);
                //FileManager.saveNewProject(data);
                // json bening saved on specfif file
                componentsContext.saveStateInJsonFile_v2(file, home.canva);

                //saving also the prefs
                //check if file exists
                String appData = loadPrefs();
                var prefsFile = Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");

                var defaultPrefs = new PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
                Commons.WriteJsonInDisc(prefsFile.toFile(), defaultPrefs);

                IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());
                uiPathProperty.set(file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private StateJson_v2 createStateData(CanvaComponent canva) {
        StateJson_v2 jsonTarget = new StateJson_v2();

        final var nodeSelected = componentsContext.nodeSelected;
        jsonTarget.id_of_component_selected = nodeSelected.get() == null ? null
                : nodeSelected.getValue().node().getId();

        final var headerSelected = componentsContext.headerSelected.get();
        jsonTarget.type_of_component_selected = headerSelected;

        // 1. Salva as propriedades do CanvaComponent
        jsonTarget.canva = canva.getData();

        // 2. Itera sobre TODOS os nós (nodes) no dataMap
        // Para cada lista de nós (os VALUES do dataMap)...
        for (ObservableList<ViewContract<?>> nodesList : componentsContext.dataMap.values()) {
            // ...itera sobre cada Node dentro dessa lista.
            for (var nodeWrapper : nodesList) {
                // A LÓGICA DE SERIALIZAÇÃO PERMANECE A MESMA

                var node = nodeWrapper.getCurrentNode();

                if (node instanceof TextComponent component) {
                    // O .getData() deve retornar um TextComponentData que inclui a flag 'in_canva'
                    jsonTarget.text_components.add(component.getData());
                }

                if (node instanceof ButtonComponent component) {
                    jsonTarget.button_components.add(component.getData());
                }

                if (node instanceof ImageComponent component) {
                    jsonTarget.image_components.add(component.getData());
                }

                if (node instanceof InputComponent component) {
                    jsonTarget.input_components.add(component.getData());
                }

                // Se o FlexComponent for uma composição de outros nós, ele deve serializar seus
                // filhos internamente.
                // CustomComponent, se for salvo como InnerComponentData.
                // Verifique se o getData() dele é compatível com InnerComponentData.
                // **Atenção:** Se ele for uma instância que contém outros componentes,
                // sua lógica de getData() deve ser recursiva (salvar seus filhos).
                if (node instanceof CustomComponent component) {
                    // Supondo que getData() retorne InnerComponentData ou StateJson_v2 completo
                    jsonTarget.custom_components.add(component.getData());
                }

                if (node instanceof ColumnComponent component) {
                    jsonTarget.column_components.add(component.getData());
                }
            } // Fim do loop interno (iteração sobre Nodes)
        } // Fim do loop externo (iteração sobre as Listas)

        return jsonTarget;
    }

    @Deprecated
    public void handleSaveAs(Home home, Stage stage) {
        home.leftSide.removeError();

        var fc = new FileChooser();

        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName("ui.json");

        try {
            var file = fc.showSaveDialog(stage);
            if (file != null) {
                // json bening saved on specfif file
                componentsContext.saveStateInJsonFile_v2(file, home.canva);

                //saving also the prefs
                //check if file exists
                String appData = loadPrefs();
                var prefsFile = Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");

                var defaultPrefs = new PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
                Commons.WriteJsonInDisc(prefsFile.toFile(), defaultPrefs);

                IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());
                uiPathProperty.set(file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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

        try {
            Desktop.getDesktop().browse(
                    new URI("https://buymeacoffee.com/plantfall"));
        } catch (Exception e) {
            throw new RuntimeException("Was not possible to go to donation");
        }
    }

}
