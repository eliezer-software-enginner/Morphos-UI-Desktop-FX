package my_app.components;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.FileManager;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.ChildHandlerComponent;
import my_app.components.shared.ItemsAmountPreviewComponent;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

import java.util.ArrayList;
import java.util.List;

// ColumnItens.java
public class ColumnComponent extends VBox implements ViewContractv2<ColumnComponentData> {
    SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None");

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(2);
    public StringProperty name = new SimpleStringProperty();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    private final HomeViewModel viewModel;
    private final CanvaComponentV2 canva;

    boolean isDeleted = false;

    public String dataTableVariableName;

    private final TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();
    private List<String> valuesOfVariableName = new ArrayList<>();

    public ColumnComponent(HomeViewModel viewModel, CanvaComponentV2 canva) {
        setSpacing(5);
        setStyle("-fx-background-color:red;");

        setAlignment(Pos.CENTER);
        setPrefWidth(Region.USE_COMPUTED_SIZE);

        setId(String.valueOf(System.currentTimeMillis()));

        this.viewModel = viewModel;
        this.canva = canva;

        getChildren().add(new TextComponent("Im new here", viewModel, canva));
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
    public Node getCurrentNode() {
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
                final var newNodeWrapper =
                        (ViewContractv2<ComponentData>) cloneExistingNode((ViewContractv2<ComponentData>) existingNode, i);

                var node = newNodeWrapper.getCurrentNode();
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
            final var newNodeWrapper = cloneExistingNode((ViewContractv2<ComponentData>) existingNode, -1);

            if (newNodeWrapper != null) {
                var node = newNodeWrapper.getCurrentNode();
                node.setMouseTransparent(true); // Impede a interação com a cópia placeholder

                getChildren().add(node);
            }
        } catch (IllegalStateException e) {
            System.err.println("Aviso: Componente placeholder " + emptyComponentId + " não encontrado: " + e.getMessage());
        }
    }

    private ViewContractv2<? extends ComponentData> cloneExistingNode(ViewContractv2<ComponentData> existingNode, int currentIndex) {
        // Lógica de clonagem mantida, pois é necessária para criar as cópias.
        var originalData = existingNode.getData();
        var type = originalData.type();

        if (type.equalsIgnoreCase(englishBase.button())) {
            var newNodeWrapper = new ButtonComponent(this.viewModel, canva);
            newNodeWrapper.applyData((ButtonComponentData) originalData);

            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }

            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.image())) {
            var newNodeWrapper = new ImageComponentv2(this.viewModel, canva);
            newNodeWrapper.applyData((ImageComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            var newNodeWrapper = new InputComponent(this.viewModel, canva);
            newNodeWrapper.applyData((InputComponentData) originalData);
            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.text())) {
            var newNodeWrapper = new TextComponent(this.viewModel, canva);
            newNodeWrapper.applyData((TextComponentData) originalData);
            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }
            return newNodeWrapper;
        } else {
            var newNodeWrapper = new CustomComponent(this.viewModel, canva);
            newNodeWrapper.applyData((CustomComponentData) originalData);
            return newNodeWrapper;
        }
    }

    // MÉTODO AJUSTADO: Agora depende SOMENTE da ViewModel
    private ViewContractv2<?> searchNode(String componentId) {
        var optionalNode = this.viewModel.SearchNodeById(componentId);
        return optionalNode.orElseThrow(() -> new IllegalStateException("Template component with ID " + componentId + " not found in ViewModel."));
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                new ChildHandlerComponent("Child component:", this, currentChildIdState, this.viewModel),
                new ItemsAmountPreviewComponent(this),
                // ChildHandlerComponent para onEmptyComponentState removido daqui, se não era usado
                Components.LabelWithComboBox("Data list", this, "data-list"),
                new ButtonRemoverComponent(this, this.viewModel)
        );
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva));
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent(translation.variableName(), name.get(), v -> name.set(v)));
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