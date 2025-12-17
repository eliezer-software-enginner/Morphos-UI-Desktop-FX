package my_app.components.shared;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private final ScrollPane scrollPane = new ScrollPane();
    private final Button addButton = Components.ButtonPrimary("+ Add Item");

    // Lista para armazenar referências aos campos de texto
    private final List<ItemFieldsHolder> itemFields = new ArrayList<>();

    public MenuDataEditorComponent(
            ObservableList<MenuItemData> items,
            HomeViewModel viewModel,
            MenuComponent menuComponent) {

        this.items = items;
        this.viewModel = viewModel;
        this.menuComponent = menuComponent;

        setSpacing(10);
        setPadding(new Insets(10, 0, 10, 0));

        // Configuração do ScrollPane
        scrollPane.setContent(itemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefHeight(400); // Altura preferencial
        scrollPane.setMaxHeight(600); // Altura máxima
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Padding do container de itens
        itemsContainer.setPadding(new Insets(5));

        // Quando clicar em "Add Item", captura os valores dos campos e atualiza a UI
        addButton.setOnAction(_ -> {
            // Primeiro, atualiza todos os itens existentes com os valores dos campos
            updateAllItemsFromFields();

            // Adiciona um novo item vazio
            this.items.add(new MenuItemData());

            // Reconstrói a UI
            rebuildUI();
        });

        rebuildUI();
        getChildren().addAll(scrollPane, addButton);
    }

    /**
     * Atualiza todos os itens da lista com os valores atuais dos campos de texto.
     * Este método deve ser chamado antes de salvar para garantir que todos os
     * valores
     * digitados sejam persistidos.
     */
    public void updateAllItemsFromFields() {
        for (int i = 0; i < itemFields.size() && i < items.size(); i++) {
            ItemFieldsHolder holder = itemFields.get(i);
            String name = holder.nameField.getText();
            String functionName = holder.functionField.getText();
            String childId = holder.currentChildId;

            MenuItemData updatedItem = new MenuItemData(name, functionName, childId);
            items.set(i, updatedItem);
        }

        // Atualiza a visualização do menu
        menuComponent.renderMenu();
    }

    /**
     * Limpa e reconstrói a UI para refletir o estado atual da lista 'items'.
     */
    private void rebuildUI() {
        itemsContainer.getChildren().clear();
        itemFields.clear();

        for (int i = 0; i < items.size(); i++) {
            MenuItemData itemData = items.get(i);

            VBox itemRow = createItemEditor(itemData, i);
            itemsContainer.getChildren().add(itemRow);
        }

        // Atualiza a visualização do menu após reconstruir a UI
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

        // --- Nome da Função (Callback) ---
        TextField functionField = new TextField(itemData.functionName());
        functionField.setPromptText("Nome da Função (e.g., handleLogin)");

        // --- Template (Child) ID ---
        Label templateLabel = Typography.caption("Template (ID do Componente):");

        // Cria um holder para armazenar as referências dos campos
        ItemFieldsHolder holder = new ItemFieldsHolder(nameField, functionField, itemData.childId());
        itemFields.add(holder);

        HBox templateRow = createChildIdSelector(holder, index);

        // --- Botão de Remover ---
        Button removeButton = Components.ButtonPrimary("Remover");
        removeButton.setOnAction(_ -> {
            // Atualiza todos os campos antes de remover
            updateAllItemsFromFields();
            items.remove(index);

            // Se a lista ficou vazia, remove o MenuComponent do dataMap
            if (items.isEmpty()) {
                viewModel.removeComponentFromDataMap(menuComponent);
                // Limpa a seleção para esconder o inspector no RightSide
                viewModel.selectNode(null);
            }

            rebuildUI();
        });

        itemBox.getChildren().addAll(
                header, nameField, functionField, templateLabel, templateRow, removeButton);
        return itemBox;
    }

    private HBox createChildIdSelector(ItemFieldsHolder holder, int index) {
        var selector = new ChildIdSelectorComponent(
                holder.currentChildId,
                this.viewModel,
                // Atualiza apenas o childId no holder quando o ComboBox mudar
                newId -> holder.currentChildId = newId);
        selector.config();

        return selector;
    }

    /**
     * Classe auxiliar para armazenar referências aos campos de um item.
     */
    private static class ItemFieldsHolder {
        final TextField nameField;
        final TextField functionField;
        String currentChildId;

        ItemFieldsHolder(TextField nameField, TextField functionField, String childId) {
            this.nameField = nameField;
            this.functionField = functionField;
            this.currentChildId = childId;
        }
    }
}