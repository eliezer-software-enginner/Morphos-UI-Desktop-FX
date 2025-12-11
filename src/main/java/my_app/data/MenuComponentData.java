package my_app.data;

import java.io.Serializable;
import java.util.List;

public record MenuComponentData(
        String type, // "menu component"
        String identification,
        List<MenuItemData> items, // Lista de itens específicos do menu
        int x,
        int y,
        boolean in_canva,
        String canva_id,
        boolean isDeleted
) implements ComponentData {

    public MenuComponentData {
        if (type == null) {
            type = "menu component";
        }
    }

    /**
     * Define o estado de um único item de menu.
     * name: Texto exibido no item.
     * functionName: Nome da função de callback no código gerado.
     * childId: ID do componente template a ser clonado para a representação visual.
     */
    public record MenuItemData(
            String name,
            String functionName,
            String childId
    ) implements Serializable {
        public MenuItemData() {
            this("New Item", "handleItemClick", "None");
        }
    }
}