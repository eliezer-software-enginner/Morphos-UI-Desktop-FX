package my_app.data;

import java.io.Serializable;
import java.util.UUID;

public class StateJson_v3 implements Serializable {
    public String screen_id;
    public String name;
    public String id_of_component_selected;
    public String type_of_component_selected;

    //public CanvaProps canva;
    public CanvaComponentDatav2 canva;

    public StateJson_v3() {
        // Gera novos IDs apenas se eles ainda não tiverem sido atribuídos
        // (Isso é uma segurança se o serializador chamar o construtor antes de setar os campos)
        this.screen_id = UUID.randomUUID().toString();
        this.name = UUID.randomUUID().toString();
    }
}
