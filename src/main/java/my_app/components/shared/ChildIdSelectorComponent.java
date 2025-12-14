// -------------------------------------------------------------------
// Componente Auxiliar para Seleção de Template (Baseado em ChildHandlerComponent)
// -------------------------------------------------------------------
// Você precisará criar uma versão deste que receba um callback em vez de um ColumnComponent.

package my_app.components.shared;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import my_app.screens.Home.HomeViewModel;

import java.util.HashMap;

public class ChildIdSelectorComponent extends HBox {

    private final ComboBox<String> combo = new ComboBox<>();
    private final HashMap<String, String> displayIdMap = new HashMap<>();

    // Callback para notificar a alteração do ID
    @FunctionalInterface
    public interface IdUpdateCallback {
        void update(String newId);
    }

    public ChildIdSelectorComponent(
            String currentId,
            HomeViewModel viewModel,
            IdUpdateCallback callback) {

        // 1. Inicializa o mapa com o valor padrão
        displayIdMap.put("None", "None");
        combo.getItems().add("None");

        // 2. Popula a ComboBox e o mapa com IDs válidos
        for (var entry : viewModel.dataMap.entrySet()) {
            String componentType = entry.getKey();

            // Filtro: Menus não podem ser filhos de menus, colunas não podem ser filhos de colunas
            if (componentType.equals("column items") || componentType.equals("menu component")) {
                continue;
            }

            for (var nodeWrapper : entry.getValue()) {
                String id = nodeWrapper.getNode().getId();

                // CRIAÇÃO DO NOME AMIGÁVEL: "Tipo - ID"
                String friendlyName = componentType + " - " + id;
                displayIdMap.put(friendlyName, id);
                if (!combo.getItems().contains(friendlyName)) {
                    combo.getItems().add(friendlyName);
                }
            }
        }

        // 3. Define o valor inicial (tenta encontrar o nome amigável para o ID atual)
        String currentFriendlyName = "None";
        if (!currentId.equals("None")) {
            currentFriendlyName = displayIdMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(currentId))
                    .map(java.util.Map.Entry::getKey)
                    .findFirst()
                    .orElse(currentId);

            if (!combo.getItems().contains(currentFriendlyName)) {
                combo.getItems().add(currentFriendlyName);
            }
        }
        combo.setValue(currentFriendlyName);

        // 4. Listener de alteração
        combo.valueProperty().addListener((obs, oldFriendly, newFriendly) -> {
            if (newFriendly != null && !newFriendly.equals(oldFriendly)) {
                String newId = displayIdMap.getOrDefault(newFriendly, newFriendly);
                callback.update(newId);
            }
        });

        getChildren().add(combo);
    }

    public void config() {
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
    }
}