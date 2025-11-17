package my_app.components.shared;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.components.ColumnComponent;
import my_app.contexts.ComponentsContext;
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
            SimpleStringProperty currentNodeId, ComponentsContext context) {

        this.title.setText(title);

        config();

        // Usamos Set para evitar duplicados
        Set<String> uniqueItems = new HashSet<>();
        uniqueItems.add("None"); // adiciona o item padrão

        // Itera sobre os GRUPOS de componentes
        for (var entry : context.dataMap.entrySet()) {
            String componentType = entry.getKey();

            // Filtro 2: ignora ColumnItens
            if (componentType.equals("column items")) {
                continue;
            }

            // Itera sobre os IDs desse grupo
            for (var node : entry.getValue()) {
                String id = node.getId();

                // Filtro 1: ignora o próprio id
                if (id.equals(self.getId())) {
                    continue;
                }

                uniqueItems.add(id); // garante unicidade
            }
        }

        // Garante que o item atual também esteja na lista
        if (currentNodeId.get() != null && !currentNodeId.get().isEmpty()) {
            uniqueItems.add(currentNodeId.get());
        }

        // Adiciona todos ao combo de uma vez
        combo.getItems().setAll(uniqueItems);

        // Mantém o valor selecionado
        combo.setValue(currentNodeId.get());

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
