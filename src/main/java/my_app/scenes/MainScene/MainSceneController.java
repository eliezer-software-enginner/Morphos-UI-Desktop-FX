package my_app.scenes.MainScene;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.scenes.SettingsScene;
import my_app.screens.Home.Home;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static my_app.data.Commons.loadPrefs;

public class MainSceneController {
    ComponentsContext componentsContext;

    public void handleClickMenuSettings(Stage stage) {
        new SettingsScene().show();
    }

    public record PrefsData(String last_project_saved_path, String language) {
    }

    public MainSceneController(ComponentsContext componentsContext) {
        this.componentsContext = componentsContext;
    }

    private File uiJsonFile;

    public void handleClickLoad(Home home, Stage stage) {
        var fc = new FileChooser();

        fc.setTitle("open selected project");
        fc.getExtensionFilters().add(
                new ExtensionFilter("ui extension", "*.json"));

        var uiFile = fc.showOpenDialog(stage);
        if (uiFile != null) {
            uiJsonFile = uiFile;
            componentsContext.loadJsonState(uiFile, home.canva, stage);
        }

    }

    public void loadSceneFromJsonFile(Home home, Stage stage) {
        try {
            uiJsonFile = loadUiFileFromAppData();
            componentsContext.loadJsonState(uiJsonFile, home.canva, stage);
        } catch (Exception e) {
            e.printStackTrace();
            componentsContext.loadJsonState(null, home.canva, stage);
        }
    }

    public void handleSave(Home home, Stage stage) {
        // if (uiJsonFile == null) {
        // handleSaveAs(home, stage);
        // return;
        // }

        updateUiJsonFilePathOnAppData(uiJsonFile);

        componentsContext.saveStateInJsonFile_v2(uiJsonFile, home.canva);
    }

    private File loadUiFileFromAppData() {
        String appData = loadPrefs();

        var appFolder = new File(appData, Commons.AppNameAtAppData);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        var prefsJsonFile = new File(appFolder, "prefs.json");

        if (!prefsJsonFile.exists()) {
            // cria arquivo padrão na primeira execução
            var defaultPrefs = new PrefsData("", TranslationContext.instance().currentLanguage());
            Commons.WriteJsonInDisc(prefsJsonFile, defaultPrefs);
            return null; // ainda não há projeto salvo
        }

        try (var stream = new FileInputStream(prefsJsonFile)) {
            var om = new ObjectMapper();
            final var path = om.readValue(stream, PrefsData.class).last_project_saved_path;
            return path == null || path.isBlank() ? null : new File(path);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível carregar prefs.json", e);
        }
    }


    public void handleSaveAs(Home home, Stage stage) {
        home.leftSide.removeError();

        var fc = new FileChooser();

        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName("ui.json");

        try {

            var file = fc.showSaveDialog(stage);
            if (file != null) {
                // json bening saved on specfif file
                componentsContext.saveStateInJsonFile_v2(file, home.canva);

                //saving also the prefs
                //check if file exists
                String appData = loadPrefs();
                var prefsFile = Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");

                var defaultPrefs = new PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
                Commons.WriteJsonInDisc(prefsFile.toFile(), defaultPrefs);

                IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());

            }


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private void updateUiJsonFilePathOnAppData(File file) {

        String appData = loadPrefs();

        var appFolder = new File(appData, Commons.AppNameAtAppData);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        var fileInCurrentDirectory = new File(appFolder, "prefs.json");

        var pref = new PrefsData(file.getAbsolutePath(), TranslationContext.instance().currentLanguage());
        Commons.WriteJsonInDisc(fileInCurrentDirectory, pref);
    }

//    private String loadPrefs() {
//        String appData = System.getenv("LOCALAPPDATA"); // C:\Users\<user>\AppData\Local
//        if (appData == null) {
//            appData = System.getProperty("user.home") + "\\AppData\\Local";
//        }
//        return appData;
//    }


    public void handleBecomeContributor() {
        // https://buymeacoffee.com/plantfall

        try {
            Desktop.getDesktop().browse(
                    new URI("https://buymeacoffee.com/plantfall"));
        } catch (Exception e) {
            throw new RuntimeException("Was not possible to go to donation");
        }
    }

}
