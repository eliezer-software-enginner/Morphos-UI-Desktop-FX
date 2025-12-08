package my_app.screens.ScreenCreateProject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.Typography;
import toolkit.Component;
import toolkit.Toast;

import java.io.File;

public class ScreenCreateProject extends VBox {

    ScreenCreateProjectViewModel viewModel;
    Toast toast = new Toast();

    public ScreenCreateProject(Stage primaryStage) {
        // A View inicializa a ViewModel
        this.viewModel = new ScreenCreateProjectViewModel(primaryStage);

        // Layout Principal: Centraliza e exibe o cabeçalho e os dois painéis
        getChildren().addAll(
                Typography.title(Commons.AppName),
                Typography.subtitle(Commons.AppVersion),
                new HBox(30, createRecentProjectsPane(), createNewProjectPane()), // Layout com dois painéis
                toast);

        setAlignment(Pos.CENTER);
        setSpacing(20);
        getStyleClass().add("background-color");
    }

    @Component
    private VBox createRecentProjectsPane() {
        var title = Typography.subtitle("Projetos Recentes");
        var projectListContainer = new VBox(5);

        // Listener reativo: A View observa a lista da VM e constrói a UI dinamicamente
        this.viewModel.recentProjectsProperty.addListener((obs, oldList, newList) -> {
            projectListContainer.getChildren().clear();

            if (newList.isEmpty()) {
                projectListContainer.getChildren().add(new Label("Nenhum projeto recente encontrado."));
                return;
            }

            for (String path : newList) {
                // Cria um botão/link para cada projeto
                Button projectLink = Components.ButtonPrimary(new File(path).getName());

                // Comando: Chama a função de abrir na VM
                projectLink.setOnAction(e -> this.viewModel.handleOpenExistingProject(path));

                projectListContainer.getChildren().add(projectLink);
            }
        });

        var root = new VBox(10, title, projectListContainer);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("card-create-project"); // Reutilizamos a classe CSS
        return root;
    }

    @Component
    private VBox createNewProjectPane() {
        var title = Typography.subtitle("Criar novo projeto");
        var input = new TextField();
        var errorContainer = new VBox();

        var btn = Components.ButtonPrimary("Criar projeto");
        var root = new VBox(10, title, input, errorContainer, btn);

        input.getStyleClass().add("project-name-input");
        root.getStyleClass().add("card-create-project");

        // Data Binding: Conecta o input de texto à propriedade da ViewModel.
        input.textProperty().bindBidirectional(this.viewModel.inputTextProperty);

        // Event Listener: A View chama o comando na ViewModel.
        btn.setOnMouseClicked(ev -> this.viewModel.handleClickCreateProject());

        // Listener Reativo: A View observa a propriedade de erro da VM.
        this.viewModel.errorMessageProperty.addListener((obs, oldVal, newVal) -> {
            errorContainer.getChildren().clear();
            if (newVal != null && !newVal.isEmpty()) {
                errorContainer.getChildren().setAll(Typography.error(newVal));
            }
        });

        // Listener para Toast: A View observa a propriedade de notificação da VM.
        this.viewModel.showToastProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                this.toast.show(newVal);
                this.viewModel.showToastProperty.set(null);
            }
        });

        return root;
    }
}