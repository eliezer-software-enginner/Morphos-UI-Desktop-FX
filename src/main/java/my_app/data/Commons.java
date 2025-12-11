package my_app.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commons {

    public enum ScreensSize {
        SMALL(640, 480),

        _700x500(700, 500),
        _775x500(775, 500),
        _900x500(900, 500),
        _900x560(900, 650),

        _1200x650(1200, 650),
        _1280x720(1280, 720),

        MEDIUM(800, 600),
        LARGE(1280, 720),
        _1500x900(1500, 900),
        XLARGE(1600, 900),
        FULL(1920, 1080);

        public final double width, heigh;

        ScreensSize(int width, int height) {
            this.width = width;
            this.heigh = height;
        }
    }

    public static String AppName = "Morphos Desktop FX";
    public static String AppNameAtAppData = "morphos_desktop_fx";
    public static String AppVersion = "v1.0";

    public static double CanvaWidthDefault = 800;
    public static double CanvaHeightDefault = 600;

    public static String FontWeightDefault = "normal";
    public static String ItemTextFontSizeDefault = "14";

    public static String CanvaBgColorDefault = "white";
    public static String ButtonBgColorDefault = "#664db3";
    public static String ButtonPaddingDefault = "10";
    public static String ButtonFontWeightDefault = "normal";
    public static String ButtonFontSizeDefault = "16";
    public static String FontSizeDefault = "16";
    public static String ColorTransparent = "transparent";
    public static String BorderColorDefault = "black";
    public static String FocusColorDefault = "black";
    public static String PlaceHolderColorDefault = "gray";

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

    @Deprecated
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
        if (node.getParent() instanceof CanvaComponentV2 canva) {
            // Caso POSITIVO: Retorna TRUE com o ID do Canva pai
            return new NodeInCanva(true, canva.getId());
        }

        return new NodeInCanva(false, null);
    }

    //change this function in the future
    @Deprecated
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

    public static void CentralizeComponent(Node node, Pane canva) {

        Runnable runnable = () -> {
            double larguraPane = canva.getWidth();
            double larguraNode = node.getBoundsInLocal().getWidth();

            // Aplica a formula de centralizacao
            double novaPosX = (larguraPane - larguraNode) / 2;

            // Define a nova posicao
            node.setLayoutX(novaPosX);
        };

        runnable.run();

        // Adiciona um listener para recalcular a centralização
        // caso o tamanho do Pane mude (por exemplo, ao redimensionar a janela)
        canva.widthProperty().addListener((obs, oldVal, newVal) -> {
            runnable.run();
        });

    }

    static void main() {
        //getVariableNamesInDataTable().forEach(IO::println);
        // getValuesFromVariablename("colors").forEach(IO::println);
    }

}
