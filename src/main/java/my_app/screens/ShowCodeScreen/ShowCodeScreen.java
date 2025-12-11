package my_app.screens.ShowCodeScreen;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;
import toolkit.Toast;

import java.util.List;

public class ShowCodeScreen extends StackPane {
    private final TranslationContext.Translation translation = TranslationContext.instance().get();
    private final ShowCodeController controller;

    @Component
    Toast toast = new Toast();

    public ShowCodeScreen(HomeViewModel viewModel, CanvaComponentV2 canvaComponent) {
        this.controller = new ShowCodeController(viewModel);

        final VBox layout = new VBox();
        getChildren().addAll(layout, toast);

        // 1. Geração de Código
        String importsContent = controller.createImports();
        String codeContent = controller.createRestOfCode(canvaComponent);
        List<String> customComponentsContent = controller.createComponentsForPreview(canvaComponent.getChildren());

        // 2. Imports
        VBox importsColumnContent = columnItem(importsContent, translation.imports());
        VBox.setMargin(importsColumnContent, new Insets(0, 0, 20, 0));
        layout.getChildren().add(importsColumnContent);

        // 3. Código Principal da Screen
        VBox codeColumnContent = columnItem(codeContent, translation.codeContent());
        VBox.setMargin(codeColumnContent, new Insets(0, 0, 20, 0));
        layout.getChildren().add(codeColumnContent);

        // 4. Componentes Customizados
        for (String customComponentCode : customComponentsContent) {
            VBox customComponentsColumnContent = columnItem(customComponentCode, translation.codeContentOfCustomComponent());
            VBox.setMargin(customComponentsColumnContent, new Insets(0, 0, 20, 0));
            layout.getChildren().add(customComponentsColumnContent);
        }

        // 5. Estilo da Screen
        layout.setSpacing(10);
        // O alinhamento superior é ideal para VBoxes com conteúdo de rolagem
        setStyle("-fx-padding: 20; -fx-alignment: top-center;");
        getStyleClass().add("background-color");
    }

    @Component
    VBox columnItem(String content, String title) {
        Text titleText = new Text(title);
        VBox.setMargin(titleText, new Insets(0, 0, 10, 0));

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(false); // Mantido: Scroll horizontal é melhor para código

        // --- MUDANÇAS CHAVE PARA O CÁLCULO DE ALTURA DINÂMICA ---

        // 1. Calcula o número de linhas (mínimo de 10 por estética)
        long lineCount = content.lines().count();
        int rows = (int) Math.max(10, lineCount + 1);

        // 2. Define o número de linhas preferenciais. Isso faz o TextArea calcular
        // a altura necessária para o conteúdo (quebrando o limite de 200px fixo).
        textArea.setPrefRowCount(rows);

        // 3. Remove restrições de altura conflitantes
        textArea.setMinHeight(200);
        //VBox.setVgrow(textArea, Priority.ALWAYS); // Removido: Não é necessário para "size-to-content"

        // Garante que a altura máxima não seja um limite artificial, mas permita o cálculo baseado em rowCount.
        textArea.setMaxHeight(Double.MAX_VALUE);

        // -----------------------------------------------------------

        titleText.setStyle("-fx-fill:white;-fx-font-size:18px;");

        textArea.setStyle(
                "-fx-control-inner-background:#1E1E1E;" +
                        "-fx-font-family:Consolas, monospace;" +
                        "-fx-highlight-fill:#264F78;" +
                        "-fx-highlight-text-fill:white;" +
                        "-fx-text-fill:white;"
        );

        final var btnCopyToClipboard = Components.ButtonPrimary("Copy");
        var icon = FontIcon.of(Feather.COPY);
        icon.setIconSize(18);
        icon.setIconColor(Color.WHITE);

        btnCopyToClipboard.setGraphic(icon);
        btnCopyToClipboard.setOnMouseClicked(ev -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent c = new ClipboardContent();
            c.putString(content);
            clipboard.setContent(c);
            
            toast.show("Copied to clipboard!");
        });
        HBox rowHeader = new HBox(titleText, btnCopyToClipboard);

        VBox column = new VBox(rowHeader, textArea);
        column.setSpacing(5);

        return column;
    }
}