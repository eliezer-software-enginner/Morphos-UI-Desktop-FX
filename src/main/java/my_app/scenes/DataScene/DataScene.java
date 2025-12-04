package my_app.scenes.DataScene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.ThemeManager;
import my_app.windows.WindowPrimitiveListForm;
import toolkit.Component;

public class DataScene extends Scene {
    Stage stage = new Stage();
    @Component
    Button btnCreateData = Components.ButtonPrimaryOutline("Create Data");
    @Component
    TilePane cardsTitlePane = new TilePane(15, 5);
    DataSceneViewModel viewModel = new DataSceneViewModel();

    @Component
    private final VBox mainView = new VBox(10, btnCreateData, cardsTitlePane);

    public DataScene() {
        super(new VBox(), 1200, 650);
        super.setRoot(mainView);

        viewModel.createCardsIntoTilePane(cardsTitlePane);

        //mainView.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20;");

        btnCreateData.setOnAction(e -> new WindowPrimitiveListForm().show());

        setup();
    }

    public void setup() {
        ThemeManager.Instance().addScene(this);

        mainView.getStyleClass().add("background-color");
        Commons.UseDefaultStyles(this);
    }

    public void show() {
        stage.setScene(this);
        stage.show();
    }
}
