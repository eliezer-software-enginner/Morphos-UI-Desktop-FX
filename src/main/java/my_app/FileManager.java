package my_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.PrefsData;
import my_app.data.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileManager {

    public static void saveNewProject(
            //StateJson_v2 currentCanvaScreen,
            String name, File file) {

        try {
            var project = new Project(name, new Commons.TableData(List.of()), List.of());
            writeDataAsJsonInFileInDisc(project, file);
            IO.println("project was saved");

            saveDataInPrefs(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void saveDataInPrefs(String absolutePathOfCurrentProject) {
        var prefsFile = getPrefsFile();

        var defaultPrefs = new PrefsData(absolutePathOfCurrentProject, TranslationContext.instance().currentLanguage());
        try {
            writeDataAsJsonInFileInDisc(defaultPrefs, prefsFile.toFile());
            IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static PrefsData loadDataInPrefs() {
        var prefsFile = getPrefsFile();

        var om = new ObjectMapper();

        try {
            return om.readValue(prefsFile.toFile(), PrefsData.class);
            //final var path = prefsData.last_project_saved_path();
            //return path == null || path.isBlank() ? null : new File(path);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar prefs.json", e);
        }
    }

    private static Path getPrefsFile() {
        return morphosPathInFileSystem().resolve("prefs.json");
    }

    public static void writeDataAsJsonInFileInDisc(Object obj, File file) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.writeValue(file, obj);
    }


    private static Path morphosPathInFileSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String appDataAbsolutePath;

        if (os.contains("win")) {
            // Windows
            String appData = System.getenv("LOCALAPPDATA");
            if (appData == null) {
                appData = userHome + "\\AppData\\Local";
            }
            appDataAbsolutePath = appData;
        } else if (os.contains("mac")) {
            // macOS
            appDataAbsolutePath = userHome + "/Library/Application Support";
        } else {
            // Linux e outros
            appDataAbsolutePath = userHome + "/.local/share";
        }
        return Path.of(appDataAbsolutePath).resolve(Commons.AppNameAtAppData);
    }
}
