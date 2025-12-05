package my_app.mappers;

import javafx.collections.ObservableList;
import my_app.components.ColumnComponent;
import my_app.components.CustomComponent;
import my_app.components.InputComponent;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.StateJson_v2;
import my_app.data.ViewContract;

public class CanvaMapper {
    public static StateJson_v2 toStateJson(CanvaComponent canva, ComponentsContext componentsContext) {
        final var nodeSelected = componentsContext.nodeSelected.get();
        final var headerSelected = componentsContext.headerSelected.get();
        final var dataMap = componentsContext.dataMap;

        StateJson_v2 jsonTarget = new StateJson_v2();
        jsonTarget.id_of_component_selected = nodeSelected == null ? null
                : nodeSelected.node().getId();

        jsonTarget.type_of_component_selected = headerSelected;

        // 1. Salva as propriedades do CanvaComponent
        jsonTarget.canva = canva.getData();

        // 2. Itera sobre TODOS os nós (nodes) no dataMap
        // Para cada lista de nós (os VALUES do dataMap)...
        for (ObservableList<ViewContract<?>> nodesList : dataMap.values()) {
            // ...itera sobre cada Node dentro dessa lista.
            for (var nodeWrapper : nodesList) {
                // A LÓGICA DE SERIALIZAÇÃO PERMANECE A MESMA

                var node = nodeWrapper.getCurrentNode();

                if (node instanceof TextComponent component) {
                    // O .getData() deve retornar um TextComponentData que inclui a flag 'in_canva'
                    jsonTarget.text_components.add(component.getData());
                }

                if (node instanceof ButtonComponent component) {
                    jsonTarget.button_components.add(component.getData());
                }

                if (node instanceof ImageComponent component) {
                    jsonTarget.image_components.add(component.getData());
                }

                if (node instanceof InputComponent component) {
                    jsonTarget.input_components.add(component.getData());
                }

                // Se o FlexComponent for uma composição de outros nós, ele deve serializar seus
                // filhos internamente.
                // CustomComponent, se for salvo como InnerComponentData.
                // Verifique se o getData() dele é compatível com InnerComponentData.
                // **Atenção:** Se ele for uma instância que contém outros componentes,
                // sua lógica de getData() deve ser recursiva (salvar seus filhos).
                if (node instanceof CustomComponent component) {
                    // Supondo que getData() retorne InnerComponentData ou StateJson_v2 completo
                    jsonTarget.custom_components.add(component.getData());
                }

                if (node instanceof ColumnComponent component) {
                    jsonTarget.column_components.add(component.getData());
                }
            } // Fim do loop interno (iteração sobre Nodes)
        } // Fim do loop externo (iteração sobre as Listas)

        return jsonTarget;
    }
}
