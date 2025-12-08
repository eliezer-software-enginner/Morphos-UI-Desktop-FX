package my_app.screens.ScreenCreateProject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.scenes.AppScenes;

public class ScreenCreateProjectViewModel {

    // Propriedades observáveis para que a View reaja ao estado da ViewModel.
    public StringProperty inputTextProperty = new SimpleStringProperty("projeto-teste");
    public StringProperty errorMessageProperty = new SimpleStringProperty(null); // Para exibir erros na UI
    public StringProperty showToastProperty = new SimpleStringProperty(null);    // Para notificar a View a exibir um Toast

    private final Stage stage; // Mantido para a lógica de navegação (mudança de cena)

    public ScreenCreateProjectViewModel(Stage primaryStage) {
        this.stage = primaryStage;
    }

    // 1. Validação (Lógica de Negócios)
    private boolean validateInput(String text) {
        errorMessageProperty.set(null); // Limpa erros anteriores

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

    // 2. Comando (Chamado pela View)
    public void handleClickCreateProject() {
        String text = inputTextProperty.get().trim();

        if (!validateInput(text)) {
            return; // Validação falhou, o errorMessageProperty já foi setado
        }

        // Limpar possíveis erros de validação antes da I/O
        errorMessageProperty.set(null);

        var fc = new FileChooser();

        fc.setTitle("save project as");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ui.json", "*.json"));
        fc.setInitialFileName(text + ".json");

        try {
            var file = fc.showSaveDialog(stage);
            if (file != null) {
                // 3. Interação com o Model/Serviços
                FileManager.saveProject(text, file);

                // 4. Notificação da View e Navegação
                showToastProperty.set("Project was created!"); // A View vai reagir a isso

                // Navegação (A ViewModel decide para onde ir, mas não sabe como a cena é construída)
                stage.setScene(AppScenes.HomeScene(stage));
            }
        } catch (Exception e) {
            // Se houver exceção na I/O, notificar a View
            errorMessageProperty.set("Erro ao salvar projeto: " + e.getMessage());
        }
    }
}