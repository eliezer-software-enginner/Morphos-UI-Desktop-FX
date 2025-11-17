package my_app.components.shared;

import javafx.beans.property.SimpleStringProperty;
import my_app.components.ColumnComponent;

// OnEmptyComponent Refatorado (FINAL)
public class OnEmptyComponent extends BaseChildSelectorComponent {

    private final SimpleStringProperty onEmptyComponentState;

    // A assinatura do construtor deve ser atualizada para SimpleStringProperty
    public OnEmptyComponent(ColumnComponent nodeTarget, SimpleStringProperty onEmptyComponentState) {

        // Passa o rótulo e o valor inicial (que é apenas o ID)
        super(nodeTarget, "On Empty:", onEmptyComponentState.get());
        this.onEmptyComponentState = onEmptyComponentState;

        // Ouve a StringProperty (estado) e a reflete no ComboBox
        onEmptyComponentState.addListener((_a, _b, newId) -> {
            // Se a propriedade for mudada externamente (e.g., applyData), atualiza o Combo.
            if (combo.getItems().contains(newId)) {
                combo.setValue(newId);
            } else {
                combo.setValue(DEFAULT_SELECTION);
            }
        });
    }

    @Override
    protected void setupChangeListener(ColumnComponent nodeTarget) {
        // O listener agora simplesmente seta o ID na SimpleStringProperty.
        combo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Seta o ID ("None" ou o ID real).
                onEmptyComponentState.set(newVal);

                // Força a recriação dos filhos se a lista estiver vazia.
                if (nodeTarget.childrenAmountState.get() == 0) {
                    nodeTarget.recreateChildren();
                }
            }
        });
    }
}
