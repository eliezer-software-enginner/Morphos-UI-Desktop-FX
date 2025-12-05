package my_app.data;

import java.util.List;

public record Project(String name, TableData tableData, List<StateJson_v2> screens) {
}
