package toolkit.theme;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import my_app.data.Commons;

/**
 * Sistema de tema global baseado em Material Design
 * Permite definir estilos programaticamente sem depender de arquivos CSS
 */

@Deprecated
public class MaterialTheme {

    // Singleton instance
    private static MaterialTheme instance;

    // Cores do tema
    private Color primaryColor = Color.web("#1976D2");
    private Color primaryVariantColor = Color.web("#1565c0ff");
    private Color secondaryColor = Color.web("#FFC107");
    private Color secondaryVariantColor = Color.web("#FFA000");

    // Cores de fundo
    private Color backgroundColor = Color.web("#15161A");
    private Color surfaceColor = Color.web("#1E1F23");
    private Color errorColor = Color.web("#B00020");

    // Cores de texto
    private Color onPrimaryColor = Color.web("#FFFFFF");
    private Color onSecondaryColor = Color.web("#000000");
    private Color onBackgroundColor = Color.web("#000000");
    private Color onSurfaceColor = Color.web("#000000");
    private Color onErrorColor = Color.web("#FFFFFF");

    // Cores de estado
    private Color disabledColor = Color.web("#BDBDBD");
    private Color focusColor = Color.web("#3B38A0");
    private Color hoverColor = Color.web("#7371FC");
    private Color hoverColorSeconday = Color.web("#1E1F23");

    // Tipografia
    private FontWeight headingFontWeight = FontWeight.BOLD;
    private FontWeight bodyFontWeight = FontWeight.NORMAL;
    private FontWeight buttonFontWeight = FontWeight.MEDIUM;

    // Tamanhos de fonte
    private double h1FontSize = 32.0;
    private double h2FontSize = 24.0;
    private double h3FontSize = 20.0;
    private double h4FontSize = 18.0;
    private double h5FontSize = 16.0;
    private double h6FontSize = 14.0;
    private double bodyFontSize = 14.0;
    private double captionFontSize = 12.0;
    private double buttonFontSize = 14.0;

    // Espaçamentos
    private double spacingUnit = 8.0;
    private double borderRadius = 4.0;
    private double borderWidth = 1.0;

    // Família de fontes
    private String fontFamily = "Montserrat";

    private MaterialTheme() {
        // Construtor privado para singleton
    }

    public static MaterialTheme getInstance() {
        if (instance == null) {
            instance = new MaterialTheme();
        }
        return instance;
    }

    // Métodos para obter cores
    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getPrimaryVariantColor() {
        return primaryVariantColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public Color getSecondaryVariantColor() {
        return secondaryVariantColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getSurfaceColor() {
        return surfaceColor;
    }

    public Color getErrorColor() {
        return errorColor;
    }

    public Color getOnPrimaryColor() {
        return onPrimaryColor;
    }

    public Color getOnSecondaryColor() {
        return onSecondaryColor;
    }

    public Color getOnBackgroundColor() {
        return onBackgroundColor;
    }

    public Color getOnSurfaceColor() {
        return onSurfaceColor;
    }

    public Color getOnErrorColor() {
        return onErrorColor;
    }

    public Color getDisabledColor() {
        return disabledColor;
    }

    public Color getFocusColor() {
        return focusColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public Color getHoverColorSecondary() {
        return hoverColorSeconday;
    }

    // Métodos para obter tipografia
    public FontWeight getHeadingFontWeight() {
        return headingFontWeight;
    }

    public FontWeight getBodyFontWeight() {
        return bodyFontWeight;
    }

    public FontWeight getButtonFontWeight() {
        return buttonFontWeight;
    }

    // Métodos para obter tamanhos de fonte
    public double getH1FontSize() {
        return h1FontSize;
    }

    public double getH2FontSize() {
        return h2FontSize;
    }

    public double getH3FontSize() {
        return h3FontSize;
    }

    public double getH4FontSize() {
        return h4FontSize;
    }

    public double getH5FontSize() {
        return h5FontSize;
    }

    public double getH6FontSize() {
        return h6FontSize;
    }

    public double getBodyFontSize() {
        return bodyFontSize;
    }

    public double getCaptionFontSize() {
        return captionFontSize;
    }

    public double getButtonFontSize() {
        return buttonFontSize;
    }

    // Métodos para obter espaçamentos
    public double getSpacingUnit() {
        return spacingUnit;
    }

    public double getBorderRadius() {
        return borderRadius;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    // Métodos para calcular espaçamentos
    public double spacing(int units) {
        return spacingUnit * units;
    }

    public double spacing(double units) {
        return spacingUnit * units;
    }

    // Métodos para obter fontes tipográficas
    public Font getH1Font() {

        return Font.font(fontFamily, headingFontWeight, h1FontSize);
    }

    public Font getH2Font() {
        return Font.font(fontFamily, headingFontWeight, h2FontSize);
    }

    public Font getH3Font() {
        return Font.font(fontFamily, headingFontWeight, h3FontSize);
    }

    public Font getCaptionFont() {
        return Font.font(fontFamily, bodyFontWeight, captionFontSize);
    }

    public Font getButtonFont() {
        return Font.font(fontFamily, buttonFontWeight, buttonFontSize);
    }

    // Métodos para definir cores (para personalização)
    public MaterialTheme setPrimaryColor(Color color) {
        this.primaryColor = color;
        return this;
    }


    // Métodos para obter estilos CSS como String
    public String getPrimaryColorStyle() {
        return String.format("#%02X%02X%02X",
                (int) (primaryColor.getRed() * 255),
                (int) (primaryColor.getGreen() * 255),
                (int) (primaryColor.getBlue() * 255));
    }


    public String getBackgroundColorStyle() {
        return String.format("#%02X%02X%02X",
                (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255));
    }

    public String getOnPrimaryColorStyle() {
        return String.format("#%02X%02X%02X",
                (int) (onPrimaryColor.getRed() * 255),
                (int) (onPrimaryColor.getGreen() * 255),
                (int) (onPrimaryColor.getBlue() * 255));
    }

    public String getFocusColorStyle() {
        return getColorString(focusColor);
    }

    public String getHoverColorSecondaryStyle() {
        return getColorString(hoverColorSeconday);
    }

    public String getSurfaceColorStyle() {
        return getColorString(surfaceColor);
    }

    private String getColorString(Color color) {
        return Commons.ColortoHex(color);
    }

}
