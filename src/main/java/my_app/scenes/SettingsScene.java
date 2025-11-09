package my_app.scenes;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import my_app.contexts.TranslationContext;
import toolkit.Component;

import java.util.List;
import java.util.Locale;

public class SettingsScene extends Scene {
    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    Text title = new Text(translation.settings());
    @Component
    Text language = new Text("Language");


    record Field(String name, Locale locale) {
    }

    @Component
    ComboBox<Field> comboBox = new ComboBox<>(FXCollections.observableArrayList(
            List.of(new Field("English", Locale.ENGLISH),
                    new Field("Português-Br", Locale.of("pt-br"))
            )
    ));

    @Component
    VBox layout = new VBox(title, language, comboBox);

    private final Stage stage = new Stage();

    public SettingsScene() {
        super(new VBox(), 700, 500);

        setRoot(layout);

        setup();
        styles();
    }

    void setup() {
        stage.setResizable(true);
    }

    public void show() {
        stage.show();
    }

    void styles() {
        layout.setStyle("-fx-background-color:#15161A;");
        title.setStyle("-fx-font-size:40px;-fx-fill:white;");
        language.setStyle("-fx-font-size:40px;-fx-fill:white;");
    }


}
