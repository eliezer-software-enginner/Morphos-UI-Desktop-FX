package my_app.components.shared;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.components.ColumnComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.HashMap;

public class ChildHandlerComponent extends HBox {

    @Component
    Label title = Typography.caption("Child component:");
    @Component
    ComboBox<String> combo = new ComboBox<>();

    public ChildHandlerComponent(
            String title,
            ColumnComponent self,
            SimpleStringProperty currentNodeId, HomeViewModel viewModel) {

        this.title.setText(title);

        config();

        //Set<String> uniqueItems = new HashSet<>();
        //uniqueItems.add("None");

        combo.getItems().add("None");

        // Mapeamento interno para armazenar "Nome Amigável" -> "ID Real"
        var displayIdMap = new HashMap<String, String>();
        displayIdMap.put("None", "None");

        // Itera sobre os componentes no ViewModel
        for (var entry : viewModel.dataMap.entrySet()) {
            String componentType = entry.getKey();

            // Filtro 1: ignora ColumnItens como filhos
            if (componentType.equals("column items")) {
                continue;
            }

            // Itera sobre os IDs desse grupo
            for (var nodeWrapper : entry.getValue()) {
                String id = nodeWrapper.getCurrentNode().getId();

                if (id.equals(self.getId())) {
                    continue;
                }

                // CRIAÇÃO DO NOME AMIGÁVEL: "Tipo - ID"
                String friendlyName = componentType + " - " + id;
                displayIdMap.put(friendlyName, id);
                combo.getItems().add(friendlyName);
            }
        }

        // CORREÇÃO ESSENCIAL: Garantir que o item atual seja exibido, mesmo que não esteja mais no dataMap
        String currentId = currentNodeId.get();
        String currentFriendlyName = "None";

        // Tenta encontrar o nome amigável para o ID atual, se houver
        if (!currentId.equals("None")) {
            currentFriendlyName = displayIdMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(currentId))
                    .map(java.util.Map.Entry::getKey)
                    .findFirst()
                    .orElse(currentId); // Se não encontrar, mostra o ID bruto como fallback

            if (!combo.getItems().contains(currentFriendlyName) && !currentFriendlyName.equals(currentId)) {
                combo.getItems().add(currentFriendlyName);
            }
        }


        combo.setValue(currentFriendlyName); // Define o valor amigável

        combo.valueProperty().addListener((obs, oldFriendly, newFriendly) -> {
            if (newFriendly != null && !newFriendly.equals(oldFriendly)) {
                // Obtém o ID real através do mapeamento
                String newId = displayIdMap.getOrDefault(newFriendly, newFriendly);

                currentNodeId.set(newId);
                self.recreateChildren();
            }
        });

        getChildren().addAll(this.title, combo);
    }

    void config() {
        setSpacing(10);
    }
}