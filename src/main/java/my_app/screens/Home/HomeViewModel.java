package my_app.screens.Home;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.components.ColumnComponent;
import my_app.components.Components;
import my_app.components.InputComponentv2;
import my_app.components.TextComponentv2;
import my_app.components.buttonComponent.ButtonComponentv2;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.ComponentData;
import my_app.data.StateJson_v3;
import my_app.data.ViewContractv2;
import my_app.mappers.CanvaMapper;
import my_app.scenes.SettingsScene;
import my_app.screens.Home.components.ScreenTab;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import my_app.windows.AllWindows;
import toolkit.Component;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class HomeViewModel {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    ComponentsContext componentsContext;
    public StringProperty uiPathProperty = new SimpleStringProperty();

    private Home home;
    private Stage stage;

    public StringProperty currentScreenId = new SimpleStringProperty();

    BooleanProperty refreshScreensTabs = new SimpleBooleanProperty();
    public SimpleBooleanProperty leftItemsStateRefreshed = new SimpleBooleanProperty(false);

    public SimpleStringProperty headerSelected = new SimpleStringProperty(null);

    //key é o type
    public ObservableMap<String, ObservableList<ViewContractv2<?>>> dataMap = FXCollections
            .observableHashMap();

    public void init(Home home, Stage theirStage) {
        this.home = home;
        this.stage = theirStage;

        fillMenuBar(home.menuBar);

        final var projectData = FileManager.getProjectData();
        final var firstScreen = projectData.screens().getFirst();
        loadScreenAndApplyToCanva(firstScreen);

        // Criando as tabs que ficam sobre o Canva central
        this.refreshScreensTabs.addListener((_, _, _) -> {
            final var updatedProjectData = FileManager.getProjectData();

            this.home.screensTabs.getChildren().clear();
            for (var screen : updatedProjectData.screens()) {
                ScreenTab tab = new ScreenTab(screen, this);

                this.home.screensTabs.getChildren().add(tab);
            }
            final var btnAdd = Components.ButtonPrimary("+");
            btnAdd.setOnMouseClicked(ev -> {
                dataMap.clear();
                headerSelected.set(null);
                nodeSelected.set(null);
                leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());

                // 1. Cria o novo objeto StateJson_v3 (sem salvar no disco ainda)
                final var screen = new StateJson_v3();
                // 3. Atualiza a referência da UI
                final var canvaGerado = loadScreenAndApplyToCanva(screen);

                // 4. Salva a nova tela (com dados iniciais do canva) e o projeto:
                // Serializa os dados padrão do Canva recém-criado de volta para o objeto screen.
                final var updatedScreen = CanvaMapper.toStateJson(canvaGerado, this);

                // Adiciona a tela (agora com dados básicos) à lista do projeto e salva no disco.
                FileManager.addScreenToProjectAndSave(updatedScreen); // <<< NOVO MÉTODO NO FileManager

                toggleRefreshScreenTabs();
            });

            this.home.screensTabs.getChildren().add(btnAdd);
        });

        toggleRefreshScreenTabs();
    }


    public void handleTabClicked(String screenIdToLoad) {

        // 1. VERIFICAÇÃO DE ECONOMIA: Se a tela já está carregada, saia.
        if (this.currentScreenId.get() != null && this.currentScreenId.get().equals(screenIdToLoad)) {
            IO.println("Tela já está ativa. Pulando recarregamento.");
            return; // Sai do método sem carregar/processar
        }

        // Limpeza de estados globais
        dataMap.clear();
        headerSelected.set(null);
        nodeSelected.set(null);
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());


        // SOLUÇÃO: Busque a versão MAIS RECENTE da tela no disco USANDO O ID
        final var latestProjectData = FileManager.getProjectData();
        final var latestScreenOptional = latestProjectData.screens().stream()
                .filter(s -> s.screen_id.equals(screenIdToLoad))
                .findFirst();

        if (latestScreenOptional.isPresent()) {
            this.loadScreenAndApplyToCanva(latestScreenOptional.get());
        } else {
            IO.println("Erro: Tela com ID " + screenIdToLoad + " não encontrada ao clicar.");
        }
    }

    private CanvaComponentV2 loadScreenAndApplyToCanva(StateJson_v3 screen) {

        IO.println("vai tentar atualizar a ui atual do canva: " + screen.name);

        final var newCanva = CanvaMapper.fromScreenToCanva(screen, this);
        this.currentScreenId.set(screen.screen_id);

        // Chame o novo método da Home para atualizar a UI
        this.home.updateCanvaInEditor(newCanva);

        final var prefsData = FileManager.loadDataInPrefs();
        //acessar o arqivo de projeto
        final var absolutePath = prefsData.last_project_saved_path();
        final var projectFile = new File(absolutePath);
        uiJsonFile = projectFile;
        uiPathProperty.set(uiJsonFile.getAbsolutePath());

        return newCanva;
    }

    public void removeNode(String nodeId) {
        final var currentCanva = home.currentCanva;
        System.out.println("mainCanva: " + currentCanva);
        // 1. Tenta remover o Node do mainCanva (UI)
        ObservableList<Node> canvaChildren = currentCanva.getChildren();
        boolean removedFromCanva = canvaChildren.removeIf(node -> nodeId.equals(node.getId()));

        // 2. Remove do dataMap (a coleção de dados)
        boolean removedFromDataMap = removeItemByIdentification(nodeId);

        Node currentlySelectedNode = nodeSelected.get() != null ? nodeSelected.get().node() : null;

        if (currentlySelectedNode != null && nodeId.equals(currentlySelectedNode.getId())) {
            nodeSelected.set(null);
            headerSelected.set(null); // Limpa o header também
        }

        // 4. Atualiza a UI lateral SOMENTE se a remoção foi bem-sucedida em algum lugar
        if (removedFromCanva || removedFromDataMap) {
            refreshSubItems();
        }
    }

    private boolean removeItemByIdentification(String identification) {
        // Itera sobre todas as listas de nós no dataMap.
        for (var itemsList : dataMap.values()) {

            // Procura o item a ser removido (a forma mais garantida para ObservableList)
            ViewContractv2<?> itemToRemove = null;
            for (var item : itemsList) {
                if (identification.equals(item.getCurrentNode().getId())) {
                    itemToRemove = item;
                    break;
                }
            }

            if (itemToRemove != null) {
                // Remove o item da ObservableList do dataMap
                itemsList.remove(itemToRemove);
                // Retorna true assim que o item for removido
                return true;
            }
        }
        // Retorna false se o item não for encontrado em nenhuma lista
        return false;
    }

// HomeViewModel.java

    /**
     * Exclui uma tela do projeto, lida com a mudança de tela se a tela atual for removida,
     * e atualiza a interface de abas.
     *
     * @param screenId O ID da tela a ser excluída.
     */
    public void deleteScreen(String screenId) {

        // 1. Verificar se a tela sendo excluída é a tela atualmente visível.
        boolean wasCurrentScreen = this.currentScreenId.get() != null
                && this.currentScreenId.get().equals(screenId);

        // 2. Excluir a tela do arquivo JSON (persiste a mudança no disco).
        FileManager.deleteScreenFromProject(screenId); // Usando o nome que definimos anteriormente

        // 3. Obter a lista atualizada de telas após a exclusão.
        final var projectData = FileManager.getProjectData();
        final var remainingScreens = projectData.screens();

        if (remainingScreens.isEmpty()) {
            // Cenário 3a: Não há mais telas. Crie uma nova tela padrão para evitar um estado vazio.
            IO.println("Todas as telas foram excluídas. Criando uma nova tela padrão.");

            final var newDefaultScreen = new StateJson_v3();
            final var canvaGerado = loadScreenAndApplyToCanva(newDefaultScreen);

            final var updatedScreen = CanvaMapper.toStateJson(canvaGerado, this);
            FileManager.addScreenToProjectAndSave(updatedScreen);

        } else if (wasCurrentScreen) {
            // Cenário 3b: A tela ativa foi excluída. Carregue a ultima tela restante.
            final var nextScreen = remainingScreens.getLast();
            this.loadScreenAndApplyToCanva(nextScreen);
        }

        // 4. Forçar o redesenho da barra de abas.
        toggleRefreshScreenTabs();

        // 5. Limpeza de estados, se necessário
        dataMap.clear();
        headerSelected.set(null);
        nodeSelected.set(null);
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }

    public record SelectedComponent(String type, Node node) {
    }

    public SimpleObjectProperty<SelectedComponent> nodeSelected = new SimpleObjectProperty<>();

    public void toggleRefreshScreenTabs() {
        refreshScreensTabs.set(!refreshScreensTabs.get());
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

        final var selected = nodeSelected.get();

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
                SelectedComponent newSelection = new SelectedComponent(type, node);
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

    public void addComponent(String type) {

        final var currentCanva = home.currentCanva;

        if (type == null || type.isBlank()) {
            return;
        }

        ViewContractv2<?> node = null;
        var content = "Im new here";

        var typeNormalized = type.trim().toLowerCase();


        if (type.equalsIgnoreCase(englishBase.button())) {
            node = new ButtonComponentv2(content, this);
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            node = new InputComponentv2(content, this, currentCanva);

        } else if (type.equalsIgnoreCase(englishBase.text())) {
            node = new TextComponentv2(content, this, currentCanva);

        } else if (type.equalsIgnoreCase(englishBase.image())) {
            node = new ImageComponentv2(
                    ComponentsContext.class.getResource("/assets/images/mago.jpg").toExternalForm(),
                    this);

        } else if (type.equalsIgnoreCase(englishBase.component())) {
            //  new ShowComponentScene(currentCanva, this).stage.show();
            return;
        } else if (type.equalsIgnoreCase(englishBase.columnItems())) {
            node = new ColumnComponent(this, currentCanva);
        }

        if (node != null) {

            // 1. Adiciona o nó ao dataMap
            addItem(typeNormalized, node);

            // 2. CRIA E ATUALIZA o nodeSelected com o novo objeto SelectedComponent
            // ESTA É A LINHA CORRIGIDA

            final var newSelection = new SelectedComponent(typeNormalized, node.getCurrentNode());
            nodeSelected.set(newSelection);

            // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
            headerSelected.set(typeNormalized);

            // 4. Adiciona o nó à tela (Canva)
            currentCanva.addElementDragable(node.getCurrentNode(), true);

            // 5. Notifica a UI lateral para atualizar a lista
            refreshSubItems();
        }

    }

    public void refreshSubItems() {
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }

    public void addItem(String type, ViewContractv2<?> nodeWrapper) {
        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .add(nodeWrapper);
    }

    public void handleSave() {
        home.leftSide.removeError();
        final var screen = CanvaMapper.toStateJson(home.currentCanva, this);

        FileManager.updateScreen(screen);

        // FileManager.updateProject(CanvaMapper.toStateJson( home.currentCanva));
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
        MenuItem itemContribute = new MenuItem(translation.optionsMenuMainScene().becomeContributor());

        menu.getItems().addAll(itemNovo, itemSalvar, itemSaveAs, itemLoad, itemContribute);

        //itemNovo.setOnAction(_ -> handleNew(home, stage));
        itemSalvar.setOnAction(_ -> handleSave());
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


    @Deprecated
    public record PrefsData(String last_project_saved_path, String language) {
    }

    public HomeViewModel(ComponentsContext componentsContext) {
        this.componentsContext = componentsContext;
    }

    private File uiJsonFile;

    public void handleSaveAs_(Home home, Stage stage) {
        home.leftSide.removeError();

        //...
    }


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


    public void removeComponentFromAllPlaces(ViewContractv2<?> componentWrapper, CanvaComponentV2 canvaComponent) {
        removeComponentFromCanva(componentWrapper, canvaComponent);
        removeComponentFromDataMap(componentWrapper);
        refreshSubItems();
    }

    public void removeComponentFromCanva(ViewContractv2<?> componentWrapper, CanvaComponentV2 canvaComponent) {
        canvaComponent.getChildren().remove(componentWrapper.getCurrentNode());
    }

    public void removeComponentFromDataMap(ViewContractv2<?> componentWrapper) {
        var data = (ComponentData) componentWrapper.getData();
        var list = dataMap.get(data.type());

        var currentNodeId = componentWrapper.getCurrentNode().getId();

        //list.removeIf(it -> it.getCurrentNode().getId().equals(currentNodeId));
        list.stream().filter(it -> it.getCurrentNode().getId().equals(currentNodeId))
                .findFirst().ifPresent(it -> {
                    //deletou de mentirinha
                    it.delete();
                });
        IO.println("removeu do datamap");
    }

    public ViewContractv2<?> findNodeById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        // Itera sobre todas as listas de ViewContract no dataMap (os valores do mapa)
        for (var viewList : dataMap.values()) {

            // Itera sobre cada ViewContract dentro da lista atual
            for (ViewContractv2<?> contract : viewList) {

                // Verifica se o ID do ViewContract (que representa o Node) é igual ao ID procurado
                // A verificação de null/empty deve ser feita dentro do contrato ou ao chamar getId()
                if (id.equals(contract.getData().identification())) {
                    return contract; // Encontrado! Retorna o contrato.
                }
            }
        }

        // Se o loop terminar e nada for encontrado
        return null;
    }

    public static Node SearchNodeByIdInMainCanva(String nodeId, ObservableList<Node> canvaChildren) {
        // lookin for custom component in main canva
        return canvaChildren.stream()
                .filter(n -> nodeId.equals(n.getId()))
                .findFirst()
                .orElse(null);
    }

}
