package my_app.data;

import java.util.ArrayList;
import java.util.List;

public class CanvaComponentDatav2 implements ComponentData {
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
    public String type = "canva";
    public boolean isDeleted;
    public String name;
    public String screenFatherId;

    public List<TextComponentData> text_components = new ArrayList<>();
    public List<ButtonComponentData> button_components = new ArrayList<>();
    public List<ImageComponentData> image_components = new ArrayList<>();
    public List<InputComponentData> input_components = new ArrayList<>();
    // REMOVIDA: public List<FlexComponentData> flex_components = new ArrayList<>();

    // ADICIONADA: Nova lista de componentes de Coluna
    public List<ColumnComponentData> column_components = new ArrayList<>();

    public List<CustomComponentData> custom_components = new ArrayList<>();

    public CanvaComponentDatav2() {
    }

    public CanvaComponentDatav2(
            int padding_top,
            int padding_right,
            int padding_bottom,
            int padding_left,
            double width,
            double height,
            String bg_type,
            String bgContent,
            String identification,
            int x, int y, boolean isDeleted, String name, String screenFatherId,
            List<TextComponentData> text_components,
            List<ButtonComponentData> button_components,
            List<ImageComponentData> image_components,
            List<InputComponentData> input_components,
            List<ColumnComponentData> column_components,
            List<CustomComponentData> custom_components
    ) {

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
        this.isDeleted = isDeleted;
        this.name = name;
        this.screenFatherId = screenFatherId;
        this.text_components = text_components;
        this.button_components = button_components;
        this.image_components = image_components;
        this.input_components = input_components;
        this.column_components = column_components;
        this.custom_components = custom_components;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String identification() {
        return this.identification;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }
}