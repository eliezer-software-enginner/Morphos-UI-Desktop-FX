package my_app.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.MenuDataEditorComponent;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.data.MenuComponentData.MenuItemData;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;

import java.util.ArrayList;

public class MenuComponent extends HBox implements ViewContractv2<MenuComponentData> {

    private final HomeViewModel viewModel;
    private final CanvaComponentV2 canva;
    private boolean isDeleted = false;

    // Estado Observável Central: a lista de itens do menu
    private ObservableList<MenuItemData> itemsState = FXCollections.observableArrayList(new MenuItemData());

    private final TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    public MenuComponent(HomeViewModel viewModel, CanvaComponentV2 canva) {
        setSpacing(10); // Espaçamento entre os itens do menu
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: #3f51b5;"); // Cor de debug/padrão

        this.setId(String.valueOf(System.currentTimeMillis()));

        this.viewModel = viewModel;
        this.canva = canva;

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
                getChildren().add(Typography.caption(item.name()));
                continue;
            }

            try {
                // 1. Busca o template na ViewModel
                final var existingNodeWrapper = searchNode(childId);

                // 2. Clona o template (método cloneExistingNode é reutilizado de ColumnComponent)
                // Usamos -1 para indicar que não é uma repetição de dados, apenas uma clonagem
                final var newNodeWrapper = (ViewContractv2<ComponentData>) cloneExistingNode((ViewContractv2<ComponentData>) existingNodeWrapper, item);

                var node = newNodeWrapper.getCurrentNode();

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
                System.err.println("Aviso: Template de item de menu '" + childId + "' não encontrado: " + e.getMessage());
                getChildren().add(Typography.error("Erro: Template ausente."));
            }
        }
    }

    // Adaptado do ColumnComponent para suportar o MenuComponentData
    private ViewContractv2<? extends ComponentData> cloneExistingNode(ViewContractv2<ComponentData> existingNode, MenuItemData itemData) {
        var originalData = existingNode.getData();
        var type = originalData.type();

        // Nota: A lógica de substituição de variáveis foi removida,
        // pois MenuComponent usa o nome do item de menu como texto,
        // mas foi mantida a estrutura para ser coerente com ColumnComponent.

        if (type.equalsIgnoreCase(englishBase.button())) {
            var newNodeWrapper = new ButtonComponent(this.viewModel, canva);
            newNodeWrapper.applyData((ButtonComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.image())) {
            var newNodeWrapper = new ImageComponentv2(this.viewModel, canva);
            newNodeWrapper.applyData((ImageComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            var newNodeWrapper = new InputComponent(this.viewModel, canva);
            newNodeWrapper.applyData((InputComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.text())) {
            var newNodeWrapper = new TextComponent(this.viewModel, canva);
            newNodeWrapper.applyData((TextComponentData) originalData);
            return newNodeWrapper;
        } else {
            var newNodeWrapper = new CustomComponent(this.viewModel, canva);
            newNodeWrapper.applyData((CustomComponentData) originalData);
            return newNodeWrapper;
        }
    }

    private ViewContractv2<?> searchNode(String componentId) {
        var optionalNode = this.viewModel.SearchNodeById(componentId);
        return optionalNode.orElseThrow(() -> new IllegalStateException("Template component with ID " + componentId + " not found in ViewModel."));
    }

    // -------------------------------------------------------------------
    // Implementação do ViewContractv2
    // -------------------------------------------------------------------

    @Override
    public Node getCurrentNode() {
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

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Typography.subtitle("Menu Items"),
                new MenuDataEditorComponent(this.itemsState, this.viewModel, this), // Novo editor de lista
                new ButtonRemoverComponent(this, this.viewModel)
        );
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow("Centralizar verticalmente", this, canva)
        );
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {
        // Nada específico por enquanto
        father.getChildren().setAll(Typography.body("Configurações adicionais de menu..."));
    }

    @Override
    public MenuComponentData getData() {
        var location = Commons.NodeInCanva(this);

        // Retorna o MenuComponentData com a lista de itens atual
        return new MenuComponentData(
                "menu component",
                this.getId(),
                new ArrayList<>(this.itemsState), // Copia defensiva da lista
                (int) getLayoutX(),
                (int) getLayoutY(),
                location.fatherId(),
                isDeleted
        );
    }
}