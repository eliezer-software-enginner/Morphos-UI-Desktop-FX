package my_app.screens.DataTableScreen;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.themes.Typography;
import my_app.windows.AllWindows;
import toolkit.Component;

public class DataTableScreen extends VBox {
    @Component
    Label textForEmpty = Typography.subtitle("No data list was found");
    @Component
    Button btnCreateData = Components.ButtonPrimaryOutline("Create Data");
    @Component
    TilePane cardsTitlePane = new TilePane(15, 5);
    @Component
    VBox contentContainer = new VBox(textForEmpty);

    DataTableViewModel viewModel = new DataTableViewModel();

    public DataTableScreen() {
        setSpacing(10);
        setPadding(new Insets(20));

        getChildren().addAll(btnCreateData, contentContainer);

        viewModel.createCardsIntoTilePane(cardsTitlePane, contentContainer, textForEmpty);
        viewModel.refreshDataProperty.addListener((_, _, _) -> {
            cardsTitlePane.getChildren().clear();

            viewModel.createCardsIntoTilePane(cardsTitlePane, contentContainer, textForEmpty);
        });

        btnCreateData.setOnAction(e -> {
            AllWindows.showWindowForDataTableForm_PrimitiveData(() -> viewModel.toggleRefreshData());
        });

        getStyleClass().add("background-color");
    }
}
