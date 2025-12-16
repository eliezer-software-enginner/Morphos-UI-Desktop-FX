package my_app.screens.Home;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.components.ComponentsFactory;
import my_app.contexts.TranslationContext;
import my_app.data.ComponentData;
import my_app.data.StateJson_v3;
import my_app.data.contracts.ViewComponent;
import my_app.mappers.CanvaMapper;
import my_app.scenes.AppScenes;
import my_app.scenes.SettingsScene;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.windows.AllWindows;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class HomeViewModel {

    // --- DEPENDÊNCIAS & SERVIÇOS ---
    private final TranslationContext.Translation translation = TranslationContext.instance().get();
    private final TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    // Mantemos o Stage para diálogos de arquivo (FileChooser) e troca de cenas
    private final Stage stage;

    // --- ESTADO OBSERVÁVEL (A View faz Bind nisso) ---

    // 1. Lista de Abas (Screens) para a UI renderizar
    public ObservableList<StateJson_v3> screenTabs = FXCollections.observableArrayList();

    // 2. O conteúdo central atual (O Canva Ativo)
    public ObjectProperty<CanvaComponentV2> activeCanva = new SimpleObjectProperty<>();

    // 3. ID da tela atual
    public StringProperty currentScreenId = new SimpleStringProperty();

    // 4. Propriedades de Interface
    public StringProperty uiPathProperty = new SimpleStringProperty();
    public StringProperty headerSelected = new SimpleStringProperty(null);
    public SimpleBooleanProperty leftItemsStateRefreshed = new SimpleBooleanProperty(false);

    // 5. Seleção de Componentes
    public SimpleObjectProperty<SelectedComponent> nodeSelected = new SimpleObjectProperty<>();

    // 6. Mapa de Dados dos Componentes (Lógica Interna)
    // Key: Tipo do componente ("button", "text", etc.)
    public ObservableMap<String, ObservableList<ViewComponent<?>>> dataMap = FXCollections.observableHashMap();

    // Propriedades para Toast/Erro (Opcional, se a View tiver um Toast)
    public StringProperty errorMessageProperty = new SimpleStringProperty();
    public StringProperty showToastProperty = new SimpleStringProperty();


    // --- CONSTRUTOR & INICIALIZAÇÃO ---

    public HomeViewModel(Stage stage) {
        this.stage = stage;
    }

    /**
     * Chamado pela View logo após a criação para carregar os dados iniciais.
     */
    public void init(boolean isCustomComponent) {
        if (isCustomComponent) {
            createDefaultScreen(isCustomComponent);
            return;
        }
        loadProjectData();
    }

    private void loadProjectData() {
        var projectData = FileManager.getProjectData();

        // Atualiza a lista observável de abas (A View reagirá a isso automaticamente)
        if (projectData != null && !projectData.screens().isEmpty()) {
            screenTabs.setAll(projectData.screens());

            // Carrega a primeira tela
            try {
                loadScreenById(projectData.screens().getFirst().screen_id);
            } catch (Exception e) {
                IO.println("Erro ao carregar primeira tela: " + e.getMessage());
                createDefaultScreen(false);
            }
        } else {
            createDefaultScreen(false);
        }
    }

    private void createDefaultScreen(boolean isCustomComponent) {
        var screen = new StateJson_v3();
        // 1. Gera o canva temporário (necessário para ter o CanvaComponentV2 com defaults)
        var newCanva = CanvaMapper.fromScreenToCanva(screen, this);

        // 2. Converte de volta para StateJson_v3 para obter o ID e dados defaults corretos
        var updatedScreen = CanvaMapper.toStateJson(newCanva, this);

        // --- CORREÇÃO: Lógica para Custom Component ---
        if (isCustomComponent) {
            // Se for um componente customizado, APENAS carrega o Canva na UI,
            // mas NÃO salva no disco e NÃO adiciona na lista de abas (screenTabs).

            // 2.1. Define o ID da tela temporária
            this.currentScreenId.set(updatedScreen.screen_id);

            // 2.2. Ativa o Canva. Isso dispara o listener em Home.java
            // que cria LeftSide, RightSide e centraliza o Canva.
            this.activeCanva.set(newCanva);

            // 2.3. Seleciona o Canva para carregar o RightSide
            this.selectNode(newCanva);

            // Não precisa de updateUiPathProperty() para componente customizado
            return;
        }
        // ----------------------------------------------

        // Lógica original para projetos normais (salva e carrega)
        try {
            //pode ser que não dê para carregar o projeto para salvar
            FileManager.addScreenToProjectAndSave(updatedScreen);
        } catch (RuntimeException e) {
            errorMessageProperty.set(e.getMessage());
        }

        // Atualiza estado
        screenTabs.add(updatedScreen);
        loadScreenAndApplyState(updatedScreen);
    }

    // --- COMANDOS (Ações chamadas pela View) ---

    /**
     * Chamado quando o botão "+" é clicado na barra de abas.
     */
    public void addScreen() {
        // Limpa seleção
        clearSelectionState();

        // 1. Cria nova estrutura de dados
        final var screen = new StateJson_v3();

        // 2. Gera o Canva em memória para pegar defaults
        // Nota: Passamos 'this' (ViewModel) para o Canva, o que é aceitável
        final var canvaGerado = CanvaMapper.fromScreenToCanva(screen, this);

        // 3. Salva
        final var updatedScreen = CanvaMapper.toStateJson(canvaGerado, this);
        FileManager.addScreenToProjectAndSave(updatedScreen);

        // 4. Atualiza a lista observável (A View adicionará a aba visualmente)
        screenTabs.add(updatedScreen);

        // 5. Carrega a nova tela
        loadScreenAndApplyState(updatedScreen);
    }

    /**
     * Corrigido: Persiste o estado da tela atual antes de carregar a próxima.
     */
    public void handleTabClicked(String screenIdToLoad) {
        if (currentScreenId.get() != null && currentScreenId.get().equals(screenIdToLoad)) {
            return;
        }

        // 1. AÇÃO CORRETIVA: Salva o estado do Canva que está sendo DESCARREGADO
        persistActiveScreenStateAndSave();

        // 2. Carrega a próxima tela
        loadScreenById(screenIdToLoad);
    }

    /**
     * Chamado para excluir uma tela.
     */
    public void deleteScreen(String screenId) {
        boolean wasCurrentScreen = currentScreenId.get() != null && currentScreenId.get().equals(screenId);

        // 1. Remove do disco/projeto
        FileManager.deleteScreenFromProject(screenId);

        // 2. Atualiza a lista observável (A View removerá a aba visualmente)
        screenTabs.removeIf(s -> s.screen_id.equals(screenId));

        // 3. Lógica de fallback de navegação
        if (screenTabs.isEmpty()) {
            IO.println("Todas as telas excluídas. Criando padrão.");
            createDefaultScreen(false);
        } else if (wasCurrentScreen) {
            // Carrega a última tela restante
            loadScreenById(screenTabs.getLast().screen_id);
        }
    }

    /**
     * Centraliza a lógica de salvar o estado do canva atualmente ativo no arquivo.
     */
    public void persistActiveScreenStateAndSave() {
        if (activeCanva.get() != null) {
            // 1. Converte o objeto Canva (com as alterações de cor, etc.) em StateJson_v3
            var screen = CanvaMapper.toStateJson(activeCanva.get(), this);

            // 2. Persiste no disco/estrutura global de dados
            FileManager.updateScreen(screen);

            // 3. Opcional: Atualiza a lista observável screenTabs com o novo StateJson
            screenTabs.stream()
                    .filter(s -> s.screen_id.equals(screen.screen_id))
                    .findFirst()
                    .ifPresent(s -> {
                        // Substitui a entrada na lista com o novo objeto StateJson serializado
                        int index = screenTabs.indexOf(s);
                        if (index != -1) {
                            screenTabs.set(index, screen);
                        }
                    });
        }
    }


    /**
     * Salva o projeto atual.
     */
    public void handleSave() {
        if (activeCanva.get() != null) {
            // Notifica para remover erros da UI (via propriedade, se houver binding) ou callback
            // home.leftSide.removeError(); // <-- Removido (View deve observar status)

            final var screen = CanvaMapper.toStateJson(activeCanva.get(), this);
            FileManager.updateScreen(screen);
            showToastProperty.set("Projeto salvo com sucesso!");
        }
    }

    /**
     * Abre um projeto existente (FileChooser).
     */
    public void handleOpenExistingProject() {
        var fc = new FileChooser();
        fc.setTitle("load json project");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("ui.json", "*.json"));

        errorMessageProperty.set(null);
        try {
            var file = fc.showOpenDialog(stage);
            if (file != null) {
                final var text = FileManager.loadDataFromProjectFile(file).name();
                FileManager.saveProjectAndAddToRecents(text, file);

                showToastProperty.set("Project loaded successfully!");
                // Reinicia a cena Home para carregar o novo contexto
                AppScenes.SwapScene(stage, AppScenes.HomeScene(stage));
            }
        } catch (Exception e) {
            errorMessageProperty.set("Erro ao carregar projeto: " + e.getMessage());
        }
    }

    public void exitProject() {
        FileManager.updateCurrentProjectFIleInPrefs(null);
        AppScenes.SwapScene(this.stage, AppScenes.CreateProjectScene(this.stage));
    }

    public void handleBecomeContributor() {
        Thread.ofVirtual().start(() -> {
            try {
                Desktop.getDesktop().browse(new URI("https://buymeacoffee.com/plantfall"));
            } catch (Exception e) {
                System.err.println("Was not possible to go to donation");
            }
        });
    }

    // Delegações simples para janelas auxiliares
    public void handleClickMenuSettings() {
        new SettingsScene().show();
    }

    public void handleClickDataTable() {
        AllWindows.showWindowForDataTable();
    }


    // --- LÓGICA INTERNA DE CARREGAMENTO ---

    private void loadScreenById(String screenId) {
        clearSelectionState();

        // Busca dados frescos do disco (Otimização que fizemos antes)
        final var projectData = FileManager.getProjectData();
        final var screenOptional = projectData.screens().stream()
                .filter(s -> s.screen_id.equals(screenId))
                .findFirst();

        if (screenOptional.isPresent()) {
            loadScreenAndApplyState(screenOptional.get());
        } else {
            IO.println("Erro: Tela ID " + screenId + " não encontrada.");
        }
    }

    private void loadScreenAndApplyState(StateJson_v3 screen) {
        IO.println("Carregando tela: " + screen.name);

        // Atualiza ID atual
        this.currentScreenId.set(screen.screen_id);

        // Gera o componente visual (Conteúdo)
        final var newCanva = CanvaMapper.fromScreenToCanva(screen, this);

        // Define o Canva Ativo -> A View vai observar isso e atualizar o editor.setContent()
        this.activeCanva.set(newCanva);

        // Seleciona o Canva por padrão
        selectNode(newCanva);

        // Atualiza caminho na UI
        updateUiPathProperty();
    }

    private void updateUiPathProperty() {
        try {
            final var prefsData = FileManager.loadDataInPrefs();
            final var file = new File(prefsData.last_project_saved_path());
            uiPathProperty.set(file.getAbsolutePath());
        } catch (Exception e) {
            // ignore
        }
    }

    private void clearSelectionState() {
        dataMap.clear();
        headerSelected.set(null);
        nodeSelected.set(null);
        refreshSubItems();
    }

    // --- LÓGICA DE COMPONENTES E SELEÇÃO ---

    public void addComponent(String type, ViewComponent<?> component) {
        final var currentCanva = activeCanva.get(); // Pega da propriedade!
        if (currentCanva == null || type == null || type.isBlank()) return;

        var node = ComponentsFactory.createNew(type, this, currentCanva);
        var typeNormalized = type.trim().toLowerCase();

        if (node != null) {
            addItemOnDataMap(typeNormalized, node);

            // Seleciona o novo nó
            highlightComponent(node);

            // Adiciona visualmente ao Canva atual
            currentCanva.addElementDragable(node, true);

            refreshSubItems();
        }
    }


    private void highlightComponent(ViewComponent<?> component) {
        var typeNormalized = component.getData().type().trim().toLowerCase();
        // Seleciona o novo nó
        final var newSelection = new SelectedComponent(typeNormalized, component.getNode());
        nodeSelected.set(newSelection);
        headerSelected.set(typeNormalized);
    }

    //here for example is when i only want to select the node for editing inside custom component
    public void selectNodePartially(ViewComponent<?> node) {
        var comp = (ComponentData) node.getData();
        SelectedComponent newSelection = new SelectedComponent(comp.type(), node.getNode());
        nodeSelected.set(newSelection);
        System.out.println("Selecionado: " + node + " (Type: " + comp.type() + ")");
    }

    public void removeNode(String nodeId) {
        final var currentCanva = activeCanva.get();
        if (currentCanva == null) return;

        ObservableList<Node> canvaChildren = currentCanva.getChildren();
        boolean removedFromCanva = canvaChildren.removeIf(node -> nodeId.equals(node.getId()));
        boolean removedFromDataMap = removeItemByIdentification(nodeId);

        Node currentlySelectedNode = nodeSelected.get() != null ? nodeSelected.get().node() : null;
        if (currentlySelectedNode != null && nodeId.equals(currentlySelectedNode.getId())) {
            nodeSelected.set(null);
            headerSelected.set(null);
        }

        if (removedFromCanva || removedFromDataMap) {
            refreshSubItems();
        }
    }

    //todo considerar a remoção desse método, pois não está sendo usado em lugar nenhum!
    public void removeComponentFromAllPlaces(ViewComponent<?> componentWrapper, CanvaComponentV2 canvaComponent) {
        // Método auxiliar usado por componentes internos
        canvaComponent.getChildren().remove(componentWrapper.getNode());
        removeComponentFromDataMap(componentWrapper);
        refreshSubItems();
    }

    // --- MÉTODOS AUXILIARES DE DADOS ---

    /**
     * Adiciona um item no mapa de dados, verificando se o ID do componente já existe
     * para prevenir duplicação de estado.
     */
    public void addItemOnDataMap(String type, ViewComponent<?> nodeWrapper) {
        String newId = nodeWrapper.getNode().getId();

        // 1. Verifica se o ID já existe em qualquer lista do dataMap (prevenção de duplicação)
        boolean idExists = dataMap.values().stream()
                .flatMap(List::stream) // Achata todas as listas de componentes em um único Stream
                .anyMatch(existingNode -> newId.equals(existingNode.getNode().getId()));

        if (idExists) {
            // Se o ID já existe, ignora a adição. Isso é comum e esperado durante o
            // processo de carregamento de telas que já estão na memória.
            System.err.println("Warning: Component with ID " + newId +
                    " already exists in dataMap. Skipping addition.");
            return;
        }

        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList()).add(nodeWrapper);
    }

    private boolean removeItemByIdentification(String identification) {
        for (var itemsList : dataMap.values()) {
            var itemToRemove = itemsList.stream()
                    .filter(item -> identification.equals(item.getNode().getId()))
                    .findFirst()
                    .orElse(null);

            if (itemToRemove != null) {
                itemsList.remove(itemToRemove);
                return true;
            }
        }
        return false;
    }

    public void removeComponentFromDataMap(ViewComponent<?> componentWrapper) {
        var data = (ComponentData) componentWrapper.getData();
        var list = dataMap.get(data.type());
        if (list != null) {
            var currentNodeId = componentWrapper.getNode().getId();
            list.stream().filter(it -> it.getNode().getId().equals(currentNodeId))
                    .findFirst().ifPresent(it -> it.delete());
        }
    }

    public void selectNode(Node node) {
        if (node == null) {
            nodeSelected.set(null);
            headerSelected.set(null);
        } else {
            String type = getNodeType(node);
            if (type != null) {
                SelectedComponent newSelection = new SelectedComponent(type, node);
                nodeSelected.set(newSelection);
                headerSelected.set(type);
                System.out.println("Selecionado: " + node + " (Type: " + type + ")");
            } else {
                // Caso especial para o próprio Canva ou nós não mapeados
                nodeSelected.set(null);
                headerSelected.set(null);
            }
        }
        refreshSubItems();
    }

    public String getNodeType(Node node) {
        if (node == null) return null;
        String nodeId = node.getId();
        for (var entry : dataMap.entrySet()) {
            if (entry.getValue().stream().anyMatch(n -> node.getId().equals(n.getNode().getId()))) {
                return entry.getKey();
            }
        }
        return null; // Pode retornar "canva" se for o background, dependendo da lógica desejada
    }

    public Optional<ViewComponent<?>> SearchNodeById(String nodeId) {
        return dataMap.values().stream()
                .flatMap(List::stream)
                .filter(node -> node.getNode().getId().equals(nodeId))
                .findFirst();
    }

    public List<ViewComponent<?>> getItemsByType(String type) {
        return dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .stream()
                .filter(component -> !component.isDeleted())
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean currentNodeIsSelected(String nodeId) {
        final var selected = nodeSelected.get();
        return selected != null && selected.node() != null && selected.node().getId().equals(nodeId);
    }

    public void refreshSubItems() {
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }

    public static Node SearchNodeByIdInMainCanva(String nodeId, ObservableList<Node> canvaChildren) {
        return canvaChildren.stream()
                .filter(n -> nodeId.equals(n.getId()))
                .findFirst()
                .orElse(null);
    }

    public ViewComponent<?> findNodeById(String id) {
        if (id == null || id.isEmpty()) return null;
        for (var viewList : dataMap.values()) {
            for (final var contract : viewList) {
                if (id.equals(contract.getData().identification())) {
                    return contract;
                }
            }
        }
        return null;
    }

    // --- RECORDS ---
    public record SelectedComponent(String type, Node node) {
    }
}