package my_app.windows;

import javafx.stage.Stage;
import my_app.scenes.AppScenes;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class AllWindows {

    public static void showWindowForShowCode(HomeViewModel homeViewModel, CanvaComponentV2 canva) {
        final var window = new Stage();
        window.setScene(AppScenes.ShowCodeFormScene(homeViewModel, window, canva));
        window.show();
    }

    public static void showWindowForDataTable() {
        final var window = new Stage();
        window.setScene(AppScenes.DataTableScene(window));
        window.show();
    }

    public static void showWindowForDataTableForm_PrimitiveData(Runnable callback) {
        final var window = new Stage();
        window.setScene(AppScenes.PrimitiveListFormScene(window, callback));
        window.show();
    }

    public static void showWindowForCreateNewProject() {
        final var window = new Stage();
        window.setScene(AppScenes.CreateProjectScene(window));
        window.show();
    }

    public static void showWindowForSelectIcons(Consumer<FontIcon> onIconSelected) {
        final var window = new Stage();
        // Passa o callback para AppScenes.IconsScene
        window.setScene(AppScenes.IconsScene(window, onIconSelected));
        window.show();
    }

    public static void showSceneCreateCustomComponent(HomeViewModel homeViewModel) {
        final var window = new Stage();
        window.setScene(AppScenes.SceneCreateCustomComponent(window, homeViewModel));
        window.show();
    }

    public static void showWindowForPreviewUI(CanvaComponentV2 canva) {
        final var window = new Stage();
        window.setScene(AppScenes.ScenePreviewUI(window, canva));
        window.show();
    }
}
