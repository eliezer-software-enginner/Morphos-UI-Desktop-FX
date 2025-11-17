package my_app.components;

import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
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

// ColumnItens.java
public class ColumnComponent extends VBox implements ViewContract<ColumnComponentData> {
    SimpleStringProperty currentChildIdState = new SimpleStringProperty("None");
    SimpleStringProperty onEmptyComponentState = new SimpleStringProperty("None"); // Novo padrão: "None"

    public SimpleIntegerProperty childrenAmountState = new SimpleIntegerProperty(3);
    public StringProperty name = new SimpleStringProperty();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    private final ComponentsContext componentsContext;
    private final CanvaComponent canva;

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
    public void applyData(ComponentData data) {
        var cast = (ColumnComponentData) data;
        // Limpa os filhos existentes antes de aplicar o novo estado
        getChildren().clear();

        this.setLayoutX(cast.x());
        this.setLayoutY(cast.y());

        this.setId(data.identification());

        String childId = cast.childId() == null ? "None" : cast.childId();
        String alternativeChildId = cast.alternativeChildId() == null ? "None" : cast.alternativeChildId();

        currentChildIdState.set(childId);
        onEmptyComponentState.set(alternativeChildId);
        childrenAmountState.set(cast.pref_child_amount_for_preview());

        // 3. Chamar a lógica centralizada (permanece igual)
        recreateChildren();
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

    TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

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
                if (existingNode instanceof ViewContract<?> existingView) {
                    var originalData = (ComponentData) existingView.getData();

                    var type = originalData.type();

                    ViewContract<?> nodeWrapper = null;

                    if (type.equalsIgnoreCase(englishBase.button())) {
                        nodeWrapper = new ButtonComponent(componentsContext, canva);
                        nodeWrapper.applyData(originalData);
                    } else if (type.equalsIgnoreCase(englishBase.image())) {
                        nodeWrapper = new ImageComponent(componentsContext, canva);
                        nodeWrapper.applyData(originalData);
                    } else if (type.equalsIgnoreCase(englishBase.input())) {
                        nodeWrapper = new InputComponent(componentsContext, canva);
                        nodeWrapper.applyData(originalData);
                    } else if (type.equalsIgnoreCase(englishBase.text())) {
                        nodeWrapper = new TextComponent(componentsContext, canva);
                        nodeWrapper.applyData(originalData);
                    } else if (type.equalsIgnoreCase(englishBase.component())) {
                        nodeWrapper = new CustomComponent(componentsContext, canva);
                        nodeWrapper.applyData(originalData);
                    }

                    //aqui eu posso remover ele do header e do canva
                    if (nodeWrapper != null) {
                        // Cria uma NOVA cópia do nó a partir dos dados originais
                        // ⚠️ PASSO CRUCIAL: Torna o placeholder transparente ao mouse
                        var node = nodeWrapper.getCurrentNode();
                        node.setMouseTransparent(true); // <-- ADICIONAR ESTA LINHA

                        // Remove o nó de seu pai anterior e adiciona
                        if (node.getParent() != null) {
                            ((Pane) node.getParent()).getChildren().remove(node);
                        }
                        getChildren().add(node);

                    }

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
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                new ChildHandlerComponent("Child component:", this, currentChildIdState, componentsContext),
                new ItemsAmountPreviewComponent(this),
                new ChildHandlerComponent("Component (if empty):", this, onEmptyComponentState, componentsContext),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext));
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva));
    }

    @Override
    public void otherSettings(Pane father, CanvaComponent canva) {
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
                childrenAmountState.get());
    }

}
