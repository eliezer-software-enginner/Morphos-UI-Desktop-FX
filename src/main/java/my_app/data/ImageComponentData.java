package my_app.data;

public record ImageComponentData(
        String url,
        double width,
        double height,
        double x,
        double y,
        boolean preserve_ratio,
        String identification,
        boolean in_canva,
        String canva_id, String name, String type_of_clip, String type, boolean isDeleted) implements ComponentData {
}