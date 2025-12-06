package my_app.screens.ShowCodeScreen;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import toolkit.Component;

import java.util.List;

public class ShowCodeScreen extends VBox {
    private final TranslationContext.Translation translation = TranslationContext.instance().get();
    private final ShowCodeController controller;

    // por enquanto só os node do canva
    // mas adiante expandir para os componentes gerais também pra
    // ver como vou encaix-alos no codigo gerado
    public ShowCodeScreen(ComponentsContext mainComponentContext, CanvaComponent canvaComponent) {
        this.controller = new ShowCodeController(mainComponentContext);

        String importsContent = controller.createImports();
        String codeContent = controller.createRestOfCode(canvaComponent);
        List<String> customComponentsContent = controller.createComponentsForPreview(canvaComponent.getChildren());

        VBox importsColumnContent = columnItem(importsContent, translation.imports());
        getChildren().add(importsColumnContent);

        VBox.setMargin(importsColumnContent, new Insets(0, 0, 20, 0));

        VBox codeColumnContent = columnItem(codeContent, translation.codeContent());
        getChildren().add(codeColumnContent);

        for (String text : customComponentsContent) {
            VBox.setMargin(importsColumnContent, new Insets(0, 0, 20, 0));

            VBox customComponentsColumnContent = columnItem(text, translation.codeContentOfCustomComponent());
            getChildren().add(customComponentsColumnContent);
        }

        setSpacing(10);
        setStyle("-fx-padding: 20; -fx-alignment: center;");

        getStyleClass().add("background-color");
    }

    @Component
    VBox columnItem(String content, String title) {
        Text titleText = new Text(title);
        VBox.setMargin(titleText, new Insets(0, 0, 10, 0));

        // TextArea permite seleção e cópia
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false); // não deixa editar
        textArea.setWrapText(true); // quebra de linha automática

        // ScrollPane não é necessário, o TextArea já tem barra de rolagem embutida
        textArea.setPrefHeight(200);

        titleText.setStyle("-fx-fill:white;-fx-font-size:18px;");

        textArea.setStyle(
                "-fx-control-inner-background:#1E1E1E;" + // fundo estilo editor
                        "-fx-font-family:Consolas, monospace;" + // fonte de código
                        "-fx-highlight-fill:#264F78;" + // cor do highlight
                        "-fx-highlight-text-fill:white;" +
                        "-fx-text-fill:white;" // cor do texto
        );

        VBox column = new VBox(titleText, textArea);
        column.setSpacing(5);

        ScaleTransition st = new ScaleTransition(Duration.millis(600));
        st.setNode(textArea);

        st.setFromX(0.7);
        st.setFromY(0.7);

        st.setToX(1);
        st.setToY(1);

        st.setAutoReverse(true);
        st.setCycleCount(1);

        st.play();

        return column;
    }
}
