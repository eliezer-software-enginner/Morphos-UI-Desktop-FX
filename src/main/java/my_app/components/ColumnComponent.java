package my_app.components;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.ChildHandlerComponent;
import my_app.components.shared.ItemsAmountPreviewComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.*;

import java.util.ArrayList;
import java.util.List;

// ColumnItens.java
public class ColumnComponent extends VBox implements ViewContract<ColumnComponentData> {
    SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None"); // Novo padrão: "None"

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(3);
    public StringProperty name = new SimpleStringProperty();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    private final ComponentsContext componentsContext;
    private final CanvaComponent canva;

    boolean isDeleted = false;

    public String dataTableVariableName;

    public ColumnComponent(ComponentsContext componentsContext, CanvaComponent canva) {
        // Configuração inicial como VBox
        setSpacing(5);
        setStyle("-fx-background-color:red;");

        setAlignment(Pos.CENTER);
        setPrefWidth(Region.USE_COMPUTED_SIZE); // Permite que o VBox se ajuste ao conteúdo/pai

        setId(String.valueOf(System.currentTimeMillis()));

        this.componentsContext = componentsContext;
        this.canva = canva;

        getChildren().add(new TextComponent("Im new here", componentsContext, canva));
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

        valuesOfVariableName.addAll(Commons.getValuesFromVariablename(data.dataTableVariableName()));

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
    private final List<ViewContract<?>> localComponents = new ArrayList<>();

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
        ViewContract<?> existingNode = searchNode(currentChildId);

        var copies = new ArrayList<Node>();
        boolean nodeOriginalRemoved = false;
        for (int i = 0; i < amount; i++) {
            ViewContract<ComponentData> newNodeWrapper = (ViewContract<ComponentData>) cloneExistingNode((ViewContract<ComponentData>) existingNode, i);

            // Cria uma NOVA cópia do nó a partir dos dados originais
            // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
            var node = newNodeWrapper.getCurrentNode();
            node.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

            if (!nodeOriginalRemoved) {
                localComponents.add(existingNode);
                componentsContext.removeComponentFromAllPlaces(existingNode, canva);
            }

            // Aplicamos o ID da cópia
            copies.add(node);

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

        ViewContract<?> existingNode = searchNode(emptyComponentId);
        ViewContract<?> newNodeWrapper = cloneExistingNode((ViewContract<ComponentData>) existingNode, -1);

        //aqui eu posso remover ele do header e do canva
        if (newNodeWrapper != null) {
            // Cria uma NOVA cópia do nó a partir dos dados originais
            // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
            var node = newNodeWrapper.getCurrentNode();
            node.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

            //adiciona em cache local e remove do contexto
            localComponents.add(existingNode);
            // Remove o nó de seu pai anterior e adiciona
            componentsContext.removeComponentFromAllPlaces(existingNode, canva);
            getChildren().add(node);
        }


    }

    private ViewContract<? extends ComponentData> cloneExistingNode(ViewContract<ComponentData> existingNode, int currentIndex) {
        var originalData = existingNode.getData();
        var type = originalData.type();

        if (type.equalsIgnoreCase(englishBase.button())) {
            var newNodeWrapper = new ButtonComponent(componentsContext, canva);
            newNodeWrapper.applyData((ButtonComponentData) originalData);

            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }

            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.image())) {
            var newNodeWrapper = new ImageComponent(componentsContext, canva);
            newNodeWrapper.applyData((ImageComponentData) originalData);
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            var newNodeWrapper = new InputComponent(componentsContext, canva);
            newNodeWrapper.applyData((InputComponentData) originalData);
            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }
            return newNodeWrapper;
        } else if (type.equalsIgnoreCase(englishBase.text())) {
            var newNodeWrapper = new TextComponent(componentsContext, canva);
            newNodeWrapper.applyData((TextComponentData) originalData);
            if (currentIndex != -1 && !valuesOfVariableName.isEmpty()) {
                final var currentText = newNodeWrapper.getText();
                newNodeWrapper.setText(currentText.replace("${boom}", valuesOfVariableName.get(currentIndex)));
            }
            return newNodeWrapper;
        } else {
            var newNodeWrapper = new CustomComponent(componentsContext, canva);
            newNodeWrapper.applyData((CustomComponentData) originalData);
            return newNodeWrapper;
        }
    }

    private ViewContract<?> searchNode(String emptyComponentId) {
        // Busca o nó original pelo ID e faz a DEEP COPY
        var op = componentsContext.SearchNodeById(emptyComponentId);
        ViewContract<?> existingNode = null;

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
                new ChildHandlerComponent("Child component:", this, currentChildIdState, componentsContext),
                new ItemsAmountPreviewComponent(this),
                new ChildHandlerComponent("Component (if empty):", this, onEmptyComponentState, componentsContext),
                Components.LabelWithComboBox("Data list", this, "data-list"),
                new ButtonRemoverComponent(this, componentsContext)
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
                Components.LabelWithTextContent("Variable name", name.get(), v -> name.set(v)));
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

        valuesOfVariableName.addAll(Commons.getValuesFromVariablename(dataTableVariableName));
        recreateChildren();
    }

}
