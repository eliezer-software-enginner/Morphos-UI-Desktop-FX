package my_app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.hotreload.CoesionApp;
import my_app.hotreload.HotReload;
import my_app.scenes.AppScenes;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@CoesionApp(stylesheets = {})
public class App extends Application {

    public static Stage stage;
    TranslationContext translationContext;

    HotReload hotReload;

    @Override
    public void init() {
        translationContext = TranslationContext.instance();
        translationContext.onEntryPoint(this);

//        var prefsData = Commons.getPrefsData();
//
//        if (prefsData != null) {
//            translationContext.loadTranslation(Locale.of(prefsData.language()));
//        } else {
//            translationContext.loadTranslation(Locale.getDefault());
//        }

        translationContext.loadTranslation(Locale.getDefault());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // 🛑 NOVO: Chama a lógica de inicialização em um método estático
        // A primeira chamada é feita pelo App original (System CL)
        initializeScene(primaryStage);

        Set<String> exclusions = new HashSet<String>();
        // 🛑 REMOÇÃO CRUCIAL: App NÃO deve mais ser excluído para que o HotReload possa carregá-lo
        // exclusions.add("my_app.App");

        // Mantemos a exclusão dessas classes/interfaces de infraestrutura:
        exclusions.add("my_app.hotreload.CoesionApp");
        exclusions.add("my_app.hotreload.Reloader");

        this.hotReload = new HotReload(
                "src/main/java/my_app",
                "target/classes",
                "src/main/resources",
                "my_app.hotreload.UIReloaderImpl",
                primaryStage,
                exclusions
        );

        // getStylesheets().add(getClass().getResource("/global_styles.css").toExternalForm());
        stage.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            javafx.scene.Node node = event.getPickResult().getIntersectedNode();
            if (node != null) {
                System.out.println("Clique em: " + node.getClass().getSimpleName());
                // node.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0, 0, 0);");

                if (node.getId() != null)
                    System.out.println("ID: " + node.getId());
            }
        });


        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/app_ico_window_32_32.png"))));

        primaryStage.show();
        this.hotReload.start();
    }

    // 🛑 NOVO MÉTODO ESTÁTICO: Centraliza a lógica de UI
    // Este método será chamado pelo start() e também pelo UIReloaderImpl1 (via Reflection)
    public static void initializeScene(Stage stage) throws Exception {
        // Configurações do Stage que podem mudar em desenvolvimento
        stage.setTitle(Commons.AppName + " " + Commons.AppVersion);
        stage.setMinWidth(Commons.ScreensSize.LARGE.width);

        // A linha chave: A Scene é recriada.
        // Se este método for chamado pelo HotReloadCL, o AppScenes será o novo.
        stage.setScene(AppScenes.HomeScene(stage));
        System.out.println("[App] Scene re-initialized.");
    }

    public void changeLanguage(Locale locale) {
        translationContext.loadTranslation(locale);

//        MainScene mainScene = new MainScene();
//        stage.setScene(mainScene);
    }

    // 🛑 NOVO MÉTODO: Ponto de entrada para a recarga de UI
    // Ele precisa ser estático, público/privado (usamos setAccessible no Reloader)
    // e deve aceitar o Stage principal.
    public static void reinitScene(Stage stage) throws Exception {
        // Re-executa a lógica que configura a Scene principal
        // O AppScenes.HomeScene() será resolvido pelo ClassLoader que carregou a nova AppScenes.java.
        // Já que a AppScenes provavelmente NÃO está na lista de classes excluídas,
        // a versão mais nova dela será usada aqui.
        stage.setScene(AppScenes.HomeScene(stage));
        System.out.println("[App] Scene re-initialized with new AppScenes logic.");
    }

    static void main(String[] args) {
        launch(args);
    }
}
