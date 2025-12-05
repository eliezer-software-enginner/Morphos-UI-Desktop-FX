package my_app.windows;

import javafx.scene.Scene;
import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.data.Commons;
import my_app.themes.ThemeManager;

@Deprecated
public class MainWindow extends Stage {
    Scene scene;

    public MainWindow(ComponentsContext mainComponentsContext) {
        //scene = AppScenes.HomeScene(mainComponentsContext);
        setScene(scene);
        //setWidth(800);
        //setHeight(500);

        setup();
    }

    public void setup() {
        ThemeManager.Instance().addScene(scene);
        Commons.UseDefaultStyles(scene);
    }
}
