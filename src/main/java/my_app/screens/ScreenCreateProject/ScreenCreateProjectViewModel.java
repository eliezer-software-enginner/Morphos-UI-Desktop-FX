package my_app.screens.ScreenCreateProject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.contexts.ComponentsContext;
import my_app.scenes.AppScenes;
import my_app.themes.Typography;
import toolkit.Toast;

public class ScreenCreateProjectViewModel {
    private final Toast toast;
    public StringProperty inputTextProperty = new SimpleStringProperty("projeto-teste");
    Stage stage;

    public ScreenCreateProjectViewModel(Stage primaryStage, Toast toast) {
        this.stage = primaryStage;
        this.toast = toast;
    }

    public void handleClickCreateProject(VBox errorContainer) {
        String text = inputTextProperty.get().trim();

        if (text.isEmpty()) {
            errorContainer.getChildren().setAll(Typography.error("O nome do projeto está vazio!"));
            return;
        }
        if (text.length() < 5) {
            errorContainer.getChildren().setAll(Typography.error("O nome do projeto está muito curto!"));
            return;
        }

        var fc = new FileChooser();

        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName(text + ".json");

        try {
            var file = fc.showSaveDialog(stage);
            if (file != null) {
                FileManager.saveProject(text, file);
                errorContainer.getChildren().clear();
                this.toast.show("Project was created!");

                stage.setScene(AppScenes.HomeScene(stage));
            }
        } catch (Exception e) {
            errorContainer.getChildren().setAll(Typography.error(e.getMessage()));
        }
    }
}
