package my_app.screens.ScreenCreateProject;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.Typography;
import toolkit.Component;
import toolkit.Toast; // Mantemos o import porque a View ainda o renderiza

public class ScreenCreateProject extends VBox {

    // A View recebe a ViewModel no construtor.
    // O Toast deve ser um componente UI, gerenciado pela View.
    ScreenCreateProjectViewModel viewModel;
    Toast toast = new Toast();

    public ScreenCreateProject(Stage primaryStage) {
        // A ViewModel deve ser inicializada fora da View se a dependência for injetada,
        // mas por simplicidade, vamos criá-la aqui.
        this.viewModel = new ScreenCreateProjectViewModel(primaryStage);

        getChildren().addAll(
                Typography.title(Commons.AppName),
                Typography.title(Commons.AppVersion),
                createCard(), toast);

        setAlignment(Pos.CENTER);
        setSpacing(10);
        getStyleClass().add("background-color");
    }

    @Component
    public VBox createCard() {
        var title = Typography.subtitle("Criar novo projeto");
        var input = new TextField();
        var errorContainer = new VBox(); // Container de erros.

        var btn = Components.ButtonPrimary("Criar projeto");
        var root = new VBox(10, title, input, errorContainer, btn);

        input.getStyleClass().add("project-name-input");
        root.getStyleClass().add("card-create-project");

        // 1. Data Binding: Conecta o input de texto à propriedade da ViewModel.
        input.textProperty().bindBidirectional(this.viewModel.inputTextProperty);

        // 2. Event Listener: A View chama o comando na ViewModel.
        btn.setOnMouseClicked(ev -> this.viewModel.handleClickCreateProject());

        // 3. Listener Reativo: A View observa a propriedade de erro da VM e ATUALIZA a UI.
        this.viewModel.errorMessageProperty.addListener((obs, oldVal, newVal) -> {
            errorContainer.getChildren().clear();
            if (newVal != null && !newVal.isEmpty()) {
                errorContainer.getChildren().setAll(Typography.error(newVal));
            }
        });

        // 4. Listener para Toast: A View observa a propriedade de notificação da VM.
        this.viewModel.showToastProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                this.toast.show(newVal);
                // Limpa a propriedade para que possa ser acionada novamente.
                this.viewModel.showToastProperty.set(null);
            }
        });

        return root;
    }
}