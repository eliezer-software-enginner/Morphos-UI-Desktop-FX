package my_app.screens.SplashScreen;

import javafx.stage.Stage;
import my_app.FileManager;
import my_app.scenes.AppScenes;

import java.io.File;

public class SplashScreenViewModel {

    private final Stage stage;

    public SplashScreenViewModel(Stage theirStage) {
        this.stage = theirStage;
    }

    /**
     * Comando chamado pela View (quando a animação termina).
     * Contém a lógica de negócio para decidir a próxima tela.
     */
    public void decideNextScene() {
        try {
            final var prefsData = FileManager.loadDataInPrefs();
            final var absolutePath = prefsData.last_project_saved_path();

            // Lógica de negócio: Se o caminho é inválido ou nulo, vá para Criação.
            if (absolutePath == null || !new File(absolutePath).exists()) {
                AppScenes.SwapScene(stage, AppScenes.CreateProjectScene(stage));
                return;
            }

            // Sucesso: vá para a Home
            AppScenes.SwapScene(stage, AppScenes.HomeScene(stage));

        } catch (Exception e) {
            // Falha na leitura: vá para a tela de Criação como fallback seguro.
            AppScenes.SwapScene(stage, AppScenes.CreateProjectScene(stage));
        }
    }
}