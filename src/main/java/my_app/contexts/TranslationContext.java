package my_app.contexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import my_app.App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class TranslationContext {
    private static TranslationContext instance;
    private Translation translation;
    private App application;

    public static TranslationContext instance(){
        if(instance == null) instance = new TranslationContext();
        return instance;
    }

    public void loadTranslation(Locale locale){
        IO.println(locale.getLanguage());

        var path = Path.of("src/main/resources/translations").resolve(locale.getLanguage() + ".json");

        try{
            var content = Files.readString(path);
            IO.println("content=> " + content);

            var om = new ObjectMapper();
            this.translation = om.readValue(content, Translation.class);

        }catch (IOException e){
            IO.println("erro aconteceu");
            e.printStackTrace();}

    }

    public void onEntryPoint(App app) {
        this.application = app;
    }

    public void changeLanguage(Locale locale){
        this.application.changeLanguage(locale);
    }

    public record Translation(
        SplashTranslation splashTranslation,
        OptionsMenuMainScene optionsMenuMainScene,
        Common common,
        String Appearance,
        String Layout,
        String AppearanceSettings,
        String LayoutSettings,
        String NoComponentSelected,
        String SelectComponentToViewSettings,
        String VisualElements,
        String Text,
        String Button,
        String Input,
        String Image,
        String Component,
        String menu,
        String save,
        String imports,
        String codeContent,
        String codeContentOfCustomComponent
    ){}

    public Translation get(){
        return this.translation;
    }

    public record SplashTranslation(
            String title, String description, String footer
    ){
    }

    public record OptionsMenuMainScene(
            String showCode, String becomeContributor
    ){
    }

    public record Common(
            String save, String saveAs, String load,String option
    ){
    }
}
