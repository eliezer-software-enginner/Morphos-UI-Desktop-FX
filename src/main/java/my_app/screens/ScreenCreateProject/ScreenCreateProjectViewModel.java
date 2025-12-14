package my_app.screens.ScreenCreateProject;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.scenes.AppScenes;

import java.util.ArrayList;
import java.util.List;

public class ScreenCreateProjectViewModel {

    // --- Propriedades de Estado e Reatividade ---
    public StringProperty inputTextProperty = new SimpleStringProperty("projeto-teste-v2");
    public StringProperty errorMessageProperty = new SimpleStringProperty(null);
    public StringProperty showToastProperty = new SimpleStringProperty(null);

    // NOVO: Lista de projetos recentes para a UI
    public final ObservableList<String> recentProjects = FXCollections.observableArrayList();
    public final SimpleListProperty<String> recentProjectsProperty = new SimpleListProperty<>(recentProjects);

    private final Stage stage;

    public ScreenCreateProjectViewModel(Stage primaryStage) {
        this.stage = primaryStage;
        loadRecentProjects();
        //loadRecentProjectsFake();
    }

    // --- Comandos e Lógica de Negócios ---

    private void loadRecentProjects() {
        try {
            final var prefsData = FileManager.loadDataInPrefs();

            // Adiciona a lista de caminhos. Filtra nulls para segurança.
            final var paths = prefsData.recent_projects_paths() != null
                    ? prefsData.recent_projects_paths()
                    : List.<String>of();

            IO.println(paths);

            recentProjects.setAll(paths);

        } catch (RuntimeException e) {
            // Ignoramos o erro de carregamento (por exemplo, arquivo prefs.json não existe ainda)
            System.err.println("Aviso: Não foi possível carregar projetos recentes: " + e.getMessage());
            recentProjects.clear();
        }
    }

    private void loadRecentProjectsFake() {
        try {
            List<String> paths = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                paths.add("caminho de teste: " + i);
            }

            recentProjects.setAll(paths);

        } catch (RuntimeException e) {
            // Ignoramos o erro de carregamento (por exemplo, arquivo prefs.json não existe ainda)
            System.err.println("Aviso: Não foi possível carregar projetos recentes: " + e.getMessage());
            recentProjects.clear();
        }
    }

    // NOVO: Comando para abrir um projeto existente (clique na lista)
    public void handleOpenExistingProject(String path) {
        try {
            // A lógica do FileManager deve ser atualizada para registrar o projeto como ativo
            FileManager.setLastProject(path);

            // Navegação
            AppScenes.SwapScene(stage, AppScenes.HomeScene(stage));
        } catch (Exception e) {
            errorMessageProperty.set("Erro ao abrir projeto: " + e.getMessage());
        }
    }

    private boolean validateInput(String text) {
        errorMessageProperty.set(null);
        if (text.isEmpty()) {
            errorMessageProperty.set("O nome do projeto está vazio!");
            return false;
        }
        if (text.length() < 5) {
            errorMessageProperty.set("O nome do projeto está muito curto!");
            return false;
        }
        return true;
    }

    public void handleClickCreateProject() {
        String text = inputTextProperty.get().trim();

        if (!validateInput(text)) {
            return;
        }

        errorMessageProperty.set(null);

        var fc = new FileChooser();
        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName(text + ".json");

        try {
            var file = fc.showSaveDialog(stage);
            if (file != null) {
                // A lógica do FileManager deve ser atualizada para adicionar este novo caminho à lista de recentes
                FileManager.saveProjectAndAddToRecents(text, file);

                showToastProperty.set("Project was created!");
                stage.setScene(AppScenes.HomeScene(stage));
            }
        } catch (Exception e) {
            errorMessageProperty.set("Erro ao salvar projeto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}