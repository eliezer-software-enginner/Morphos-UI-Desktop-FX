package my_app.data;

import java.util.List;

public class CustomComponentData implements ComponentData {
    public String type = "component";
    public int padding_top;
    public int padding_right;
    public int padding_bottom;
    public int padding_left;
    public double width;
    public double height;
    public String bg_type;
    public String bgContent;
    public String identification;
    public int x;
    public int y;
    public boolean in_canva;
    public String canva_id;
    public List<TextComponentData> text_components;
    public List<ButtonComponentData> button_components;
    public List<ImageComponentData> image_components;
    public List<InputComponentData> input_components;
    public List<ColumnComponentData> column_components;
    public List<CustomComponentData> custom_components;
    public boolean isDeleted;

    // Construtor vazio
    public CustomComponentData() {
    }

    // Construtor com todos os argumentos
    public CustomComponentData(
            int padding_top,
            int padding_right,
            int padding_bottom,
            int padding_left,
            double width,
            double height,
            String bg_type,
            String bgContent,
            String identification,
            int x,
            int y,
            boolean in_canva,
            String canva_id,
            List<TextComponentData> text_componentes,
            List<ButtonComponentData> button_components,
            List<ImageComponentData> image_components,
            List<InputComponentData> input_components,
            List<ColumnComponentData> column_components,
            List<CustomComponentData> custom_components,
            boolean isDeleted) {

        this.padding_top = padding_top;
        this.padding_right = padding_right;
        this.padding_bottom = padding_bottom;
        this.padding_left = padding_left;
        this.width = width;
        this.height = height;
        this.bg_type = bg_type;
        this.bgContent = bgContent;
        this.identification = identification;
        this.x = x;
        this.y = y;
        this.in_canva = in_canva;
        this.canva_id = canva_id;
        this.text_components = text_componentes;
        this.button_components = button_components;
        this.image_components = image_components;
        this.input_components = input_components;
        this.column_components = column_components;
        this.custom_components = custom_components;
        this.isDeleted = isDeleted;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String identification() {
        return identification;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }
}
