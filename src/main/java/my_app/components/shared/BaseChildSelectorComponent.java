package my_app.components.shared;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import my_app.components.ColumnComponent;
import my_app.contexts.SubItemsContext;
import my_app.themes.Typography;

/**
 * Classe base abstrata para componentes que gerenciam a seleção de um
 * componente filho (e.g., ChildHandlerComponent, OnEmptyComponent). Centraliza
 * a lógica de UI (HBox, Text, ComboBox) e a lógica de popular a lista com IDs
 * de componentes disponíveis.
 */
public abstract class BaseChildSelectorComponent extends HBox {

    protected Label title; // Protegido para que subclasses possam mudar o título
    protected ComboBox<String> combo = new ComboBox<>();
    protected SubItemsContext context = SubItemsContext.getInstance();

    // O ID padrão é 'None' para OnEmptyComponent, e agora também será 'None' para
    // ChildHandlerComponent.
    protected static final String DEFAULT_SELECTION = "None";

    /**
     * @param nodeTarget       O ColumnItens pai, usado para filtrar a si mesmo.
     * @param label            O texto do rótulo (e.g., "Child component:" ou "On
     *                         Empty:")
     * @param currentSelection O valor inicial que deve ser selecionado no
     *                         ComboBox.
     */
    public BaseChildSelectorComponent(ColumnComponent nodeTarget, String label, String currentSelection) {
        this.title = Typography.caption(label);

        config();
        populateCombo(nodeTarget);

        // Aplica o valor inicial
        combo.setValue(currentSelection);

        getChildren().addAll(this.title, combo);

        // Adiciona o listener principal. As subclasses devem implementá-lo.
        setupChangeListener(nodeTarget);
    }

    // Método abstrato que as subclasses devem implementar para definir a lógica de
    // o que acontece quando o usuário muda a seleção.
    protected abstract void setupChangeListener(ColumnComponent nodeTarget);

    // Lógica para popular a ComboBox (IDÊNTICA em ambos os originais)
    private void populateCombo(ColumnComponent nodeTarget) {
        Set<String> uniqueItems = new HashSet<>();
        uniqueItems.add(DEFAULT_SELECTION); // Adiciona o item padrão ("None")

        // Itera sobre os GRUPOS de componentes
        for (var entry : context.getAllData().entrySet()) {
            String componentType = entry.getKey();

            // Filtro: ignora ColumnComponent
            if (componentType.equals("column items")) {
                continue;
            }

            // Itera sobre os IDs desse grupo
            for (SimpleStringProperty idProperty : entry.getValue()) {
                String id = idProperty.get();

                // Filtro: ignora o próprio nodeTarget
                if (id.equals(nodeTarget.getId())) {
                    continue;
                }

                uniqueItems.add(id); // garante unicidade
            }
        }

        combo.getItems().setAll(uniqueItems);
    }

    private void config() {

        setSpacing(10);
    }
}
