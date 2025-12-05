package my_app.data;

import java.util.List;
import java.util.UUID;

public record PrimitiveData(String id, String variableName,
                            String type,
                            List<String> values,
                            long createdAt) {
    public PrimitiveData(String variableName, String type, List<String> values) {
        this(UUID.randomUUID().toString(), variableName, type, values, System.currentTimeMillis());
    }

    // Canonical constructor para validar quando id vier nulo
    public PrimitiveData {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }
}