package my_app.themes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public enum ThemeType {
        LIGHT, DARK
    }

    public final ObjectProperty<ThemeType> themeProperty = new SimpleObjectProperty<>(ThemeType.DARK);

    private final List<Scene> scenes;
    private final String lightThemePath = ThemeManager.class.getResource("/theme-light.css").toExternalForm();
    private final String darkThemePath = ThemeManager.class.getResource("/theme-dark.css").toExternalForm();

    private static ThemeManager instance;

    public static ThemeManager Instance() {
        if (instance == null) {
            instance = new ThemeManager();
        }

        return instance;
    }

    private ThemeManager() {
        this.scenes = new ArrayList<>();

        themeProperty.addListener((_, _, newTheme) -> applyTheme(newTheme));
    }

    public void addScene(Scene scene) {
        if (!this.scenes.contains(scene)) { // evita duplicação
            this.scenes.add(scene);
            applyTheme(themeProperty.get());
        }
    }

    public void toogleTheme() {
        themeProperty.set(themeProperty.get() == ThemeType.DARK ? ThemeType.LIGHT : ThemeType.DARK);
    }

    private void applyTheme(ThemeType t) {
        for (Scene scene : scenes) {
            final var stylesheets = scene.getStylesheets();

            stylesheets.remove(lightThemePath);
            stylesheets.remove(darkThemePath);

            if (t == ThemeType.LIGHT) {
                if (!stylesheets.contains(lightThemePath)) {
                    stylesheets.add(lightThemePath);
                }
            } else {
                if (!stylesheets.contains(darkThemePath)) {
                    stylesheets.add(darkThemePath);
                }
            }

            stylesheets.forEach(System.out::println);
        }

    }

    public ThemeType getTheme() {
        return themeProperty.get();
    }

    public boolean themeIsWhite(ThemeType themeType) {
        return themeType.equals(ThemeType.LIGHT);
    }

    public boolean themeIsWhite() {
        return themeProperty.get().equals(ThemeType.LIGHT);
    }
}
