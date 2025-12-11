package my_app.data;

public record InputComponentData(
        String type,
        String text,
        String placeholder,
        String font_weight,
        String font_size,
        String color,
        double x,
        double y,
        String identification,
        String canva_id,
        String focus_color,
        String placeholder_color,
        String no_focus_color,
        String name, boolean isDeleted
) implements ComponentData {
}