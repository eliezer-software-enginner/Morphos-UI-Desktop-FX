package my_app.contexts;

import java.io.File;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.stage.Stage;
import my_app.components.CustomComponent;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.columnComponent.ColumnComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.inputComponents.InputComponent;
import my_app.data.ButtonComponentData;
import my_app.data.ColumnComponentData;
import my_app.data.Commons;
import my_app.data.CustomComponentData;
import my_app.data.ImageComponentData;
import my_app.data.InputComponentData;
import my_app.data.StateJson_v2;
import my_app.data.TextComponentData;
import my_app.scenes.ShowComponentScene.ShowComponentScene;
import my_app.screens.Home.Home;

public class ComponentsContext {

    public record SelectedComponent(String type, Node node) {
    }

    public SimpleObjectProperty<SelectedComponent> nodeSelected = new SimpleObjectProperty<>();

    public ObservableMap<String, ObservableList<Node>> dataMap = FXCollections
            .observableHashMap();

    public SimpleStringProperty headerSelected = new SimpleStringProperty(null);

    public SimpleBooleanProperty leftItemsStateRefreshed = new SimpleBooleanProperty(false);

    private CanvaComponent mainCanvaComponent;

    public boolean currentNodeIsSelected(String nodeId) {

        SelectedComponent selected = nodeSelected.get();

        // 1. Verifica se algo está selecionado (selected != null)
        // 2. Verifica se o Node dentro do SelectedComponent não é nulo (selected.node()
        // != null)
        // 3. Compara o ID do Node selecionado com o nodeId fornecido
        return selected != null && selected.node() != null && selected.node().getId().equals(nodeId);
    }

    public void loadJsonState(File file, CanvaComponent canvaComponent, Stage stage) {
        canvaComponent.getChildren().clear();

        mainCanvaComponent = canvaComponent;

        String idOfComponentSelected = null;
        nodeSelected.set(null);
        headerSelected.set(null);
        dataMap.clear();

        ObjectMapper om = new ObjectMapper();

        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        try {
            var state = om.readValue(file, StateJson_v2.class);
            mainCanvaComponent.applyData(state.canva);

            if (state.id_of_component_selected != null) {
                idOfComponentSelected = state.id_of_component_selected;
            }

            for (TextComponentData data : state.text_components) {
                TextComponent comp = new TextComponent(data.text(), this);
                comp.applyData(data);
                // nodes.add(comp);

                // subItemsContext.addItem("text", data.identification());
                addItem("text", comp);

                if (data.in_canva()) {
                    mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura os botões
            for (ButtonComponentData data : state.button_components) {
                ButtonComponent comp = new ButtonComponent(this);

                comp.applyData(data);
                // nodes.add(comp);
                // subItemsContext.addItem("button", data.identification());
                addItem("button", comp);

                if (data.in_canva()) {
                    mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura as imagens
            for (ImageComponentData data : state.image_components) {
                ImageComponent comp = new ImageComponent(this);
                comp.stage = stage;

                comp.applyData(data);
                // nodes.add(comp);

                addItem("image", comp);
                if (data.in_canva()) {
                    mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura inputs
            for (InputComponentData data : state.input_components) {
                InputComponent comp = new InputComponent("", this);

                comp.applyData(data);
                // nodes.add(comp);

                addItem("input", comp);

                if (data.in_canva()) {
                    mainCanvaComponent.addElementDragable(comp, false);

                }
            }

            for (CustomComponentData data : state.custom_components) {
                var comp = new CustomComponent(this);

                comp.applyData(data);
                // nodes.add(comp);

                addItem("component", comp);

                if (data.in_canva) {
                    mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            for (ColumnComponentData data : state.column_components) {
                var comp = new ColumnComponent(this);

                comp.applyData(data);
                // nodes.add(comp);

                addItem("column items", comp);

                if (data.in_canva()) {
                    mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            SearchNodeById(idOfComponentSelected).ifPresent(node -> selectNode(node));

            leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());

            headerSelected.set(state.type_of_component_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addItem(String type, Node node) {
        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .add(node);
    }

    public String getNodeType(Node targetNode) {
        if (targetNode == null) {
            return null;
        }
        String nodeId = targetNode.getId();

        // Itera sobre o mapa para encontrar a chave (tipo) que contém o Node.
        for (var entry : dataMap.entrySet()) {
            if (entry.getValue().stream().anyMatch(n -> n.getId().equals(nodeId))) {
                return entry.getKey();
            }
        }
        return null;
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

    public ObservableList<Node> getItemsByType(String type) {
        return dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList());
    }

    public void addComponent(String type, Home home) {

        if (type == null || type.isBlank()) {
            return;
        }

        Node node = null;
        var content = "Im new here";

        var typeNormalized = type.trim().toLowerCase();

        if (type.equalsIgnoreCase("Button")) {
            node = new ButtonComponent(content, this);
        } else if (type.equalsIgnoreCase("Input")) {
            node = new InputComponent(content, this);

        } else if (type.equalsIgnoreCase("Text")) {
            node = new TextComponent(content, this);

        } else if (type.equalsIgnoreCase("Image")) {
            node = new ImageComponent(
                    ComponentsContext.class.getResource("/assets/images/mago.jpg").toExternalForm(),
                    this);

        } else if (type.equalsIgnoreCase("Component")) {
            new ShowComponentScene(home.canva, this).stage.show();
            return;
        } else if (type.equalsIgnoreCase("Column items")) {
            node = new ColumnComponent(this);
        }

        if (node != null) {

            // 1. Adiciona o nó ao dataMap
            addItem(typeNormalized, node);

            // 2. CRIA E ATUALIZA o nodeSelected com o novo objeto SelectedComponent
            // ESTA É A LINHA CORRIGIDA

            SelectedComponent newSelection = new SelectedComponent(typeNormalized, node);
            nodeSelected.set(newSelection);

            // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
            headerSelected.set(typeNormalized);

            // 4. Adiciona o nó à tela (Canva)
            home.canva.addElementDragable(node, true);

            // 5. Notifica a UI lateral para atualizar a lista
            refreshSubItems();
        }
    }

    public void addCustomComponent(Node customComponent, CanvaComponent mainCanva) {
        // mainCanvaComponent = mainCanva;
        // nodes.add(customComponent); // Adiciona à lista mestre
        System.out.println("(addCustomComponent) -> mainCanva in custom component: " + mainCanva);
        addItem("component", customComponent);

        SelectedComponent newSelection = new SelectedComponent("component", customComponent);
        nodeSelected.set(newSelection);

        // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
        headerSelected.set("component");

        // 4. Adiciona o nó à tela (Canva)
        mainCanva.addElementDragable(customComponent, true);

        // 5. Notifica a UI lateral para atualizar a lista
        refreshSubItems();
    }

    public Optional<Node> SearchNodeById(String nodeId) {
        return dataMap.values()
                .stream()
                .flatMap(list -> list.stream()) // Achata todas as listas em um único stream
                .filter(node -> node.getId().equals(nodeId))
                .findFirst();
    }

    public static Node SearchNodeByIdInMainCanva(String nodeId, ObservableList<Node> canvaChildren) {
        // lookin for custom component in main canva
        var target = canvaChildren.stream()
                .filter(n -> nodeId.equals(n.getId()))
                .findFirst()
                .orElse(null);

        return target;
    }

    // public static void SelectNode(Node node) {
    // nodeSelected.set(node);
    // refreshSubItems();
    // System.out.println("Selecionado: " + node);
    // }

    public void saveStateInJsonFile_v2(File file, CanvaComponent mainCanvaComponent) {
        try {
            // Gera o StateJson_v2 a partir dos Nodes e do CanvaComponent
            StateJson_v2 data = createStateData(mainCanvaComponent);

            // Usa Commons para escrever os dados no disco
            Commons.WriteJsonInDisc(file, data);

            System.out.println("Estado salvo com sucesso no arquivo: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Erro ao salvar o estado no arquivo JSON.");
            e.printStackTrace();
        }
    }

    private StateJson_v2 createStateData(CanvaComponent canva) {
        StateJson_v2 jsonTarget = new StateJson_v2();
        jsonTarget.id_of_component_selected = nodeSelected.get() == null ? null
                : nodeSelected.getValue().node.getId();

        jsonTarget.type_of_component_selected = headerSelected.get();

        // 1. Salva as propriedades do CanvaComponent
        jsonTarget.canva = canva.getData();

        // 2. Itera sobre TODOS os nós (nodes) no dataMap
        // Para cada lista de nós (os VALUES do dataMap)...
        for (ObservableList<Node> nodesList : dataMap.values()) {
            // ...itera sobre cada Node dentro dessa lista.
            for (Node node : nodesList) {
                // A LÓGICA DE SERIALIZAÇÃO PERMANECE A MESMA

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

    public void refreshSubItems() {
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }

    public void removeNode(String nodeId) {
        System.out.println("mainCanva: " + mainCanvaComponent);
        // 1. Tenta remover o Node do mainCanva (UI)
        ObservableList<Node> canvaChildren = mainCanvaComponent.getChildren();
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

    public void removeCustomComponent(String nodeId, CanvaComponent maiCanvaComponent) {
        System.out.println("mainCanva: " + mainCanvaComponent);
        // 1. Tenta remover o Node do mainCanva (UI)
        ObservableList<Node> canvaChildren = mainCanvaComponent.getChildren();
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
        for (ObservableList<Node> itemsList : dataMap.values()) {

            // Procura o item a ser removido (a forma mais garantida para ObservableList)
            Node itemToRemove = null;
            for (Node item : itemsList) {
                if (identification.equals(item.getId())) {
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
}
