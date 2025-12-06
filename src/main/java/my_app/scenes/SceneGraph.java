package my_app.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

record SceneProps(int width, int height) {
}

record Route(String identification, Function<SceneGraph.NavigatorHandler, Parent> fn, SceneProps sceneProps) {
    public Scene createScene(SceneGraph.NavigatorHandler navigatorHandler) {
        var root = fn.apply(navigatorHandler);
        if (sceneProps == null)
            return new Scene(root);
        return new Scene(root, sceneProps.width(), sceneProps.height());
    }
}

class SplashScreen extends VBox {
    public SplashScreen(SceneGraph.NavigatorHandler navigatorHandler) {

    }
}


public class SceneGraph {
    private final Stage globalStage;
    private Map<String, Route> routes;
    private Map<String, Scene> cache;
    private NavigatorHandler internalNavigator;

    public SceneGraph(Stage globalStage, String entryPoint, List<Route> routes) {
        this.globalStage = Objects.requireNonNull(globalStage);
        //this.internalNavigator = new NavigatorHandler(this);

        this.routes = routes.stream().collect(Collectors.toMap(Route::identification, r -> r));
//        var scene = get(entryPoint);
//        globalStage.setScene(scene);
    }

    public Scene get(String id, NavigatorHandler internalNavigator) {
        if (routes == null || !routes.containsKey(id)) return null;

        var route = routes.get(id);

        var scene = route.createScene(internalNavigator);
        cache.put(id, scene);

        return scene;
    }

    public Stage stage() {
        return this.globalStage;
    }

    public String entrypointId() {
        return this.entrypointId();
    }

    public static class NavigatorHandler {
        private final SceneGraph sceneGraph;

        public NavigatorHandler(SceneGraph sceneGraph) {
            // if (sceneGraph_ == null) throw new RuntimeException("scene graph was not initialized");
            this.sceneGraph = sceneGraph;
        }

        public void showFirstRoute() {
            var scene = sceneGraph.get(sceneGraph.entrypointId(), this);
            if (scene != null) {
                sceneGraph.stage().setScene(scene);
                sceneGraph.stage().show();
            }

            IO.println("Scene not founded");
        }
    }


}
