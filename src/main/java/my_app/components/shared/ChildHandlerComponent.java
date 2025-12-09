package my_app.components.shared;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.components.ColumnComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.HashSet;
import java.util.Set;

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

        Set<String> uniqueItems = new HashSet<>();
        uniqueItems.add("None");

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

                // Filtro 2: ignora o próprio id
                if (id.equals(self.getId())) {
                    continue;
                }

                uniqueItems.add(id);
            }
        }

        // Garante que o ID atualmente selecionado esteja na lista (evita que a ComboBox fique vazia se o item tiver sido deletado)
        String currentId = currentNodeId.get();
        if (currentId != null && !currentId.isEmpty() && !currentId.equals("None")) {
            uniqueItems.add(currentId);
        }

        combo.getItems().setAll(uniqueItems);
        combo.setValue(currentId);

        combo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.equals(old)) {
                currentNodeId.set(newVal);
                self.recreateChildren();
            }
        });

        getChildren().addAll(this.title, combo);
    }

    void config() {
        setSpacing(10);
    }
}