package my_app.hotreload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.scene.layout.Region;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReloadableWindow {
    /**
     * Define a classe da View que será instanciada e terá seu conteúdo injetado
     * na Stage/Scene secundária durante o Hot Reload.
     * Esta classe deve estender javafx.scene.layout.Region.
     */
    Class<? extends Region> contentClass();

    /**
     * Define o título da janela secundária, usado também para identificação na recarga.
     */
    String title() default "Secondary Window";

    /**
     * Define a largura inicial e de recarga da janela secundária.
     */
    double width() default 700;

    /**
     * Define a altura inicial e de recarga da janela secundária.
     */
    double height() default 400;

    /**
     * Array de caminhos para stylesheets específicos desta janela.
     * Os caminhos devem ser relativos ao classpath (ex: "/styles-scene-2.css").
     * Se vazio, apenas os estilos globais da aplicação serão aplicados.
     */
    String[] stylesheets() default {};
}