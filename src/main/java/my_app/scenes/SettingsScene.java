package my_app.scenes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.Locale;
import java.util.Map;

public class SettingsScene extends Scene {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    SimpleObjectProperty<TranslationContext.Translation> translationProperty = TranslationContext.instance().translationProperty();

    @Component
    Text title = Typography.title(translation.settings());
    @Component
    Text language = Typography.subtitle(translation.language());

    Map<String, Locale> map = Map.of("English", Locale.ENGLISH,
            "Português-Br", Locale.of("pt-br"));

    @Component
    ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(
            map.keySet()
    ));

    @Component
    Region region = Components.spacerVertical(10);
    @Component
    VBox layout = new VBox(title, region, language, comboBox);

    private final Stage stage = new Stage();

    public SettingsScene() {
        super(new VBox(), 700, 500);

        setRoot(layout);

        setup();
        styles();
    }

    void setup() {
        stage.setResizable(true);


        translationProperty.addListener((_, _, v) -> {
            title.setText(v.settings());
            language.setText(v.language());
        });

        var locale = Locale.of(TranslationContext.instance().currentLanguage);

        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(locale)) {
                comboBox.setValue(entry.getKey());
                break;
            }
        }

        comboBox.setOnAction(_ -> {
            var n = comboBox.getValue();
            var locale_ = map.get(n);

            IO.println(locale_.getLanguage());

            TranslationContext.instance().changeLanguage(locale_);
        });

        ThemeManager.Instance().addScene(this);
    }

    public void show() {
        stage.setScene(this);
        stage.show();
    }

    void styles() {
        layout.setStyle("-fx-background-color:#15161A;");
        layout.setPadding(new Insets(20));

        Commons.UseDefaultStyles(this);
    }

}
