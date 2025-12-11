package my_app.mappers;

import my_app.components.*;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.data.StateJson_v3;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class CanvaMapper {

    public static StateJson_v3 toStateJson(CanvaComponentV2 canva, HomeViewModel homeViewModel) {
        final var nodeSelected = homeViewModel.nodeSelected.get();
        final var headerSelected = homeViewModel.headerSelected.get();
        final var dataMap = homeViewModel.dataMap;

        var jsonTarget = new StateJson_v3();
        jsonTarget.screen_id = canva.screenFatherId;
        jsonTarget.name = canva.name;

        jsonTarget.id_of_component_selected = nodeSelected == null ? null
                : nodeSelected.node().getId();

        jsonTarget.type_of_component_selected = headerSelected;

        // 1. Salva as propriedades do CanvaComponent
        var canvaData = canva.getData();
        jsonTarget.canva = canvaData;

        // 2. Itera sobre TODOS os nós (nodes) no dataMap
        // Para cada lista de nós (os VALUES do dataMap)...
        for (var nodesList : dataMap.values()) {
            // ...itera sobre cada Node dentro dessa lista.
            for (var nodeWrapper : nodesList) {
                // A LÓGICA DE SERIALIZAÇÃO PERMANECE A MESMA

                var node = nodeWrapper.getCurrentNode();

                if (node instanceof TextComponent component) {
                    // O .getData() deve retornar um TextComponentData que inclui a flag 'in_canva'
                    canvaData.text_components.add(component.getData());
                }

                if (node instanceof ButtonComponent component) {
                    canvaData.button_components.add(component.getData());
                }

                if (node instanceof ImageComponentv2 component) {
                    canvaData.image_components.add(component.getData());
                }

                if (node instanceof InputComponent component) {
                    canvaData.input_components.add(component.getData());
                }

                // Se o FlexComponent for uma composição de outros nós, ele deve serializar seus
                // filhos internamente.
                // CustomComponent, se for salvo como InnerComponentData.
                // Verifique se o getData() dele é compatível com InnerComponentData.
                // **Atenção:** Se ele for uma instância que contém outros componentes,
                // sua lógica de getData() deve ser recursiva (salvar seus filhos).
                if (node instanceof CustomComponent component) {
                    // Supondo que getData() retorne InnerComponentData ou StateJson_v2 completo
                    canvaData.custom_components.add(component.getData());
                }

                if (node instanceof ColumnComponent component) {
                    canvaData.column_components.add(component.getData());
                }
                if (node instanceof MenuComponent component) {
                    canvaData.menu_components.add(component.getData());
                }
            } // Fim do loop interno (iteração sobre Nodes)
        } // Fim do loop externo (iteração sobre as Listas)

        return jsonTarget;
    }

    public static CanvaComponentV2 fromScreenToCanva(StateJson_v3 screen, HomeViewModel homeViewModel) {
        final var canvaComponent = new CanvaComponentV2(homeViewModel);

        canvaComponent.applyData(screen.canva);
        canvaComponent.screenFatherId = screen.screen_id;
        canvaComponent.name = screen.name;

        return canvaComponent;
    }
}
