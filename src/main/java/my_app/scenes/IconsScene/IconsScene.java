package my_app.scenes.IconsScene;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;


public class IconsScene extends Scene {
    @Component
    Text title = Typography.title("Icons");

    @Component
    VBox header = new VBox();

    @Component
    TilePane content = new TilePane(10, 10, Typography.body("Select a icon pack to view icons here"));

    @Component
    ScrollPane scrollPane = new ScrollPane(content);
    @Component
    VBox layout = new VBox(title, Components.spacerVertical(10), header, Components.spacerVertical(10), scrollPane);

    private final IconsSceneViewModel viewModel = new IconsSceneViewModel();

    private final Stage stage = new Stage();

    public IconsScene() {
        var screenSize = Commons.ScreensSize.LARGE;
        super(new VBox(), screenSize.width, screenSize.heigh);
        stage.setResizable(false);

        viewModel.init();

        setRoot(layout);

        setup();
        styles();
    }

    void setup() {
        var tilepane = new TilePane();
        tilepane.setVgap(10);
        tilepane.setHgap(10);
        tilepane.setPrefColumns(8);

        for (var entry : viewModel.iconsMap.entrySet()) {

            viewModel.createItemForHeader(tilepane, entry);

            viewModel.createBufferOfCurrentEntry(entry);

            // A CHAVE: Diz ao VBox (layout) para dar todo o espaço vertical extra ao scrollPane.
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            scrollPane.setFitToWidth(true);

            // Opcional, mas útil: Garante que o layout VBox use toda a altura da Scene
            layout.setPrefSize(getWidth(), getHeight());
        }

        header.getChildren().add(tilepane);
        var separator = new Separator();
        header.getChildren().add(separator);

        viewModel.iconMapSelected.addListener((_, _,
                                               newV) -> {
            viewModel.handleClickOnIconMap(content, newV);
        });

        viewModel.iconIdSelected.addListener((_, _, newId) -> {
            viewModel.highlightItem(content, newId);
        });

        content.setPrefColumns(5);

        ThemeManager.Instance().addScene(this);
    }

    public ObjectProperty<FontIcon> iconItemSelected() {
        return viewModel.iconItemSelected;
    }

    public void show() {
        stage.setScene(this);
        stage.show();
    }

    void styles() {
        // layout.setStyle("-fx-background-color:#15161A;");

        layout.setPadding(new Insets(20));
        header.setSpacing(10);

        Commons.UseDefaultStyles(this);
        layout.getStyleClass().add("background-color");


        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent");
    }

}


