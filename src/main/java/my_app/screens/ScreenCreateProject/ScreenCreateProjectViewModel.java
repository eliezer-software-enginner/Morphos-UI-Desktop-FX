package my_app.screens.ScreenCreateProject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.screens.Home.HomeViewModel;
import my_app.themes.Typography;
import toolkit.Toast;

import java.nio.file.Path;

import static my_app.data.Commons.loadPrefs;

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
                FileManager.saveNewProject(text, file);

                //saving also the prefs
                //check if file exists
                String appData = loadPrefs();
                var prefsFile = Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");

                var defaultPrefs = new HomeViewModel.PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
                Commons.WriteJsonInDisc(prefsFile.toFile(), defaultPrefs);

                errorContainer.getChildren().clear();

                IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());
                this.toast.show("Project was created!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }
}
