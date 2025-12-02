package my_app.data;

public record ColumnComponentData(
        String type, // NOVO: "column",
        String identification,
        String childId,
        String alternativeChildId,
        int x,
        int y,
        boolean in_canva,
        String canva_id,
        int pref_child_amount_for_preview, boolean isDeleted,
        String dataTableVariableName) implements ComponentData {

    public ColumnComponentData {
        if (type == null) {
            type = "column items";
        }
    }

}
