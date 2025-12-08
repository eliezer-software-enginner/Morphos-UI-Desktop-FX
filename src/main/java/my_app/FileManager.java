package my_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import my_app.contexts.TranslationContext;
import my_app.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {


    // FileManager.java

    /**
     * Remove uma tela específica do projeto usando seu ID e salva as mudanças no disco.
     *
     * @param screenId O ID (UUID) da tela a ser excluída.
     */
    public static void deleteScreenFromProject(String screenId) {
        if (screenId == null || screenId.isEmpty()) {
            throw new IllegalArgumentException("O ID da tela não pode ser nulo ou vazio para a exclusão.");
        }

        try {
            final var projectData = getProjectData();
            // Assumimos que projectData.screens() retorna uma lista modificável.
            var screens = projectData.screens();

            // 1. Encontra e remove a tela pelo ID usando Streams.
            boolean removed = screens.removeIf(screen -> screen.screen_id.equals(screenId));

            if (removed) {
                // 2. Persiste a mudança no arquivo JSON.
                final var prefsData = getPrefsData();
                writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));
                IO.println("Project updated successfully: Screen with ID " + screenId + " was removed.");

                // Opcional: Se você tem um mecanismo de cache interno, você o invalidaria aqui.

            } else {
                IO.println("Aviso: Tela com ID " + screenId + " não encontrada para exclusão.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar tela no projeto: " + e.getMessage());
        }
    }

    public static void addScreenToProjectAndSave(StateJson_v3 newScreen) {
        if (newScreen.screen_id == null || newScreen.screen_id.isEmpty()) {
            throw new IllegalArgumentException("Nova tela deve ter um 'screen_id'.");
        }

        try {
            final var projectData = getProjectData();
            var screens = projectData.screens();

            // Adiciona a nova tela no final da lista
            screens.add(newScreen);

            // Persiste a mudança no arquivo
            final var prefsData = getPrefsData();
            writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));
            IO.println("Project updated successfully: New screen added (ID: " + newScreen.screen_id + ")");

        } catch (IOException e) {
            throw new RuntimeException("Error adding new screen to project: " + e.getMessage());
        }
    }

    public static void updateScreen(StateJson_v3 currentCanvaScreen) {
        if (currentCanvaScreen.screen_id == null || currentCanvaScreen.screen_id.isEmpty()) {
            throw new IllegalArgumentException("currentCanvaScreen deve ter um 'screen_id' definido para a atualização.");
        }

        try {
            final var projectData = getProjectData();
            // **IMPORTANTE:** Assumimos que 'projectData.screens()' retorna uma List que suporta
            // modificações (ex: ArrayList). Se for imutável, você precisará criar uma cópia mutável.
            var screens = projectData.screens();

            // Variável para armazenar o índice da tela que será atualizada
            int foundIndex = -1;

            // 1. Encontrar o índice da tela existente
            for (int i = 0; i < screens.size(); i++) {
                final var existingScreen = screens.get(i);

                // Se o ID da tela atual for igual ao ID de uma tela existente...
                if (currentCanvaScreen.screen_id.equals(existingScreen.screen_id)) {
                    foundIndex = i;
                    break; // Encontrou, pode sair do loop
                }
            }

            if (foundIndex != -1) {
                // 2. Se a tela foi encontrada, SUBSTITUA-A (mantendo o índice)
                screens.set(foundIndex, currentCanvaScreen);
                IO.println("Screen updated at index " + foundIndex + " (ID: " + currentCanvaScreen.screen_id + ")");
            } else {
                // 3. Se a tela NÃO foi encontrada, é uma nova tela, então adicione-a no final.
                screens.add(currentCanvaScreen);
                IO.println("New screen added (ID: " + currentCanvaScreen.screen_id + ")");
            }

            final var prefsData = getPrefsData();
            // Assumindo que 'writeDataAsJsonInFileInDisc' salva a lista 'screens' atualizada dentro de 'projectData'
            writeDataAsJsonInFileInDisc(projectData, new File(prefsData.last_project_saved_path()));

        } catch (IOException e) {
            throw new RuntimeException("Error updating project: " + e.getMessage());
        }
    }

    public static void saveProject(
            //StateJson_v2 currentCanvaScreen,
            String name, File file) {

        try {
            var project = new Projectv2(name, new TableData(List.of()), List.of());
            writeDataAsJsonInFileInDisc(project, file);
            IO.println("project was saved");

            saveDataInPrefs(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Projectv2 getProjectData() {
        try {
            final var prefsData = getPrefsData();
            final var projectAbsolutePath = prefsData.last_project_saved_path();

            final var om = new ObjectMapper();
            return om.readValue(new File(projectAbsolutePath), Projectv2.class);
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

    public static PrefsDatav2 loadDataInPrefsv2() {
        var prefsFile = getPrefsFile();

        var om = new ObjectMapper();

        try {
            return om.readValue(prefsFile.toFile(), PrefsDatav2.class);
            //final var path = prefsData.last_project_saved_path();
            //return path == null || path.isBlank() ? null : new File(path);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar prefs.json", e);
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


    private static PrefsDatav2 getPrefsData() {
        try {
            final var prefsFile = getPrefsFile();
            final var om = new ObjectMapper();
            return om.readValue(prefsFile.toFile(), PrefsDatav2.class);
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
            var screens = projectData.screens();

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

    public static List<String> getVariableNamesInDataTable() {
        // home/eliezer/.local/share/morphos_desktop_fx/Teste.json
        try {
            var proj = getProjectData();

            var list = new ArrayList<String>();
            for (var primitiveList : proj.tableData().primitiveDataList()) {
                list.add(primitiveList.variableName());
            }
            //TODO faltou lista complexa
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<String> getValuesFromVariableName(String variableName) {

        try {
            var proj = getProjectData();
            var list = new ArrayList<String>();
            for (var primitiveList : proj.tableData().primitiveDataList()) {
                if (primitiveList.variableName().equals(variableName)) {
                    list.addAll(primitiveList.values());
                }
            }
            //TODO faltou lista complexa
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // FileManager.java (Adicione estes métodos)

    /**
     * Atualiza o PrefsData para definir o último projeto ativo e adiciona o caminho
     * à lista de projetos recentes, se ainda não estiver lá.
     */
    // FileManager.java
    private static void updateRecents(String absolutePathOfCurrentProject) {
        var prefsFile = getPrefsFile();

        // 1. Carrega os dados atuais
        PrefsDatav2 currentPrefs;
        try {
            currentPrefs = loadDataInPrefsv2();
        } catch (RuntimeException e) {
            // Se a carga falhar (arquivo não existe), usa um padrão com a lista vazia
            currentPrefs = new PrefsDatav2(
                    null,
                    TranslationContext.instance().currentLanguage(),
                    List.of() // Garante uma lista vazia no padrão
            );
        }

        // A CORREÇÃO ESTÁ AQUI: Verifica se a lista é nula antes de passá-la para o ArrayList.
        List<String> pathsToCopy = currentPrefs.recent_projects_paths() != null
                ? currentPrefs.recent_projects_paths()
                : List.of();

        var recents = new ArrayList<>(pathsToCopy); // Agora pathsToCopy nunca será nulo

        // ... (restante da lógica de ordenação e limite) ...

        // 2. Garante que o projeto atual esteja no topo (ou o adiciona)
        recents.remove(absolutePathOfCurrentProject);
        recents.addFirst(absolutePathOfCurrentProject);

        // 3. Limita o tamanho da lista (ex: 10 projetos)
        if (recents.size() > 10) {
            recents.removeLast();
        }

        // 4. Cria o novo PrefsData e salva
        var newPrefs = new PrefsDatav2(absolutePathOfCurrentProject, currentPrefs.language(), recents);

        try {
            writeDataAsJsonInFileInDisc(newPrefs, prefsFile.toFile());
            IO.println("Saved prefs json at: " + prefsFile.toFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 5. Novo método para salvar e atualizar a lista de recentes (substitui o antigo saveProject)
    public static void saveProjectAndAddToRecents(String name, File file) {
        try {
            var project = new Projectv2(name, new TableData(List.of()), List.of());
            writeDataAsJsonInFileInDisc(project, file);
            IO.println("project was saved");

            // NOVO: Atualiza a lista de recentes
            updateRecents(file.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 6. Novo método para apenas definir um projeto como ativo (para cliques na lista de recentes)
    public static void setLastProject(String absolutePathOfCurrentProject) {
        updateRecents(absolutePathOfCurrentProject);
        IO.println("Project set as active: " + absolutePathOfCurrentProject);
    }
}
