package my_app.screens.ShowCode;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.contexts.TranslationContext;

import java.util.List;

public class ShowCode {

    private final TranslationContext.Translation translation = TranslationContext.instance().get();
    private Stage stage;
    private ShowCodeController controller = new ShowCodeController();
    VBox root = new VBox();

    // por enquanto só os node do canva
    // mas adiante expandir para os componentes gerais também pra
    // ver como vou encaix-alos no codigo gerado
    public ShowCode(CanvaComponent canvaComponent) {
        stage = new Stage();
        stage.setTitle("Showing code");

        String importsContent = controller.createImports();
        String codeContent = controller.createRestOfCode(canvaComponent);
        List<String> customComponentsContent = controller.createComponentsForPreview(canvaComponent.getChildren());

        VBox importsColumnContent = columnItem(importsContent, translation.imports());
        root.getChildren().add(importsColumnContent);

        VBox.setMargin(importsColumnContent, new Insets(0, 0, 20, 0));

        VBox codeColumnContent = columnItem(codeContent,  translation.codeContent());
        root.getChildren().add(codeColumnContent);

        for (String text : customComponentsContent) {
            VBox.setMargin(importsColumnContent, new Insets(0, 0, 20, 0));

            VBox customComponentsColumnContent = columnItem(text, translation.codeContentOfCustomComponent());
            root.getChildren().add(customComponentsColumnContent);
        }

        root.setSpacing(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color:#39375B");

        Scene scene = new Scene(root, 500, 550);
        stage.setScene(scene);
    }

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

    public void abrir() {
        stage.show();
    }
}
