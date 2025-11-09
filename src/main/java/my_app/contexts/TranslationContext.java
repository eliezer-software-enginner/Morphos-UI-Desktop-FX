package my_app.contexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleObjectProperty;
import my_app.App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class TranslationContext {
    private static TranslationContext instance;
    private Translation englishBase;
    private SimpleObjectProperty<Translation> translation = null;
    private App application;

    public String currentLanguage;

    public static TranslationContext instance() {
        if (instance == null) instance = new TranslationContext();
        return instance;
    }

    public void loadTranslation(Locale locale) {
        IO.println(locale.getLanguage());

        currentLanguage = locale.getLanguage();

        var path = Path.of("src/main/resources/translations").resolve(locale.getLanguage() + ".json");
        var pathEnglish = Path.of("src/main/resources/translations").resolve("en.json");

        try {
            var content = Files.readString(path);
            var contentEn = Files.readString(pathEnglish);

            var om = new ObjectMapper();

            if (this.translation == null)
                this.translation = new SimpleObjectProperty<>();

            this.translation.set(om.readValue(content, Translation.class));
            this.englishBase = om.readValue(contentEn, Translation.class);

        } catch (IOException e) {
            IO.println("erro aconteceu");
            e.printStackTrace();
        }

    }

    public void onEntryPoint(App app) {
        this.application = app;
    }

    public void changeLanguage(Locale locale) {
        this.application.changeLanguage(locale);
    }

    public String currentLanguage() {
        return currentLanguage;
    }

    public record Translation(
            SplashTranslation splashTranslation,
            OptionsMenuMainScene optionsMenuMainScene,
            Common common,
            String appearance,
            String layout,
            String appearanceSettings,
            String layoutSettings,
            String noComponentSelected,
            String selectComponentToViewSettings,
            String visualElements,
            String text,
            String button,
            String input,
            String image,
            String component,
            String menu,
            String save,
            String imports,
            String codeContent,
            String codeContentOfCustomComponent,
            String width,
            String height,
            String textContent,
            String fontWeight,
            String fontColor,
            String fontSize,
            String removeComponent,
            String preserveRatio,
            String chooseImage,
            String borderRadius,
            String borderWidth,
            String borderColor,
            String backgroundColor,
            String settings,
            String language
    ) {
    }

    public Translation get() {
        return this.translation.get();
    }

    public SimpleObjectProperty<Translation> translationProperty() {
        return this.translation;
    }

    public Translation getInEnglishBase() {
        return this.englishBase;
    }

    public record SplashTranslation(
            String title, String description, String footer
    ) {
    }

    public record OptionsMenuMainScene(
            String showCode, String becomeContributor
    ) {
    }

    public record Common(
            String save, String saveAs, String load, String option
    ) {
    }
}
