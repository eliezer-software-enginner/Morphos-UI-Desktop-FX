package my_app.data;

public record ButtonComponentData(
        String type,
        String text,
        String fontSize,
        String fontWeight,
        String color,
        String borderWidth,
        String borderRadius,
        String bgColor,
        double x,
        double y,
        int padding_top,
        int padding_right,
        int padding_bottom,
        int padding_left,
        String identification,
        String nameOfOnClickMethod,
        String canva_id,
        String border_color, String name, IconData icon, boolean isDeleted) implements ComponentData {

}