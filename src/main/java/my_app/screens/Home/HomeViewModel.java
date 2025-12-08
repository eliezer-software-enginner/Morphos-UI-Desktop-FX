package my_app.screens.Home;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.components.Components;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.ViewContractv2;
import my_app.mappers.CanvaMapper;
import my_app.scenes.SettingsScene;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import my_app.windows.AllWindows;
import toolkit.Component;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static my_app.data.Commons.loadPrefs;

public class HomeViewModel {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public StringProperty uiPathProperty = new SimpleStringProperty();

    private Home home;
    private Stage stage;

    StringProperty currentScreenId = new SimpleStringProperty();

    BooleanProperty refreshScreensTabs = new SimpleBooleanProperty();
    public SimpleBooleanProperty leftItemsStateRefreshed = new SimpleBooleanProperty(false);

    public SimpleStringProperty headerSelected = new SimpleStringProperty(null);

    public record SelectedComponent(String type, Node node) {
    }

    public SimpleObjectProperty<ComponentsContext.SelectedComponent> nodeSelected = new SimpleObjectProperty<>();


    //key é o type
    public ObservableMap<String, ObservableList<ViewContractv2<?>>> dataMap = FXCollections
            .observableHashMap();

    public void init(Home home, Stage theirStage) {
        this.home = home;
        this.stage = theirStage;

        fillMenuBar(home.menuBar);
        loadScreenAndApplyToCanva();

        this.refreshScreensTabs.addListener((_, _, _) -> {
            final var updatedProjectData = FileManager.getProjectData();

            this.home.screensTabs.getChildren().clear();
            for (var screen : updatedProjectData.screens()) {
                MenuButton menu = new MenuButton(screen.name);
                MenuItem itemShowCode = new MenuItem(translation.optionsMenuMainScene().showCode());
                itemShowCode.setOnAction(ev -> {
                    //AllWindows.showWindowForShowCode(componentsContext, canva);
                });

                menu.getItems().add(itemShowCode);

                this.home.screensTabs.getChildren().add(menu);
            }

            this.home.screensTabs.getChildren().add(Components.ButtonPrimary("+"));
        });

        toggleRefreshScreenTabs();
    }

    public void toggleRefreshScreenTabs() {
        refreshScreensTabs.set(!refreshScreensTabs.get());
    }

    private void loadScreenAndApplyToCanva() {
        final var prefsData = FileManager.loadDataInPrefs();

        final var projectData = FileManager.getProjectData();
        final var screen = projectData.screens().getFirst();

        home.currentCanva = CanvaMapper.fromScreenToCanva(screen, this);

        //acessar o arqivo de projeto
        final var absolutePath = prefsData.last_project_saved_path();
        final var projectFile = new File(absolutePath);
        uiJsonFile = projectFile;
        uiPathProperty.set(uiJsonFile.getAbsolutePath());
        //conteudo do arquivo é um json Project

        //final var projectData = om.readValue(projectFile, Project.class);
        //componentsContext.loadJsonState_(uiJsonFile, home.canva, stage);
    }

    public void addItemOnDataMap(String type, ViewContractv2<?> nodeWrapper) {
        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .add(nodeWrapper);
    }

    public Optional<ViewContractv2<?>> SearchNodeById(String nodeId) {
        return dataMap.values()
                .stream()
                .flatMap(list -> list.stream()) // Achata todas as listas em um único stream
                .filter(node -> node.getCurrentNode().getId().equals(nodeId))
                .findFirst();
    }


    public List<ViewContractv2<?>> getItemsByType(String type) {
        final var originalList =
                dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList());

        // Retorna uma lista simples filtrada (List)
        return originalList.stream()
                .filter(component -> !component.isDeleted())
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean currentNodeIsSelected(String nodeId) {

        ComponentsContext.SelectedComponent selected = nodeSelected.get();

        // 1. Verifica se algo está selecionado (selected != null)
        // 2. Verifica se o Node dentro do SelectedComponent não é nulo (selected.node()
        // != null)
        // 3. Compara o ID do Node selecionado com o nodeId fornecido
        return selected != null && selected.node() != null && selected.node().getId().equals(nodeId);
    }

    // --- NOVO MÉTODO SELECTNODE ---
    public void selectNode(Node node) {
        if (node == null) {
            nodeSelected.set(null);
            headerSelected.set(null); // Desseleciona o header também
        } else {
            String type = getNodeType(node);
            if (type != null) {
                ComponentsContext.SelectedComponent newSelection = new ComponentsContext.SelectedComponent(type, node);
                nodeSelected.set(newSelection);
                headerSelected.set(type); // Mantemos o headerSelected por compatibilidade com a UI
                System.out.println("Selecionado: " + node + " (Type: " + type + ")");
            } else {
                // Lidar com o caso onde o nó existe mas não está no dataMap
                System.err.println("Erro: Node encontrado, mas não está registrado no dataMap. ID: " + node.getId());
                nodeSelected.set(null);
                headerSelected.set(null);
            }
        }
        refreshSubItems();
    }

    public String getNodeType(Node node) {
        if (node == null) {
            return null;
        }
        String nodeId = node.getId();

        // Itera sobre o mapa para encontrar a chave (tipo) que contém o Node.
        for (var entry : dataMap.entrySet()) {
            if (entry.getValue().stream().anyMatch(n -> node.getId().equals(nodeId))) {
                return entry.getKey();
            }
        }
        return null;
    }

    TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    public void addComponent(String type, CanvaComponentV2 currentCanva) {

        if (type == null || type.isBlank()) {
            return;
        }

        ViewContractv2<?> node = null;
        var content = "Im new here";

        var typeNormalized = type.trim().toLowerCase();

        /*
        if (type.equalsIgnoreCase(englishBase.button())) {
            node = new ButtonComponentv2(content, this);
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            // node = new InputComponent(content, this, currentCanva);

        } else if (type.equalsIgnoreCase(englishBase.text())) {
            node = new TextComponent(content, this, mainCanvaComponent);

        } else if (type.equalsIgnoreCase(englishBase.image())) {
            node = new ImageComponent(
                    ComponentsContext.class.getResource("/assets/images/mago.jpg").toExternalForm(),
                    this);

        } else if (type.equalsIgnoreCase(englishBase.component())) {
            //  new ShowComponentScene(currentCanva, this).stage.show();
            return;
        } else if (type.equalsIgnoreCase(englishBase.columnItems())) {
            node = new ColumnComponent(this, mainCanvaComponent);
        }

        if (node != null) {

            // 1. Adiciona o nó ao dataMap
            addItem(typeNormalized, node);

            // 2. CRIA E ATUALIZA o nodeSelected com o novo objeto SelectedComponent
            // ESTA É A LINHA CORRIGIDA

            ComponentsContext.SelectedComponent newSelection = new ComponentsContext.SelectedComponent(typeNormalized, node.getCurrentNode());
            nodeSelected.set(newSelection);

            // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
            headerSelected.set(typeNormalized);

            // 4. Adiciona o nó à tela (Canva)
            currentCanva.addElementDragable(node.getCurrentNode(), true);

            // 5. Notifica a UI lateral para atualizar a lista
            refreshSubItems();
        }
         */
    }

    public void refreshSubItems() {
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }

    public void addItem(String type, ViewContractv2<?> nodeWrapper) {
        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .add(nodeWrapper);
    }

    public void handleSave(Home home, Stage stage) {
        //updateUiJsonFilePathOnAppData(uiJsonFile);
        FileManager.updateProject(CanvaMapper.toStateJson(home.currentCanva, componentsContext));
        //componentsContext.saveStateInJsonFile_v2(uiJsonFile, home.canva);
    }

    public void handleClickMenuSettings() {
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

        menuText.setOnMouseClicked(_ -> handleClickMenuSettings());

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
        home.currentCanva.getChildren().clear();
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
            // componentsContext.loadJsonState(uiFile, home.canva, stage);
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
