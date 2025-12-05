package my_app.screens.DataTableScreen;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import my_app.FileManager;
import my_app.data.TableData;
import my_app.themes.Typography;

public class DataTableViewModel {

    BooleanProperty refreshDataProperty = new SimpleBooleanProperty();

    public void toggleRefreshData() {
        refreshDataProperty.set(!refreshDataProperty.get());
    }

    private TableData getDataTable() {
        var projectData = FileManager.getProjectData();
        return projectData.tableData();
    }

    public void createCardsIntoTilePane(TilePane titlePane, VBox contentContainer, Label textForEmpty) {
        var dataTable = getDataTable();
        final var primitives = dataTable.primitiveDataList();

        if (primitives.isEmpty()) {
            contentContainer.getChildren().setAll(textForEmpty);
            return;
        }


        primitives.forEach(primitiveData -> {
            final var header = new HBox(Typography.body(primitiveData.variableName()));
            final var list_Of_type = (Typography.body("List<%s>".formatted(primitiveData.type())));

            final var itemsContainerVertical = new VBox(5);
            primitiveData.values().forEach(it -> {
                        final var label = Typography.body(it);
                        label.setStyle("-fx-border-width:1px; -fx-border-color:gray; -fx-alignment: top-center;" +
                                "fx-padding: 8px;");
                        itemsContainerVertical.getChildren().add(label);

                    }
            );

            final var comp = new VBox(header, list_Of_type, itemsContainerVertical);
            comp.setStyle("-fx-border-width:1px; -fx-border-color:black; -fx-border-radius:9px; -fx-spacing: 5;-fx-padding:10px;");

            titlePane.getChildren().add(comp);
        });


        contentContainer.getChildren().setAll(titlePane);
    }
}
