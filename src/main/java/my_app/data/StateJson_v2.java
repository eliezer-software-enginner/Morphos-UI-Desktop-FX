package my_app.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StateJson_v2 implements Serializable {
    public String screen_id = UUID.randomUUID().toString();
    public String id_of_component_selected;
    public String type_of_component_selected;

    //public CanvaProps canva;
    public CanvaComponentData canva;
    public List<TextComponentData> text_components = new ArrayList<>();
    public List<ButtonComponentData> button_components = new ArrayList<>();
    public List<ImageComponentData> image_components = new ArrayList<>();
    public List<InputComponentData> input_components = new ArrayList<>();
    // REMOVIDA: public List<FlexComponentData> flex_components = new ArrayList<>();

    // ADICIONADA: Nova lista de componentes de Coluna
    public List<ColumnComponentData> column_components = new ArrayList<>();

    public List<CustomComponentData> custom_components = new ArrayList<>();
}
