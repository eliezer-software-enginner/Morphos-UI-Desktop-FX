package my_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import my_app.contexts.TranslationContext;
import my_app.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileManager {

    public static void updateProject(StateJson_v2 currentCanvaScreen) {
        // ... (Verificações de ID)
        if (currentCanvaScreen.screen_id == null || currentCanvaScreen.screen_id.isEmpty()) {
            // Lide com erro: A tela atual deve ter um ID para ser atualizada.
            throw new IllegalArgumentException("currentCanvaScreen deve ter um 'screen_id' definido para a atualização.");
        }

        try {
            final var projectData = getProjectData();
            List<StateJson_v2> screens = projectData.screens();

            boolean updated = false;

            // Itera para encontrar e remover a tela antiga
            for (int i = 0; i < screens.size(); i++) {
                StateJson_v2 existingScreen = screens.get(i);

                // Se o ID da tela atual for igual ao ID de uma tela existente...
                if (currentCanvaScreen.screen_id.equals(existingScreen.screen_id)) {
                    // 1. Remove a versão antiga
                    screens.remove(i);

                    // 2. Adiciona a nova versão atualizada
                    screens.add(currentCanvaScreen);
                    updated = true;
                    break;
                }
            }

            // Se não encontrou uma tela existente (é uma tela nova), apenas a adiciona.
            if (!updated) {
                screens.add(currentCanvaScreen);
            }

            final var prefsData = getPrefsData();
            writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));
            IO.println("Project updated successfully (screen ID: " + currentCanvaScreen.screen_id + ")");

        } catch (IOException e) {
            throw new RuntimeException("Error updating project: " + e.getMessage());
        }
    }

    public static void saveProject(
            //StateJson_v2 currentCanvaScreen,
            String name, File file) {

        try {
            var project = new Project(name, new TableData(List.of()), List.of());
            writeDataAsJsonInFileInDisc(project, file);
            IO.println("project was saved");

            saveDataInPrefs(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Project getProjectData() {
        try {
            final var prefsData = getPrefsData();
            final var projectAbsolutePath = prefsData.last_project_saved_path();

            final var om = new ObjectMapper();
            return om.readValue(new File(projectAbsolutePath), Project.class);
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

    private static PrefsData getPrefsData() {
        try {
            final var prefsFile = getPrefsFile();
            final var om = new ObjectMapper();
            return om.readValue(prefsFile.toFile(), PrefsData.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
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

    public static void updateScreenNameInProject(String screenId, String newName) {
        try {
            final var projectData = getProjectData();
            List<StateJson_v2> screens = projectData.screens();

            var screenOp = screens.stream().filter(it -> it.screen_id.equals(screenId))
                    .findFirst();

            screenOp.ifPresent(it -> {
                it.name = newName;
                IO.println("atualizou nome");
            });

            final var prefsData = getPrefsData();
            writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));
            IO.println("Project updated successfully (screen ID: " + screenId + ")");

        } catch (IOException e) {
            throw new RuntimeException("Error updating project: " + e.getMessage());
        }
    }

    public static void addPrimitiveDataInProject(PrimitiveData data) {
        try {
            var projectData = getProjectData();
            projectData.tableData().primitiveDataList().add(data);

            final var prefsData = getPrefsData();
            writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));
            IO.println("Project updated! primitive types were added");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
