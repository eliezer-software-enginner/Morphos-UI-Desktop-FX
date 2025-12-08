package my_app.screens.Home.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import my_app.data.StateJson_v3;
import my_app.screens.Home.HomeViewModel;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ScreenTab extends VBox {

    private final StateJson_v3 screen;
    private final HomeViewModel viewModel;
    private final HBox contentContainer;
    private final Rectangle indicator; // O "chão azul"

    public ScreenTab(StateJson_v3 screen, HomeViewModel viewModel) {
        this.screen = screen;
        this.viewModel = viewModel;

        // 1. Container do conteúdo (Nome + Botão Fechar)
        Label nameLabel = new Label(screen.name);
        nameLabel.getStyleClass().add("screen-tab-content");

        Button closeButton = new Button();

        //  IconDatabase.ICONS.get().get()
        closeButton.setGraphic(new FontIcon(Feather.X));
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

        // 3. Adiciona o conteúdo e o indicador
        this.getChildren().addAll(contentContainer, indicator);
        this.getStyleClass().add("tab-container");
        this.setSpacing(0); // Garante que o indicador fique colado no conteúdo

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

        // Atualiza o estilo do conteúdo (cores e fonte)
        contentContainer.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), isSelected);

        // Atualiza o estilo do indicador (o "chão azul")
        indicator.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), isSelected);
    }
}