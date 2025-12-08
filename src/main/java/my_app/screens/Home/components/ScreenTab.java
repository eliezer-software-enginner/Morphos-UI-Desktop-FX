package my_app.screens.Home.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import my_app.data.StateJson_v3;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ScreenTab extends VBox {

    private final StateJson_v3 screen;
    private final HBox contentContainer;
    private final Rectangle indicator; // O "chão azul"

    ThemeManager themeManager = ThemeManager.Instance();

    public ScreenTab(StateJson_v3 screen, HomeViewModel viewModel) {
        this.screen = screen;

        // 1. Container do conteúdo (Nome + Botão Fechar)
        final var nameLabel = Typography.caption(screen.name);
        nameLabel.getStyleClass().add("screen-tab-content");

        Button closeButton = new Button();

        final var icon = new FontIcon(Feather.X);
        icon.setIconColor(themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);

        closeButton.setGraphic(icon);
        closeButton.getStyleClass().add("close-tab-button");

        closeButton.setOnAction(e -> {
            // Lógica de exclusão da tela (a ser implementada no FileManager)
            viewModel.deleteScreen(screen.screen_id);
            System.out.println("Excluir tela: " + screen.name);
            e.consume(); // Consome o clique para não acionar o clique da VBox pai
        });

        // Contêiner principal da Tab (Nome e Fechar)
        contentContainer = new HBox(5, nameLabel, closeButton); // Espaçamento entre nome e fechar
        contentContainer.setAlignment(Pos.CENTER_LEFT);
        contentContainer.getStyleClass().add("screen-tab-content");

        // 2. O Indicador (o "chão azul")
        indicator = new Rectangle();
        indicator.getStyleClass().add("screen-tab-indicator");
        // Adicione a regra para que o Region preencha a largura
        // CORREÇÃO ESSENCIAL: Vincular a largura do indicador à largura do ScreenTab (VBox)
        indicator.setWidth(100);
        indicator.widthProperty().bind(contentContainer.widthProperty()); // <<< CORREÇÃO

        // Para garantir que o VBox pai preencha a largura disponível (dentro do HBox screensTabs)

        // 3. Adiciona o conteúdo e o indicador
        this.getChildren().addAll(contentContainer, indicator);
        this.getStyleClass().add("tab-container");
        this.setSpacing(0); // Garante que o indicador fique colado no conteúdo

        this.widthProperty().addListener((_, _, v) -> IO.println(v.doubleValue()));

        indicator.setHeight(5);
        //indicator.setWidth(100);

        // 4. Lógica de Seleção
        this.setOnMouseClicked(e -> {
            // Mantém a lógica da ViewModel para carregar a tela
            viewModel.handleTabClicked(screen.screen_id);
        });

        // 5. Listener para atualizar o visual quando a seleção muda
        viewModel.currentScreenId.addListener((obs, oldId, newId) -> {
            updateSelectionStyle(newId);
        });

        // Aplica o estilo inicial (se for a primeira tela carregada)
        updateSelectionStyle(viewModel.currentScreenId.get());
    }

    private void updateSelectionStyle(String currentId) {
        boolean isSelected = screen.screen_id.equals(currentId);

        if (isSelected) {
            // Se selecionado: Aplica a classe selecionada em AMBOS
            contentContainer.getStyleClass().setAll("screen-tab-content", "screen-tab-content-selected");
            indicator.getStyleClass().setAll("screen-tab-indicator", "screen-tab-indicator-selected"); // Deve ter AMBAS classes
        } else {
            // Se não selecionado: Remove a classe selecionada em AMBOS
            contentContainer.getStyleClass().setAll("screen-tab-content");
            indicator.getStyleClass().setAll("screen-tab-indicator");
        }
    }
}