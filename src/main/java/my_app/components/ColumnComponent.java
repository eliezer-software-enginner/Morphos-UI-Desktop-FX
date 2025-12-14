package my_app.components;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.FileManager;
import my_app.data.ColumnComponentData;
import my_app.data.Commons;
import my_app.data.ComponentData;
import my_app.data.contracts.ViewComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

import java.util.ArrayList;
import java.util.List;

// ColumnItens.java
public final class ColumnComponent extends VBox implements ViewComponent<ColumnComponentData> {
    public SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None");

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(2);
    public StringProperty name = new SimpleStringProperty();

    private final HomeViewModel viewModel;
    private final CanvaComponentV2 canva;

    boolean isDeleted = false;

    public String dataTableVariableName;

    private final List<String> valuesOfVariableName = new ArrayList<>();

    public ColumnComponent(HomeViewModel viewModel, CanvaComponentV2 canva) {
        setSpacing(5);

        setAlignment(Pos.CENTER);
        setPrefWidth(Region.USE_COMPUTED_SIZE);

        setId(String.valueOf(System.currentTimeMillis()));

        this.viewModel = viewModel;
        this.canva = canva;

        getChildren().add(new TextComponent("Im new here"));
    }

    @Override
    public void applyData(ColumnComponentData data) {
        getChildren().clear();

        this.setLayoutX(data.x());
        this.setLayoutY(data.y());

        this.setId(data.identification());

        String childId = data.childId() == null ? "None" : data.childId();
        String alternativeChildId = data.alternativeChildId() == null ? "None" : data.alternativeChildId();

        currentChildIdState.set(childId);
        onEmptyComponentState.set(alternativeChildId);
        childrenAmountState.set(data.pref_child_amount_for_preview());
        isDeleted = data.isDeleted();
        this.dataTableVariableName = data.dataTableVariableName();

        // Carrega os valores da variável (mantido, pois é um cache de dados, não de componentes)
        valuesOfVariableName.clear();
        valuesOfVariableName.addAll(FileManager.getValuesFromVariableName(data.dataTableVariableName()));

        recreateChildren();
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

    // -------------------------------------------------------------------
    // MÉTODO AJUSTADO: Lógica Centralizada para Recriar os Filhos
    // -------------------------------------------------------------------
    public void recreateChildren() {
        int amount = childrenAmountState.get();

        getChildren().clear();

        if (amount == 0) {
            renderComponentForStateEmpty();
            return;
        }

        String currentChildId = currentChildIdState.get();
        if (currentChildId.equals("None")) {
            return;
        }

        final var existingNode = searchNode(currentChildId);

        var copies = new ArrayList<Node>();

        if (valuesOfVariableName.size() >= amount) {
            for (int i = 0; i < amount; i++) {
                final var newNodeWrapper = cloneExistingNode(existingNode, i);

                var node = newNodeWrapper.getNode();
                node.setMouseTransparent(true); // Impede a interação com as cópias
                copies.add(node);
            }
        }

        getChildren().addAll(copies);
    }

    private void renderComponentForStateEmpty() {
        String emptyComponentId = onEmptyComponentState.get();

        if (emptyComponentId.equals("None") || emptyComponentId.isEmpty()) {
            return;
        }

        try {
            final var existingNode = searchNode(emptyComponentId);
            final var newNodeWrapper = cloneExistingNode(existingNode, -1);

            if (newNodeWrapper != null) {
                var node = newNodeWrapper.getNode();
                node.setMouseTransparent(true); // Impede a interação com a cópia placeholder

                getChildren().add(node);
            }
        } catch (IllegalStateException e) {
            System.err.println("Aviso: Componente placeholder " + emptyComponentId + " não encontrado: " + e.getMessage());
        }
    }

    private ViewComponent<? extends ComponentData> cloneExistingNode(ViewComponent<?> existingNode, int currentIndex) {
        return ComponentsFactory.cloneFrom(
                existingNode,
                viewModel,
                canva,
                currentIndex,
                valuesOfVariableName.get(currentIndex)
        );
    }

    // MÉTODO AJUSTADO: Agora depende SOMENTE da ViewModel
    private ViewComponent<?> searchNode(String componentId) {
        var optionalNode = this.viewModel.SearchNodeById(componentId);
        return optionalNode.orElseThrow(() -> new IllegalStateException("Template component with ID " + componentId + " not found in ViewModel."));
    }


    @Override
    public ColumnComponentData getData() {

        String childId = currentChildIdState.get().equals("None") ? null : currentChildIdState.get();

        String alternativeChildId = onEmptyComponentState.get().equals("None") ? null : onEmptyComponentState.get();
        var location = Commons.NodeInCanva(this);

        // Retorna o novo ColumnComponentData
        return new ColumnComponentData(
                "column items",
                this.getId(),
                childId, // Usa o ID do estado (fonte correta)
                alternativeChildId,
                (int) getLayoutX(),
                (int) getLayoutY(),
                location.inCanva(),
                location.fatherId(),
                childrenAmountState.get(),
                isDeleted,
                this.dataTableVariableName
        );
    }

    public void setDataTableVariableName(String dataTableVariableName) {
        this.dataTableVariableName = dataTableVariableName;

        valuesOfVariableName.clear();

        var values = FileManager.getValuesFromVariableName(dataTableVariableName);

        valuesOfVariableName.addAll(values);
        recreateChildren();
    }
}