package my_app.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.scenes.MainScene.MainSceneController;

public class Commons {

    public static String AppName = "Morphos Desktop FX";
    public static String AppNameAtAppData = "morphos_desktop_fx";
    public static String AppVersion = "v1.0";

    public static double CanvaWidthDefault = 800;
    public static double CanvaHeightDefault = 600;

    public static String FontWeightDefault = "normal";
    public static String ItemTextFontSizeDefault = "14";

    public static String CanvaBgColorDefault = "#1E1F23";
    public static String ButtonBgColorDefault = "#664db3";
    public static String ButtonPaddingDefault = "10";
    public static String ButtonFontWeightDefault = "normal";
    public static String ButtonFontSizeDefault = "16";
    public static String FontSizeDefault = "16";
    public static String ButtonTextColorDefault = "white";
    public static String ButtonRadiusDefault = "3";
    public static String ButtonRadiusWidth = "0";

    public static void UseDefaultStyles(Scene scene) {
        scene.getStylesheets().addAll(
                Commons.class.getResource("/global_styles.css").toExternalForm(),
                Commons.class.getResource("/typography.css").toExternalForm());
    }

    /**
     * @param path corresponds to path inside resources directory
     * @return
     */
    public static ImageView CreateImageView(String path) {

        final var img = new Image(Commons.class.getResourceAsStream(path));
        return new ImageView(img);
    }

    public static String UpdateEspecificStyle(
            String currentStyle,
            String targetField,
            String value) {

        // Cria a string de estilo com o valor a ser atualizado
        String newStyle = targetField + ": " + value + ";";

        // Verifica se o estilo já contém o campo de destino
        if (currentStyle.contains(targetField)) {
            // Substitui a parte do estilo correspondente ao targetField com o novo valor
            currentStyle = currentStyle.replaceAll(
                    "(?i)" + targetField + ":\\s*[^;]+;", // Captura o campo de destino e o valor atual, ignorando
                    // espaços extras
                    newStyle); // Substitui com o novo valor
        } else {
            // Se não houver, adiciona o novo estilo no final
            if (!currentStyle.endsWith(" ")) { // Evita duplicação de espaços
                currentStyle += " ";
            }
            currentStyle += newStyle; // Adiciona o novo estilo ao final
        }

        // Para verificar o estilo final (opcional, apenas para depuração)
        System.out.println("Updated Style: " + currentStyle);

        return currentStyle;
    }

    public static String getValueOfSpecificField(
            String currentStyle,
            String targetField) {

        // Verifica se o campo está presente
        if (currentStyle.contains(targetField)) {
            // Expressão regular para capturar o valor do campo, tratando espaços extras e
            // valores de cor
            String regex = targetField + ":\\s*([^;]+);"; // \\s* permite espaços extras
            Pattern pattern = java.util.regex.Pattern.compile(regex);
            Matcher matcher = pattern.matcher(currentStyle);

            // Se encontrar uma correspondência, retorna o valor
            if (matcher.find()) {
                return matcher.group(1); // grupo 1 contém o valor após ":"
            }
        }

        // Se não encontrar o campo, retorna uma string vazia
        return "";
    }

    public static String ColortoHex(Color color) {
        return String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void WriteJsonInDisc(File file, Object obj) {

        ObjectMapper om = new ObjectMapper();

        try {
            om.writeValue(file, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record NodeInCanva(boolean inCanva, String fatherId) {
    }

    public static NodeInCanva NodeInCanva(Node node) {
        if (node.getParent() instanceof CanvaComponent canva) {
            // Caso POSITIVO: Retorna TRUE com o ID do Canva pai
            return new NodeInCanva(true, canva.getId());
        }

        return new NodeInCanva(false, null);
    }

    public static MainSceneController.PrefsData getPrefsData() {
        String appData = loadPrefs();
        var prefs = Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");

        try {
            var om = new ObjectMapper();
            var jsonContent = Files.readString(prefs);
            return om.readValue(jsonContent, MainSceneController.PrefsData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Path getPrefsFile() {
        String appData = loadPrefs();
        return Path.of(appData).resolve(Commons.AppNameAtAppData).resolve("prefs.json");
    }

    //change this function in the future
    public static String loadPrefs() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows
            String appData = System.getenv("LOCALAPPDATA");
            if (appData == null) {
                appData = userHome + "\\AppData\\Local";
            }
            return appData;
        } else if (os.contains("mac")) {
            // macOS
            return userHome + "/Library/Application Support";
        } else {
            // Linux e outros
            return userHome + "/.local/share";
        }
    }
}
