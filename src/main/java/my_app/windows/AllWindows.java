package my_app.windows;

import javafx.stage.Stage;
import my_app.scenes.AppScenes;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

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
}
