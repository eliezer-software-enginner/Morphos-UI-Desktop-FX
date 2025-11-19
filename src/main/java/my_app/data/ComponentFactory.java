package my_app.data;

// ComponentFactory.java (NOVA CLASSE)

import javafx.scene.Node;
import my_app.components.CustomComponent;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.ColumnComponent;
import my_app.components.InputComponent;
import my_app.contexts.ComponentsContext;

@Deprecated(forRemoval = true, since = "19/nov/2025")
public class ComponentFactory {

    @Deprecated(forRemoval = true, since = "17/nov/2025")
    public static Node createNodeFromData(ComponentData data, ComponentsContext componentsContext) {
        if (data == null) {
            return null;
        }

        Node component = null;

        // O switch baseado no campo 'type' (que deve existir em todos os ComponentData)
        switch (data.type()) {
            case "text":
                // 1. Cria a instância
                component = new TextComponent("", componentsContext, null);
                // 2. Aplica os dados (assumindo que TextComponent implementa
                // applyData<TextComponentData>)
                ((ViewContract<TextComponentData>) component).applyData((TextComponentData) data);
                break;

            case "component":
                component = new CustomComponent(componentsContext, null);
                // Assumindo que o applyData do CustomComponent recebe CustomComponentData
                ((ViewContract<CustomComponentData>) component).applyData((CustomComponentData) data);
                break;

            case "button":
                component = new ButtonComponent(componentsContext, null);
                ((ViewContract<ButtonComponentData>) component).applyData((ButtonComponentData) data);
                break;

            case "column items":
                // Em casos de contêineres, evite recursão desnecessária no Factory.
                // O ColumnComponent aplica os dados e recria os filhos internamente.
                component = new ColumnComponent(componentsContext, null);
                ((ViewContract<ColumnComponentData>) component).applyData((ColumnComponentData) data);
                break;

            case "input":
                // Em casos de contêineres, evite recursão desnecessária no Factory.
                // O ColumnComponent aplica os dados e recria os filhos internamente.
                IO.println("Em componente factory -> input");
                component = new InputComponent(componentsContext, null);
                ((ViewContract<InputComponentData>) component).applyData((InputComponentData) data);
                break;

            // Adicionar outros tipos de componentes (Input, Image, etc.)
            default:
                System.err.println("Tipo de componente desconhecido: " + data.type());
                return null;
        }

        return component;
    }
}