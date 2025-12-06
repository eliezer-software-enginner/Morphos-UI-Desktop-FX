package my_app.data;

import java.io.Serializable;
import java.util.UUID;

public class StateJson_v3 implements Serializable {
    public String screen_id = UUID.randomUUID().toString();
    public String name = UUID.randomUUID().toString();
    public String id_of_component_selected;
    public String type_of_component_selected;

    //public CanvaProps canva;
    public CanvaComponentDatav2 canva;

}
