package my_app.screens.Home.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import my_app.components.NodeWrapper;
import my_app.contexts.ComponentsContext;
import my_app.contexts.ComponentsContext.SelectedComponent;
import my_app.contexts.TranslationContext;
import my_app.data.ViewContract;
import my_app.themes.Typography;
import toolkit.theme.MaterialTheme;

import static my_app.components.shared.UiComponents.ButtonPrimary;
import static my_app.components.shared.UiComponents.ButtonSecondary;

public class RightSide extends VBox {
    private TranslationContext.Translation translation = TranslationContext.instance().get();
    final double width = 250;
    // 1. ALTERADO: Tipo da propriedade é agora SelectedComponent
    final ObjectProperty<SelectedComponent> selectedComponentProperty;

    Button btnAppearence = ButtonPrimary(translation.appearance());
    Button btnLayout = ButtonSecondary(translation.layout());
    HBox top = new HBox(btnAppearence, btnLayout);
    HBox topWrapper = new HBox(top); // wrapper só para não se esticar

    Label title = Typography.body("");
    Label NoContentText = Typography.caption(translation.noComponentSelected());

    private VBox dynamicContainer; // container que será substituído

    BooleanProperty appearenceIsSelected = new SimpleBooleanProperty(true);


    public RightSide(ComponentsContext componentsContext) {
        // 1. ALTERADO: Atribui a propriedade com o tipo correto
        ObjectProperty<SelectedComponent> selectedCompProp = componentsContext.nodeSelected;

        this.selectedComponentProperty = selectedCompProp; // Renomeado para clareza

        btnAppearence.setOnAction(_ -> appearenceIsSelected.set(true));
        btnLayout.setOnAction(_ -> appearenceIsSelected.set(false));

        getChildren().add(topWrapper);

        title.textProperty().bind(Bindings
                .createStringBinding(() -> appearenceIsSelected.get() ? translation.appearanceSettings() : translation.layoutSettings(),
                        appearenceIsSelected));

        getChildren().add(title);

        // ---- Container dinâmico (será trocado conforme o node selecionado) ----
        dynamicContainer = new VBox();

        getChildren().add(dynamicContainer);

        var spacer = new Region();
        spacer.setPrefHeight(10);
        getChildren().add(spacer);

        // mount
        mount();

        // Atualiza UI quando muda de seleção

        appearenceIsSelected.addListener((_, _, _) -> mount());
        // NodeWrapper

        // quando muda o node
        appearenceIsSelected.addListener((_, _, _) -> mount());

        // 2. ALTERADO: Listener agora recebe SelectedComponent
        selectedComponentProperty.addListener((_, _, newComp) -> {
            // Extrai o Node do SelectedComponent. Será null se a seleção for limpa.
            Node newNode = (newComp != null) ? newComp.node() : null;

            if (newNode instanceof ViewContract renderable) {
                NodeWrapper nw = new NodeWrapper(renderable);
                nw.renderRightSideContainer(dynamicContainer, appearenceIsSelected);
            } else {
                // Se newNode for null (desseleção) ou não for ViewContract
                dynamicContainer.getChildren().setAll(NoContentText);
            }
        });

        config();
    }

    void mount() {
        SelectedComponent currentSelectedComp = selectedComponentProperty.get();
        Node currentNode = (currentSelectedComp != null) ? currentSelectedComp.node() : null;

        if (currentNode instanceof ViewContract renderable) {
            NodeWrapper nw = new NodeWrapper(renderable);
            nw.renderRightSideContainer(dynamicContainer, appearenceIsSelected);
        } else {
            // Garante que o container esteja limpo se nada estiver selecionado ao montar
            Label desc = Typography.caption(translation.selectComponentToViewSettings());
            desc.setWrapText(true);

            dynamicContainer.getChildren().setAll(desc);
        }
    }

    void config() {

        top.setSpacing(0);

        HBox.setHgrow(top, Priority.NEVER);
        top.setMaxWidth(Region.USE_COMPUTED_SIZE); // largura baseada nos filhos

        setMaxHeight(Double.MAX_VALUE);
        setBackground(new Background(
                new BackgroundFill(MaterialTheme.getInstance().getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        setPrefWidth(width);
        setMinWidth(width);
        setMaxWidth(width);

        setPadding(new Insets(15));
    }
}
