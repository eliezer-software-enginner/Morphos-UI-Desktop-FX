package my_app.scenes.DataScene;

import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import my_app.data.Commons;
import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreenViewModel;
import my_app.themes.Typography;

import java.util.List;

public class DataSceneViewModel {

    private Commons.TableData getDataTable() {
        //todo buscar o nome do projeto atual

        var primitiveDataList = new PrimitiveListFormScreenViewModel.PrimitiveData(
                "colors", "String", List.of("black", "yellow", "blue")
        );
        var primitiveDataList2 = new PrimitiveListFormScreenViewModel.PrimitiveData(
                "colors", "String", List.of("black", "yellow", "blue")
        );
        return new Commons.TableData(List.of(primitiveDataList, primitiveDataList2));
    }

    public void createCardsIntoTilePane(TilePane titlePane) {
        var dataTable = getDataTable();

        dataTable.primitiveDataList().forEach(primitiveData -> {
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
    }
}
