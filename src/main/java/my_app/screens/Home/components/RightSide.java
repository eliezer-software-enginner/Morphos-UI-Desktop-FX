package my_app.screens.Home.components;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import my_app.components.NodeWrapper;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.contexts.ComponentsContext;
import my_app.contexts.ComponentsContext.SelectedComponent;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.ViewContract;
import my_app.themes.Typography;
import toolkit.Component;

import static my_app.components.shared.UiComponents.ButtonPrimary;
import static my_app.components.shared.UiComponents.ButtonSecondary;

public class RightSide extends VBox {
    private TranslationContext.Translation translation = TranslationContext.instance().get();
    final double width = 350;
    // 1. ALTERADO: Tipo da propriedade é agora SelectedComponent
    final ObjectProperty<SelectedComponent> selectedComponentProperty;

    @Component
    Button btnAppearence = ButtonPrimary(translation.appearance());

    @Component
    Button btnLayout = ButtonSecondary(translation.layout());

    @Component
    Button btnOtherSettings = ButtonSecondary(translation.otherSettings());
    @Component
    HBox top = new HBox(5, btnAppearence, btnLayout, btnOtherSettings);
    @Component
    HBox topWrapper = new HBox(top); // wrapper só para não se esticar
    @Component
    Label title = Typography.subtitle("");
    @Component
    Label NoContentText = Typography.caption(translation.noComponentSelected());
    @Component
    private final VBox dynamicContainer; // container que será substituído

    IntegerProperty optionSelected = new SimpleIntegerProperty(1);

    public RightSide(ComponentsContext componentsContext, CanvaComponentV2 canva) {
        // 1. ALTERADO: Atribui a propriedade com o tipo correto
        ObjectProperty<SelectedComponent> selectedCompProp = componentsContext.nodeSelected;

        this.selectedComponentProperty = selectedCompProp; // Renomeado para clareza

        btnAppearence.setOnAction(_ -> optionSelected.set(1));
        btnLayout.setOnAction(_ -> optionSelected.set(2));
        btnOtherSettings.setOnAction(_ -> optionSelected.set(3));

        getChildren().add(topWrapper);
        getChildren().add(title);

        // ---- Container dinâmico (será trocado conforme o node selecionado) ----
        dynamicContainer = new VBox();

        getChildren().add(dynamicContainer);

        var spacer = new Region();
        spacer.setPrefHeight(10);
        getChildren().add(spacer);

        // mount
        mount(canva);

        // Atualiza UI quando muda de seleção

        // quando muda o node
        optionSelected.addListener((_, _, _) -> mount(canva));

        // 2. ALTERADO: Listener agora recebe SelectedComponent
        selectedComponentProperty.addListener((_, _, newComp) -> {
            // Extrai o Node do SelectedComponent. Será null se a seleção for limpa.
            Node newNode = (newComp != null) ? newComp.node() : null;

            if (newNode instanceof ViewContract renderable) {
                NodeWrapper nw = new NodeWrapper(renderable);
                nw.renderRightSideContainer(dynamicContainer, optionSelected, canva);
            } else {
                // Se newNode for null (desseleção) ou não for ViewContract
                dynamicContainer.getChildren().setAll(NoContentText);
            }
        });

        config();
        style();
    }

    void mount(CanvaComponentV2 canva) {
        SelectedComponent currentSelectedComp = selectedComponentProperty.get();
        Node currentNode = (currentSelectedComp != null) ? currentSelectedComp.node() : null;

        int opselected = optionSelected.get();

        if (opselected == 1) title.setText(translation.appearanceSettings());
        else if (opselected == 2) title.setText(translation.layoutSettings());
        else title.setText(translation.otherSettings());


        if (currentNode instanceof ViewContract renderable) {
            NodeWrapper nw = new NodeWrapper(renderable);
            nw.renderRightSideContainer(dynamicContainer, optionSelected, canva);
        } else {
            // Garante que o container esteja limpo se nada estiver selecionado ao montar
            Label desc = Typography.caption(translation.selectComponentToViewSettings());
            desc.setWrapText(true);

            dynamicContainer.getChildren().setAll(desc);
        }
    }

    void config() {
        HBox.setHgrow(top, Priority.NEVER);
        top.setMaxWidth(Region.USE_COMPUTED_SIZE); // largura baseada nos filhos
        setPrefWidth(width);
        setMaxHeight(Double.MAX_VALUE);

        var mediumWidth = Commons.ScreensSize.MEDIUM.width;
        var largeWidth = Commons.ScreensSize.LARGE.width;//1280
        var fullscreenWidth = Commons.ScreensSize.FULL.width;//1920

        Platform.runLater(() -> {
            Scene scene = getScene();
            if (scene != null) {
                scene.widthProperty().addListener((_, _, newW) -> {
                    var v = newW.doubleValue();
                    IO.println("width: " + v);

                    if (v <= mediumWidth) {
                        this.setPrefWidth(200);
                    }

                    if (v > mediumWidth && v <= largeWidth) {
                        this.setPrefWidth(width);
                    }

                    if (v > largeWidth && v <= 1400) {
                        this.setPrefWidth(390);
                    }

                    if (v > 1400 && v <= fullscreenWidth) {
                        this.setPrefWidth(410);
                    }

                    if (v > fullscreenWidth) {
                        this.setPrefWidth(500);
                    }

                    // double novaLargura = Math.max(250, newW.doubleValue() * 0.2);

                    // this.setPrefWidth(novaLargura);
                });
            }
        });
    }

    void style() {
        getStyleClass().add("background-color");
        setPadding(new Insets(15));
        setSpacing(5);
    }
}
