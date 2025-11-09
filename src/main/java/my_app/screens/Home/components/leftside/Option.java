package my_app.screens.Home.components.leftside;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.screens.Home.Home;
import my_app.themes.Typography;
import toolkit.Component;

//--Button (OptionHeader)
//     -btn1 (subItem)

public class Option extends VBox {
    BooleanProperty expanded = new SimpleBooleanProperty(false);

    @Component
    VBox subItemsContainer = new VBox();
    String type;

    @Component
    OptionHeader header;

    ComponentsContext componentsContext;

    @Component
    Home home;

    public Option(LeftSide.Field field, Home home, ComponentsContext componentsContext) {
        this.type = field.nameEngligh().toLowerCase().trim();
        this.home = home;
        this.componentsContext = componentsContext;

        header = new OptionHeader(field, home, expanded, componentsContext);

        getChildren().add(header);
        getChildren().add(subItemsContainer);

        System.out.println(this.type);

        loadSubItems();

        componentsContext.leftItemsStateRefreshed.addListener((_, _, _) -> {
            loadSubItems();
        });

        subItemsContainer.managedProperty().bind(expanded);
        subItemsContainer.visibleProperty().bind(expanded);

        subItemsContainer.setPadding(new Insets(5, 0, 0, 20));
        subItemsContainer.setSpacing(2);
    }

    private void loadSubItems() {
        subItemsContainer.getChildren().clear();

        ObservableList<Node> nodes = componentsContext.getItemsByType(type);

        for (Node node : nodes) {
            String itemId = node.getId();

            HBox subItemBox = createSubItemBox(itemId);

            subItemsContainer.getChildren().add(subItemBox);
        }
    }

    @Component
    private HBox createSubItemBox(String itemId) {
        HBox subItemBox = new HBox();

        // 1. Crie o ponto de lista (bullet) separadamente
        Label bullet = new Label("•");
        bullet.setFont(Font.font("Arial", 20)); // Força uma fonte padrão para o bullet
        bullet.setTextFill(Color.LIGHTGRAY);
        bullet.setPadding(new Insets(0, 4, 0, 0)); // Espaçamento entre bullet e texto

        // 2. Crie o texto (itemId) com a fonte customizada
        Label subLabel = Typography.caption(itemId);
        subLabel.setFont(Font.font(20)); // Mantém a fonte customizada

        subItemBox.setId(itemId);

        subItemBox.setId(itemId);

        subLabel.setFont(Font.font(20));
        subLabel.setTextFill(Color.LIGHTGRAY);

        subItemBox.getChildren().addAll(bullet, subLabel);
        subItemBox.setPadding(new Insets(3, 5, 3, 10));

        // Estilo inicial:
        updateSubItemStyle(subItemBox, itemId);

        // Adiciona um listener para que, se o nó for selecionado/deselecionado, o
        // estilo mude
        componentsContext.nodeSelected.addListener((_, _, _) -> {
            updateSubItemStyle(subItemBox, itemId);
        });

        subItemBox.setOnMouseClicked(_ -> onClickOnSubItem(itemId, this.type, home.canva));

        subItemBox.setOnMouseEntered(_ -> {
            if (!componentsContext.currentNodeIsSelected(itemId)) {
                subItemBox.setStyle("-fx-background-color: #2D2A6E;");
            }
        });

        subItemBox.setOnMouseExited(_ -> {
            if (!componentsContext.currentNodeIsSelected(itemId)) {
                subItemBox.setStyle("-fx-background-color: transparent;");
            }
        });

        return subItemBox;
    }

    // Método auxiliar para aplicar/remover o estilo de seleção
    private void updateSubItemStyle(HBox subItemBox, String itemId) {
        if (componentsContext.nodeSelected.get() != null && componentsContext.currentNodeIsSelected(itemId)) {
            subItemBox.setStyle("-fx-background-color: red;");
            expanded.set(true); // Opcional: Expande o menu se o nó for selecionado
        } else {
            subItemBox.setStyle("-fx-background-color: transparent;");
        }
    }

    void onClickOnSubItem(String itemIdentification, String type,
                          CanvaComponent mainCanvaComponent) {

        var canvaChildren = mainCanvaComponent.getChildren();

        var op = componentsContext.SearchNodeById(itemIdentification);

        op.ifPresent(_ -> {
            var target = ComponentsContext.SearchNodeByIdInMainCanva(itemIdentification, canvaChildren);
            // 2. finded in main canva so, selected
            if (target != null) {
                componentsContext.selectNode(target);
                CanvaComponent.Shake(target);
            } else {
                // if not, just add in canva
                mainCanvaComponent.addElementDragable(op.get(), false);
            }
        });

    }

}
