package my_app.contexts;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class TranslationContext {
    private static TranslationContext instance;
    private Translation translation;

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

    public record Translation(
        SplashTranslation splashTranslation
    ){}

    public Translation get(){
        return this.translation;
    }

    public record SplashTranslation(
            String title, String description, String footer
    ){
    }

    static void main() {
        var t = TranslationContext.instance();

        Locale[] locales = {
               Locale.US, Locale.of("pt-br")
        };

        //Arrays.stream(locales).forEach(t::loadTranslation);
        t.loadTranslation(locales[0]);

        var data = t.translation;
        var title = data.splashTranslation().title();
        IO.println(title);

    }
}
