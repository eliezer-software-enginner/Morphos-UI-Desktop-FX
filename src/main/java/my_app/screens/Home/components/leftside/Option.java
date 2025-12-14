package my_app.screens.Home.components.leftside;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import toolkit.Component;

//--Button (OptionHeader)
//     -btn1 (subItem)

public class Option extends VBox {
    private final HomeViewModel viewModel;
    BooleanProperty expanded = new SimpleBooleanProperty(false);

    @Component
    VBox subItemsContainer = new VBox();
    String type;

    @Component
    OptionHeader header;
    @Component
    CanvaComponentV2 currentCanva;


    public Option(LeftSide.Field field, CanvaComponentV2 currentCanva, HomeViewModel viewModel) {
        this.type = field.nameEngligh().toLowerCase().trim();

        this.viewModel = viewModel;
        this.currentCanva = currentCanva;

        header = new OptionHeader(field, currentCanva, expanded, viewModel);

        getChildren().add(header);
        getChildren().add(subItemsContainer);

        System.out.println(this.type);

        loadSubItems();

        viewModel.leftItemsStateRefreshed.addListener((_, _, _) -> {
            loadSubItems();
        });

        subItemsContainer.managedProperty().bind(expanded);
        subItemsContainer.visibleProperty().bind(expanded);

        subItemsContainer.setPadding(new Insets(5, 0, 0, 20));
        subItemsContainer.setSpacing(2);
    }

    private void loadSubItems() {
        subItemsContainer.getChildren().clear();

        var nodes = viewModel.getItemsByType(type);
        IO.println("nodes: " + nodes.size());

        for (var nodeWrapper : nodes) {
            String itemId = nodeWrapper.getNode().getId();

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
        viewModel.nodeSelected.addListener((_, _, _) -> {
            updateSubItemStyle(subItemBox, itemId);
        });

        subItemBox.setOnMouseClicked(_ -> onClickOnSubItem(itemId));

        subItemBox.setOnMouseEntered(_ -> {
            if (!viewModel.currentNodeIsSelected(itemId)) {
                subItemBox.setStyle("-fx-background-color: #2D2A6E;");
            }
        });

        subItemBox.setOnMouseExited(_ -> {
            if (!viewModel.currentNodeIsSelected(itemId)) {
                subItemBox.setStyle("-fx-background-color: transparent;");
            }
        });

        return subItemBox;
    }

    // Método auxiliar para aplicar/remover o estilo de seleção
    private void updateSubItemStyle(HBox subItemBox, String itemId) {
        if (viewModel.nodeSelected.get() != null && viewModel.currentNodeIsSelected(itemId)) {
            subItemBox.setStyle("-fx-background-color: red;");
            expanded.set(true); // Opcional: Expande o menu se o nó for selecionado
        } else {
            subItemBox.setStyle("-fx-background-color: transparent;");
        }
    }

    void onClickOnSubItem(String itemIdentification) {

        var canvaChildren = this.currentCanva.getChildren();

        var op = viewModel.SearchNodeById(itemIdentification);

        op.ifPresent(_ -> {
            var target = HomeViewModel.SearchNodeByIdInMainCanva(itemIdentification, canvaChildren);
            // 2. finded in main canva so, selected
            if (target != null) {
                viewModel.selectNode(target);
                this.currentCanva.Shake(target);
                //CanvaComponentV2.Shake(target);
            } else {
                // if not, just add in canva
                this.currentCanva.addElementDragable(op.get().getNode(), false);
            }
        });

    }

    // NOVO MÉTODO: Chamado por LeftSide para atualizar a referência
    public void updateCanva(CanvaComponentV2 newCanva) {
        this.currentCanva = newCanva; // 1. Atualiza a referência em Option
        this.header.updateCanva(newCanva); // 2. Chama a atualização no OptionHeader
    }
}
