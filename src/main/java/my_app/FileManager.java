package my_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import my_app.data.Commons;
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
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
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
