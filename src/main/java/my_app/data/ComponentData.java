package my_app.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/// / Esta é a CHAVE para resolver o erro 'Cannot construct instance of ComponentData'
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, // O tipo será determinado por um nome (string)
        include = JsonTypeInfo.As.PROPERTY, // O nome do tipo será incluído como uma propriedade JSON
        property = "type" // O nome da propriedade JSON que contém o tipo é 'type'
)
// Mapeie todos os tipos de dados concretos que podem estar em 'child'
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextComponentData.class, name = "text"),
        @JsonSubTypes.Type(value = ButtonComponentData.class, name = "button"),
        // Adicione todos os seus outros tipos de dados aqui:
        @JsonSubTypes.Type(value = ImageComponentData.class, name = "image"),
        @JsonSubTypes.Type(value = InputComponentData.class, name = "input"),
        @JsonSubTypes.Type(value = CustomComponentData.class, name = "custom component"),
        @JsonSubTypes.Type(value = ColumnComponentData.class, name = "column items"),
        @JsonSubTypes.Type(value = CanvaComponentDatav2.class, name = "canva"),
})

@JsonIgnoreProperties(ignoreUnknown = true)
public interface ComponentData extends Serializable {
    String type();

    String identification();

    boolean isDeleted();
}