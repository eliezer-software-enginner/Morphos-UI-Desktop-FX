package my_app.screens.ScreenCreateProject;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.themes.Typography;
import toolkit.Component;
import toolkit.Toast;

public class ScreenCreateProject extends VBox {

    ScreenCreateProjectViewModel viewModel;

    Toast toast = new Toast();

    public ScreenCreateProject(ComponentsContext mainComponentsContext, Stage primaryStage) {
        this.viewModel = new ScreenCreateProjectViewModel(mainComponentsContext, primaryStage, toast);

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
        var errorContainer = new VBox();

        var btn = Components.ButtonPrimary("Criar projeto");
        var root = new VBox(10, title, input, errorContainer, btn);

        input.getStyleClass().add("project-name-input");

        root.getStyleClass().add("card-create-project");

        btn.setOnMouseClicked(ev -> this.viewModel.handleClickCreateProject(errorContainer));
        input.textProperty().bindBidirectional(this.viewModel.inputTextProperty);

        return root;
    }
}
