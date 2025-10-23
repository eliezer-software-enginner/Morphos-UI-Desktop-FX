package my_app.themes;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Typography {
    static {
        // 🔸 Carrega as fontes personalizadas apenas uma vez (estático)
        Font.loadFont(Typography.class.getResourceAsStream("/fonts/Genshin-Impact-Font.ttf"), 10);
        Font.loadFont(Typography.class.getResourceAsStream("/fonts/Undertale.otf"), 10);
        Font.loadFont(Typography.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 10);
    }

    public static Text subtitle(String content) {
        Text text = new Text(content);
        text.getStyleClass().addAll("subtitle-typo", "text-primary-color");
        text.setStyle("-fx-font-smoothing-type: gray;"); // 🔹 Suaviza o texto
        return text;
    }

    public static Label body(String content) {
        var text = new Label(content);
        text.getStyleClass().addAll("body-typo", "text-primary-color");
        return text;
    }

    public static Label BodySecondary(String content) {
        var text = new Label(content);
        text.getStyleClass().addAll("body-typo", "text-secondary");
        return text;
    }

    public static Label caption(String content) {
        var text = new Label(content);
        text.setStyle("-fx-font-smoothing-type: gray;"); // 🔹 Suaviza o texto
        text.getStyleClass().addAll("caption-label-typo", "text-primary-color");
        return text;
    }

    public static Label error(String message) {
        var text = new Label(message);
        text.setStyle("-fx-font-smoothing-type: gray;"); // 🔹 Suaviza o texto
        text.getStyleClass().addAll("caption-label-typo", "error-color", "text-error-color");
        return text;
    }
}
