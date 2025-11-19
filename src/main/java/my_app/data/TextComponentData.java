package my_app.data;

public record TextComponentData(
        String type, // NOVO: "text"
        String text,
        double layout_x,
        double layout_y,
        String fontSize,
        String color,
        String font_weight,
        String identification,
        boolean in_canva,
        String canva_id, String name, double wrapping_width, boolean isDeleted) implements ComponentData {
}