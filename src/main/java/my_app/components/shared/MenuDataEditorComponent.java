package my_app.components.shared;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.MenuComponent;
import my_app.data.MenuComponentData.MenuItemData;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.Typography;

public class MenuDataEditorComponent extends VBox {

    private final ObservableList<MenuItemData> items;
    private final HomeViewModel viewModel;
    private final MenuComponent menuComponent;

    private final VBox itemsContainer = new VBox(5);
    private final Button addButton = Components.ButtonPrimary("+ Add Item");

    public MenuDataEditorComponent(
            ObservableList<MenuItemData> items,
            HomeViewModel viewModel,
            MenuComponent menuComponent) {

        this.items = items;
        this.viewModel = viewModel;
        this.menuComponent = menuComponent;

        setSpacing(10);
        setPadding(new Insets(10, 0, 10, 0));

        // --- MUDANÇA CRUCIAL: O LISTENER GERAL FOI REMOVIDO ---
        // A reconstrução da UI do editor agora é explicitamente chamada nos botões.

        addButton.setOnAction(_ -> {
            this.items.add(new MenuItemData());
            rebuildUI(); // Chama rebuildUI explicitamente após a adição
        });

        rebuildUI();
        getChildren().addAll(itemsContainer, addButton);
    }

    /**
     * Limpa e reconstrói a UI para refletir o estado atual da lista 'items'.
     */
    private void rebuildUI() {
        itemsContainer.getChildren().clear();

        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            MenuItemData itemData = items.get(i);

            VBox itemRow = createItemEditor(itemData, index);
            itemsContainer.getChildren().add(itemRow);
        }

        menuComponent.renderMenu();
    }

    private VBox createItemEditor(MenuItemData itemData, int index) {
        VBox itemBox = new VBox(5);
        itemBox.setPadding(new Insets(5));
        itemBox.setStyle("-fx-border-color: #AAAAAA; -fx-border-width: 1; -fx-background-color: #F0F0F0;");

        Label header = Typography.subtitle("Item " + (index + 1) + ":");

        // --- Nome do Item ---
        TextField nameField = new TextField(itemData.name());
        nameField.setPromptText("Nome do Item");

        // CORREÇÃO: Atualiza o modelo APENAS quando o campo perde o foco
        nameField.focusedProperty().addListener((obs, oldVal, hasFocus) -> {
            if (oldVal && !hasFocus) { // Se estava focado e agora perdeu o foco
                updateItemData(index, itemData.functionName(), itemData.childId(), nameField.getText());
            }
        });
        // Remove textProperty().addListener

        // --- Nome da Função (Callback) ---
        TextField functionField = new TextField(itemData.functionName());
        functionField.setPromptText("Nome da Função (e.g., handleLogin)");

        // CORREÇÃO: Atualiza o modelo APENAS quando o campo perde o foco
        functionField.focusedProperty().addListener((obs, oldVal, hasFocus) -> {
            if (oldVal && !hasFocus) { // Se estava focado e agora perdeu o foco
                updateItemData(index, functionField.getText(), itemData.childId(), itemData.name());
            }
        });
        // Remove textProperty().addListener

        // --- Template (Child) ID ---
        Label templateLabel = Typography.caption("Template (ID do Componente):");
        HBox templateRow = createChildIdSelector(itemData, index);

        // --- Botão de Remover ---
        Button removeButton = Components.ButtonPrimary("Remover");
        removeButton.setOnAction(_ -> {
            items.remove(index);
        });

        itemBox.getChildren().addAll(
                header, nameField, functionField, templateLabel, templateRow, removeButton
        );
        return itemBox;
    }

    private HBox createChildIdSelector(MenuItemData itemData, int index) {
        var selector = new ChildIdSelectorComponent(
                itemData.childId(),
                this.viewModel,
                // O ComboBox não causa perda de foco em si, então pode atualizar diretamente
                newId -> updateItemData(index, itemData.functionName(), newId, itemData.name())
        );
        selector.config();

        return selector;
    }

    /**
     * Atualiza o MenuItemData na lista e força a re-renderização do menu visual na tela principal.
     */
    private void updateItemData(int index, String functionName, String childId, String name) {
        // 1. Cria o novo registro de dados com o valor atualizado
        MenuItemData newItem = new MenuItemData(name, functionName, childId);

        // 2. Atualiza a ObservableList. Isso dispara um evento 'update' que é ignorado pelo ListChangeListener
        this.items.set(index, newItem);

        // 3. Força apenas a atualização da visualização do menu na tela principal.
        menuComponent.renderMenu();
    }
}