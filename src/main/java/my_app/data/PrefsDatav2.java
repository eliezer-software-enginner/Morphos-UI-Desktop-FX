package my_app.data;

import java.util.List;

public record PrefsDatav2(String last_project_saved_path, String language, List<String> recent_projects_paths) {

    // Construtor auxiliar para facilitar a criação quando a lista está vazia
    public PrefsDatav2(String last_project_saved_path, String language) {
        this(last_project_saved_path, language, List.of());
    }
}