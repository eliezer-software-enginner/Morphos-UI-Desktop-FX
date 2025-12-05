package my_app.windows;

import javafx.stage.Stage;
import my_app.contexts.ComponentsContext;
import my_app.scenes.AppScenes;

public class AllWindows {

    public static void showWindowForDataTable(ComponentsContext mainComponentsContext) {
        final var window = new Stage();
        window.setScene(AppScenes.DataTableScene(mainComponentsContext, window));
        window.show();
    }

    public static void showWindowForDataTableForm_PrimitiveData() {
        final var window = new Stage();
        window.setScene(AppScenes.PrimitiveListFormScene(window));
        window.show();
    }
}
