package my_app.data;

import java.util.List;

public record TableData(
        List<PrimitiveData> primitiveDataList
) {
}