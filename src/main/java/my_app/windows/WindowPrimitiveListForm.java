package my_app.windows;

import javafx.stage.Stage;

@Deprecated
public class WindowPrimitiveListForm extends Stage {
    // Scene scene = AppScenes.PrimitiveListFormScene(window);

    public WindowPrimitiveListForm() {
        //  setScene(scene);
        setWidth(800);
        setHeight(500);

        setup();
    }

    public void setup() {
        //   ThemeManager.Instance().addScene(scene);
        //  Commons.UseDefaultStyles(scene);
    }
}
