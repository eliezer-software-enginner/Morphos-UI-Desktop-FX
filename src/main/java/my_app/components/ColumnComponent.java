package my_app.components;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.FileManager;
import my_app.components.buttonComponent.ButtonComponentv2;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.ItemsAmountPreviewComponent;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponent;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

import java.util.ArrayList;
import java.util.List;

// ColumnItens.java
public class ColumnComponent extends VBox implements ViewContract<ColumnComponentData> {
    SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None"); // Novo padrão: "None"

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(3);
    public StringProperty name = new SimpleStringProperty();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    private final HomeViewModel viewModel;
    private final CanvaComponentV2 canva;

    boolean isDeleted = false;

    public String dataTableVariableName;

    public ColumnComponent(HomeViewModel viewModel, CanvaComponentV2 canva) {
        setSpacing(5);
        setStyle("-fx-background-color:red;");

        setAlignment(Pos.CENTER);
        setPrefWidth(Region.USE_COMPUTED_SIZE); // Permite que o VBox se ajuste ao conteúdo/pai

        setId(String.valueOf(System.currentTimeMillis()));

        this.viewModel = viewModel;
        this.canva = canva;

        getChildren().add(new TextComponentv2("Im new here", viewModel, canva));
    }

    @Override
    public void applyData(ColumnComponentData data) {
        // Limpa os filhos existentes antes de aplicar o novo estado
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

        valuesOfVariableName.addAll(FileManager.getValuesFromVariableName(data.dataTableVariableName()));

        // 3. Chamar a lógica centralizada (permanece igual)
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

    private final TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();
    private final List<ViewContractv2<?>> localComponents = new ArrayList<>();

    // -------------------------------------------------------------------
    // NOVO MÉTODO: Lógica Centralizada para Recriar os Filhos
    // -------------------------------------------------------------------
    public void recreateChildren() {
        int amount = childrenAmountState.get();

        // 1. Limpa todos os filhos existentes
        getChildren().clear();

        if (amount == 0) {
            renderComponentForStateEmpty();
            return; // Encerra a função, pois o placeholder foi adicionado
        }

        // 2. SE A QUANTIDADE FOR MAIOR QUE ZERO...
        String currentChildId = currentChildIdState.get();
        final var existingNode = searchNode(currentChildId);

        var copies = new ArrayList<Node>();
        boolean nodeOriginalRemoved = false;

        if (valuesOfVariableName.size() >= amount) {
            for (int i = 0; i < amount; i++) {
                ViewContract<ComponentData> newNodeWrapper =
                        (ViewContract<ComponentData>) cloneExistingNode((ViewContractv2<ComponentData>) existingNode, i);


                // Cria uma NOVA cópia do nó a partir dos dados originais
                // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
                var node = newNodeWrapper.getCurrentNode();
                node.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

                if (!nodeOriginalRemoved) {
                    localComponents.add(existingNode);
                    this.viewModel.removeComponentFromAllPlaces(existingNode, canva);
                }

                // Aplicamos o ID da cópia
                copies.add(node);

            }
        }

        getChildren().addAll(copies);
    }

    private void renderComponentForStateEmpty() {
        // SE A QUANTIDADE FOR ZERO, exibe o componente de placeholder
        String emptyComponentId = onEmptyComponentState.get();

        if (emptyComponentId.equals("None") || emptyComponentId.isEmpty()) {
            // Não há placeholder para exibir
            return;
        }

        final var existingNode = searchNode(emptyComponentId);
        final var newNodeWrapper = cloneExistingNode((ViewContractv2<ComponentData>) existingNode, -1);

        //aqui eu posso remover ele do header e do canva
        if (newNodeWrapper != null) {
            // Cria uma NOVA cópia do nó a partir dos dados originais
            // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
            var node = newNodeWrapper.getCurrentNode();
            node.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

            //adiciona em cache local e remove do contexto
            localComponents.add(existingNode);

            this.viewModel.removeComponentFromAllPlaces(existingNode, canva);
            getChildren().add(node);
        }
    }

    private ViewContractv2<? extends ComponentData> cloneExistingNode(ViewContractv2<ComponentData> existingNode, int currentIndex) {
        var originalData = existingNode.getData();
        var type = originalData.type();

        if (type.equalsIgnoreCase(englishBase.button())) {
            var newNodeWrapper = new ButtonComponentv2(this.viewModel, canva);
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
            var newNodeWrapper = new InputComponentv2(this.viewModel, canva);
            newNodeWrapper.applyData((InputComponentData) originalData);
            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.text())) {
            var newNodeWrapper = new TextComponentv2(this.viewModel, canva);
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

    private ViewContractv2<?> searchNode(String emptyComponentId) {
        // Busca o nó original pelo ID e faz a DEEP COPY
        var op = this.viewModel.SearchNodeById(emptyComponentId);
        ViewContractv2<?> existingNode;

        if (op.isPresent()) {
            existingNode = op.get();
        } else {
            existingNode = localComponents.stream().
                    filter(it -> it.getCurrentNode().getId().equals(emptyComponentId))
                    .findFirst().get();
        }
        return existingNode;
    }

    @Override
    public void appearance(VBox father, CanvaComponent canva) {
        father.getChildren().setAll(
                //    new ChildHandlerComponent("Child component:", this, currentChildIdState, componentsContext),
                new ItemsAmountPreviewComponent(this),
                //  new ChildHandlerComponent("Component (if empty):", this, onEmptyComponentState, componentsContext),
                Components.LabelWithComboBox("Data list", this, "data-list"),
                new ButtonRemoverComponent(this, this.viewModel)
        );
    }

    @Override
    public void settings(VBox father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva));
    }

    @Override
    public void otherSettings(VBox father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent(translation.variableName(), name.get(), v -> name.set(v)));
    }

    @Override
    public ColumnComponentData getData() {

        String childId = null;
        if (!getChildren().isEmpty()) {
            Node firstChild = getChildren().getFirst();
            childId = firstChild.getId();
        }

        String alternativeChildId = onEmptyComponentState.get().equals("None") ? null : onEmptyComponentState.get();
        var location = Commons.NodeInCanva(this);

        // Retorna o novo ColumnComponentData
        return new ColumnComponentData(
                // NOVO: Adicione o tipo aqui
                "column items",
                this.getId(),
                // currentChild.get(),
                childId,
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

    private List<String> valuesOfVariableName = new ArrayList<>();

    public void setDataTableVariableName(String dataTableVariableName) {
        this.dataTableVariableName = dataTableVariableName;

        IO.println("value selected: " + dataTableVariableName);
        valuesOfVariableName.clear();

        var values = FileManager.getValuesFromVariableName(dataTableVariableName);
        IO.println(values);

        valuesOfVariableName.addAll(values);
        recreateChildren();
    }

}
