package my_app.components.columnComponent;

import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.LayoutPositionComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.ChildHandlerComponent;
import my_app.components.shared.ItemsAmountPreviewComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.ColumnComponentData;
import my_app.data.Commons;
import my_app.data.ComponentData;
import my_app.data.ComponentFactory;
import my_app.data.ViewContract;

// ColumnItens.java
public class ColumnComponent extends VBox implements ViewContract<ColumnComponentData> {

    SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None"); // Novo padrão: "None"

    // Mantemos o currentState e currentChild para a barra lateral de propriedades
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(3);

    ComponentsContext componentsContext;

    public ColumnComponent(ComponentsContext componentsContext) {
        // Configuração inicial como VBox
        setSpacing(5);
        setStyle("-fx-background-color:red;");

        setAlignment(Pos.CENTER);
        setPrefWidth(Region.USE_COMPUTED_SIZE); // Permite que o VBox se ajuste ao conteúdo/pai

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
        this.componentsContext = componentsContext;
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

        // 3. Chamar a lógica centralizada (permanece igual)
        recreateChildren();

    }

    // -------------------------------------------------------------------
    // NOVO MÉTODO: Lógica Centralizada para Recriar os Filhos
    // -------------------------------------------------------------------
    public void recreateChildren() {
        int amount = childrenAmountState.get();

        // 1. Limpa todos os filhos existentes
        getChildren().clear();

        if (amount == 0) {
            // SE A QUANTIDADE FOR ZERO, exibe o componente de placeholder
            String emptyComponentId = onEmptyComponentState.get();

            if (emptyComponentId.equals("None") || emptyComponentId.isEmpty()) {
                // Não há placeholder para exibir
                return;
            }

            // Busca o nó original pelo ID e faz a DEEP COPY
            var op = componentsContext.SearchNodeById(emptyComponentId);

            op.ifPresent(existingNode -> {
                if (existingNode instanceof ViewContract existingView) {
                    ComponentData originalData = (ComponentData) existingView.getData();

                    // Cria uma NOVA cópia do nó a partir dos dados originais
                    Node emptyNode = ComponentFactory.createNodeFromData(originalData, componentsContext);
                    // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
                    emptyNode.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

                    // Remove o nó de seu pai anterior e adiciona
                    if (emptyNode.getParent() != null) {
                        ((Pane) emptyNode.getParent()).getChildren().remove(emptyNode);
                    }
                    getChildren().add(emptyNode);
                }
            });

            return; // Encerra a função, pois o placeholder foi adicionado
        }

        // 2. SE A QUANTIDADE FOR MAIOR QUE ZERO...
        String currentChildId = currentChildIdState.get();

        // Recriação de CustomComponents (DEEP COPY)
        var op = componentsContext.SearchNodeById(currentChildId);

        op.ifPresent(existingNode -> {
            // ** Universalização: Usamos ViewContract e a Fábrica **
            if (existingNode instanceof ViewContract existingView) {

                ComponentData originalData = (ComponentData) existingView.getData(); // Pega os dados originais

                var copies = new ArrayList<Node>();
                for (int i = 0; i < amount; i++) {
                    // Criamos uma nova cópia do nó a partir dos dados originais, só pra
                    // visualizacao
                    Node newCopy = ComponentFactory.createNodeFromData(originalData, componentsContext);
                    // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
                    newCopy.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

                    // Aplicamos o ID da cópia
                    copies.add(newCopy);
                }
                getChildren().addAll(copies);
            }
        });

    }

    @Override
    public void appearance(Pane father) {
        father.getChildren().setAll(
                new ChildHandlerComponent("Child component:", this, currentChildIdState),
                new ItemsAmountPreviewComponent(this),
                new ChildHandlerComponent("Component (if empty):", this, onEmptyComponentState),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father) {
        father.getChildren().setAll(
                new LayoutPositionComponent(currentState));
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
                childrenAmountState.get());
    }

}
