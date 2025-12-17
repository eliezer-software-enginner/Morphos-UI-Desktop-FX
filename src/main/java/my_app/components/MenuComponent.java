package my_app.components;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.TranslationContext;
import my_app.data.ButtonComponentData;
import my_app.data.Commons;
import my_app.data.ComponentData;
import my_app.data.CustomComponentData;
import my_app.data.ImageComponentData;
import my_app.data.InputComponentData;
import my_app.data.MenuComponentData;
import my_app.data.MenuComponentData.MenuItemData;
import my_app.data.TextComponentData;
import my_app.data.contracts.ViewComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.Typography;

public final class MenuComponent extends HBox implements ViewComponent<MenuComponentData> {

    private final HomeViewModel viewModel;
    private boolean isDeleted = false;

    // Estado Observável Central: a lista de itens do menu
    public ObservableList<MenuItemData> itemsState = FXCollections.observableArrayList();

    // Referência ao editor para sincronizar antes de salvar
    private my_app.components.shared.MenuDataEditorComponent editorComponent;

    private final TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    public MenuComponent(HomeViewModel viewModel) {
        setSpacing(10); // Espaçamento entre os itens do menu
        setAlignment(Pos.CENTER_LEFT);
        // setPadding(new Insets(5));
        // setStyle("-fx-background-color: #3f51b5;"); // Cor de debug/padrão

        this.setId(String.valueOf(System.currentTimeMillis()));

        this.viewModel = viewModel;

        // Adiciona um item padrão apenas se a lista estiver vazia (novo componente)
        if (itemsState.isEmpty()) {
            itemsState.add(new MenuItemData());
        }

        // Renderiza o item padrão inicial
        renderMenu();
    }

    @Override
    public void applyData(MenuComponentData data) {
        this.setLayoutX(data.x());
        this.setLayoutY(data.y());
        this.setId(data.identification());
        this.isDeleted = data.isDeleted();

        // Aplica a lista de itens carregada
        this.itemsState.setAll(data.items());

        renderMenu();
    }

    // -------------------------------------------------------------------
    // Lógica Centralizada para Renderizar o Menu
    // -------------------------------------------------------------------
    public void renderMenu() {
        getChildren().clear();

        for (MenuItemData item : itemsState) {
            String childId = item.childId();

            if (childId == null || childId.equals("None") || childId.isEmpty()) {
                // Se não houver template, usa um Label simples
                IO.println("MenuComponent->renderMenu(): childId is null");
                Label menuText = Typography.caption(item.name());
                menuText.setStyle("-fx-fill:black;-fx-text-fill:black;");
                getChildren().add(menuText);
                continue;
            }

            try {
                // 1. Busca o template na ViewModel
                final var existingNodeWrapper = searchNode(childId);

                // 2. Clona o template (método cloneExistingNode é reutilizado de
                // ColumnComponent)
                // Usamos -1 para indicar que não é uma repetição de dados, apenas uma clonagem
                final var newNodeWrapper = (ViewComponent<ComponentData>) cloneExistingNode(
                        (ViewComponent<ComponentData>) existingNodeWrapper, item);

                var node = newNodeWrapper.getNode();

                // 3. Aplica o nome do item de menu ao texto do componente, se possível
                if (node instanceof ButtonComponent btn) {
                    btn.setText(item.name());
                } else if (node instanceof TextComponent text) {
                    text.setText(item.name());
                }

                // 4. Configura propriedades de UI
                node.setMouseTransparent(true); // Impede interação

                getChildren().add(node);
            } catch (IllegalStateException e) {
                System.err
                        .println("Aviso: Template de item de menu '" + childId + "' não encontrado: " + e.getMessage());
                getChildren().add(Typography.error("Erro: Template ausente."));
            }
        }
    }

    // Adaptado do ColumnComponent para suportar o MenuComponentData
    private ViewComponent<? extends ComponentData> cloneExistingNode(ViewComponent<ComponentData> existingNode,
            MenuItemData itemData) {
        var originalData = existingNode.getData();
        var type = originalData.type();

        // Nota: A lógica de substituição de variáveis foi removida,
        // pois MenuComponent usa o nome do item de menu como texto,
        // mas foi mantida a estrutura para ser coerente com ColumnComponent.

        if (type.equalsIgnoreCase(englishBase.button())) {
            var newNodeWrapper = new ButtonComponent();
            newNodeWrapper.applyData((ButtonComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.image())) {
            var newNodeWrapper = new ImageComponent();
            newNodeWrapper.applyData((ImageComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            var newNodeWrapper = new InputComponent();
            newNodeWrapper.applyData((InputComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.text())) {
            var newNodeWrapper = new TextComponent();
            newNodeWrapper.applyData((TextComponentData) originalData);
            return newNodeWrapper;
        } else {
            var newNodeWrapper = new CustomComponent(this.viewModel);
            newNodeWrapper.applyData((CustomComponentData) originalData);
            return newNodeWrapper;
        }
    }

    private ViewComponent<?> searchNode(String componentId) {
        var optionalNode = this.viewModel.SearchNodeById(componentId);
        return optionalNode.orElseThrow(() -> new IllegalStateException(
                "Template component with ID " + componentId + " not found in ViewModel."));
    }

    // -------------------------------------------------------------------
    // Implementação do ViewContractv2
    // -------------------------------------------------------------------

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

    /**
     * Define a referência ao editor para permitir sincronização antes de salvar.
     */
    public void setEditorComponent(my_app.components.shared.MenuDataEditorComponent editor) {
        this.editorComponent = editor;
    }

    /**
     * Sincroniza os valores do editor antes de serializar.
     * Deve ser chamado antes de getData() para garantir que todos os valores
     * digitados sejam salvos.
     */
    public void syncEditorBeforeSave() {
        if (editorComponent != null) {
            editorComponent.updateAllItemsFromFields();
        }
    }

    @Override
    public MenuComponentData getData() {
        var location = Commons.NodeInCanva(this);

        // Sincroniza o editor antes de pegar os dados
        syncEditorBeforeSave();

        // Retorna o MenuComponentData com a lista de itens atual
        return new MenuComponentData(
                "menu component",
                this.getId(),
                new ArrayList<>(this.itemsState), // Copia defensiva da lista
                (int) getLayoutX(),
                (int) getLayoutY(),
                location.fatherId(),
                isDeleted);
    }
}