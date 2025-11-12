package my_app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

record SceneProps(int width, int height) {
}

record Route(String identification,
             BiFunction<SceneGraph.NavigatorHandler, Map<String, Object>, Parent> fn,
             SceneProps sceneProps) {
    public Scene createScene(SceneGraph.NavigatorHandler navigatorHandler, Map<String, Object> data) {
        var root = fn.apply(navigatorHandler, data);
        if (sceneProps == null)
            return new Scene(root);
        return new Scene(root, sceneProps.width(), sceneProps.height());
    }
}


record SceneWrapper(Scene scene, Map<String, Object> data) {
}

class SceneGraph {
    private final Stage globalStage;
    private final Map<String, Route> routes;
    private final Map<String, SceneWrapper> cache = new HashMap<>();
    private final String entryPointId;

    private static final Map<String, Stage> independentStagesCache = new HashMap<>();

    public SceneGraph(Stage globalStage, String entryPoint, List<Route> routes) {
        this.globalStage = Objects.requireNonNull(globalStage);
        this.entryPointId = entryPoint;

        this.routes = routes.stream().collect(Collectors.toMap(Route::identification, r -> r));
    }

    public Scene get(String id, NavigatorHandler internalNavigator, Map<String, Object> data) {
        if (cache.containsKey(id)) {
            var sw = cache.get(id); // reusa cena existente

            if (data == null && sw.data() == null) return sw.scene();//not changed

            if (sw.data() != null && sw.data().equals(data)) return sw.scene();//not changed
        }

        IO.println("mudou");
        var route = routes.get(id);

        var scene = route.createScene(internalNavigator, data);
        cache.put(id, new SceneWrapper(scene, data));

        return scene;
    }

    public Stage stage() {
        return this.globalStage;
    }

    public String entrypointId() {
        return this.entryPointId;
    }

    public Map<String, Object> getData(String sceneId) {
        var sw = cache.get(sceneId);
        if (sw != null && sw.scene().getUserData() instanceof Map<?, ?> map)
            return (Map<String, Object>) map;

        return Collections.emptyMap();
    }

    public Map<String, Route> routes() {
        return routes;
    }

    public static class NavigatorHandler {
        private final SceneGraph sceneGraph;
        private final Deque<String> history = new ArrayDeque<>();


        public NavigatorHandler(SceneGraph sceneGraph) {
            if (sceneGraph == null) throw new RuntimeException("scene graph was not initialized");
            this.sceneGraph = sceneGraph;
        }

        public void showFirstRoute() {
            var scene = sceneGraph.get(sceneGraph.entrypointId(), this, null);
            if (scene != null) {
                sceneGraph.stage().setScene(scene);

                if (history.isEmpty() || !history.peek().equals(sceneGraph.entrypointId()))
                    history.push(sceneGraph.entrypointId());

                sceneGraph.stage().show();
            } else {
                IO.println("Scene not founded");
            }
        }

        public void navigate(String destinationId, Map<String, Object> data) {
            var scene = sceneGraph.get(destinationId, this, data);

            if (scene != null) {
                sceneGraph.stage().setScene(scene);
                sceneGraph.stage().show();

                if (history.isEmpty() || !history.peek().equals(destinationId))
                    history.push(destinationId);
            } else {
                IO.println("Scene not founded");
            }
        }

        public void navigate(String destinationId, Consumer<Stage> currentStageCallback, Map<String, Object> data) {

            var scene = sceneGraph.get(destinationId, this, data);

            if (scene != null) {

                var stage = sceneGraph.stage();
                currentStageCallback.accept(stage);

                stage.setScene(scene);
                stage.show();

                if (history.isEmpty() || !history.peek().equals(destinationId))
                    history.push(destinationId);

            } else {
                IO.println("Scene not founded");
            }
        }

        public void showAsWindow(String entrypointId) {
            // Verifica se a Stage para este entrypointId já existe no cache
            if (independentStagesCache.containsKey(entrypointId)) {
                var existingStage = independentStagesCache.get(entrypointId);
                // Se já existe, apenas a traz para a frente (foco) e sai.
                existingStage.toFront();
                return;
            }

            // Se não existe, cria a nova Stage (janela)

            var stage = new Stage();

            // **IMPORTANTE:** Adiciona um listener para remover do cache quando a janela for fechada
            stage.setOnCloseRequest(event -> {
                independentStagesCache.remove(entrypointId);
                IO.println("Janela " + entrypointId + " fechada e removida do cache.");
            });

            // Armazena a nova Stage no cache antes de mostrar
            independentStagesCache.put(entrypointId, stage);

            var mapRoutes = this.sceneGraph.routes();
            var appRoutes = mapRoutes.values().stream().toList();

            // Você pode querer criar um novo SceneGraph para esta nova Stage,
            // ou talvez usar o mesmo, dependendo se deseja que a navegação
            // desta nova janela afete o histórico da janela principal.
            // Para janelas totalmente independentes, o seu modelo de
            // criar um novo SceneGraph local está correto:
            var localSceneGraph = new SceneGraph(stage, entrypointId, appRoutes);
            var navigator = new SceneGraph.NavigatorHandler(localSceneGraph);

            // Define um título básico
            stage.setTitle(entrypointId + " (Janela Independente)");

            navigator.showFirstRoute();
        }

        public void goBack() {
            if (history.size() <= 1) return;

            history.pop();
            var previousId = history.peek();

            var scene = sceneGraph.get(previousId, this, null);
            sceneGraph.stage().setScene(scene);
            sceneGraph.stage().show();
        }

        public void printHistory() {
            System.out.println("History: " + history);
        }

    }
}


public class AppNavTest extends Application {
    Stage stage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        var routes = List.of(
                new Route("MainScene", SplashScreen::new, new SceneProps(500, 200)),
                new Route("ShowCodeScene", ShowCodeScreen::new, new SceneProps(500, 400)),
                new Route("CScene", CScreen::new, new SceneProps(500, 400)),
                new Route("DScene", DScreen::new, new SceneProps(500, 400))
        );

        var sceneGraph = new SceneGraph(primaryStage, "MainScene", routes);

        var navigator = new SceneGraph.NavigatorHandler(sceneGraph);
        navigator.showFirstRoute();
    }


    static void main(String[] args) {
        launch(args);
    }


}

//Telas de teste

class SplashScreen extends VBox {

    Text text = new Text("Splash");

    public SplashScreen(SceneGraph.NavigatorHandler navigatorHandler, Map<String, Object> data) {

        getChildren().add(text);

        new Thread(() -> {
            try {
                navigatorHandler.printHistory();
                Thread.sleep(Duration.ofSeconds(3));
                Platform.runLater(() -> {
                    //navigatorHandler.navigate("ShowCodeScene");
                    navigatorHandler.navigate("ShowCodeScene", stage -> {
                        stage.setTitle("Show code meu chapa");
                    }, Map.of("id", "123456789"));
                    navigatorHandler.printHistory();
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        var btn = new Button("Avancar");

        btn.setOnAction(ev -> {
            navigatorHandler.navigate("ShowCodeScene", stage -> {
                stage.setTitle("Show code meu chapa");
            }, Map.of("id", "ABC12345"));
            navigatorHandler.printHistory();
        });

        getChildren().add(btn);
    }
}

class ShowCodeScreen extends VBox {
    Button btnBack = new Button("Back");
    Text text = new Text("ShowCodeScreen");

    Button btnGoToCScreen = new Button("Go to C");

    public ShowCodeScreen(SceneGraph.NavigatorHandler navigatorHandler, Map<String, Object> data) {
        getChildren().addAll(text, btnBack, btnGoToCScreen);

        var id = data.get("id");

        IO.println(data);

        text.setText((String) id);

        btnBack.setOnAction(ev -> {
            navigatorHandler.goBack();
            navigatorHandler.printHistory();
        });

        btnGoToCScreen.setOnAction(ev -> {
            navigatorHandler.showAsWindow("CScene");
        });
    }
}

class CScreen extends VBox {
    Button btn = new Button("Go to D");
    Text text = new Text("CScreen");

    public CScreen(SceneGraph.NavigatorHandler navigatorHandler, Map<String, Object> data) {
        getChildren().addAll(text, btn);

        //var id = data.get("id");

        IO.println(data);
        
        //text.setText((String) id);

        btn.setOnAction(ev -> {
            navigatorHandler.navigate("DScene", Map.of("text", "Olá mundo"));
            navigatorHandler.printHistory();
        });
    }
}

class DScreen extends VBox {
    Button btnBack = new Button("Back");
    Text text = new Text("DScreen");

    public DScreen(SceneGraph.NavigatorHandler navigatorHandler, Map<String, Object> data) {
        getChildren().addAll(text, btnBack);

        //var id = data.get("id");

        IO.println("data: " + data);

        //text.setText((String) id);

        btnBack.setOnAction(ev -> {
            navigatorHandler.goBack();
            navigatorHandler.printHistory();
        });
    }
}