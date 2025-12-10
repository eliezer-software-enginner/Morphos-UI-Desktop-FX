package my_app.screens.IconsScreen;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.themes.Typography;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

import java.util.function.Function;


public class IconsScreen extends VBox {
    @Component
    Text title = Typography.title("Icons");

    @Component
    VBox header = new VBox(10);

    TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    TilePane content = new TilePane(10, 10, Typography.body(translation.selectIconsPackToViewIconsHere()));

    @Component
    ScrollPane scrollPane = new ScrollPane(content);

    private final IconsSceneViewModel viewModel = new IconsSceneViewModel();

    public IconsScreen(Function<IconsScreen, IconsScreen> callable) {
        viewModel.init();

        getChildren()
                .addAll(title,
                        Components.spacerVertical(10), header,
                        Components.spacerVertical(10),
                        scrollPane
                );

        callable.apply(this);

        var tilepane = new TilePane();
        tilepane.setVgap(10);
        tilepane.setHgap(10);
        tilepane.setPrefColumns(8);

        header.getChildren().add(tilepane);
        //getStyleClass().add("background-color");
        //this.setPadding(new Insets(20));

        var separator = new Separator();
        header.getChildren().add(separator);

        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent");

        // A CHAVE: Diz ao VBox (layout) para dar todo o espaço vertical extra ao scrollPane.
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);

        content.setPrefColumns(5);

        for (var entry : viewModel.iconsMap.entrySet()) {

            viewModel.createItemForHeader(tilepane, entry);

            viewModel.createBufferOfCurrentEntry(entry);

            viewModel.iconMapSelected.addListener((_, _,
                                                   newV) -> {
                viewModel.handleClickOnIconMap(content, newV);
            });

            viewModel.iconIdSelected.addListener((_, _, newId) -> {
                viewModel.highlightItem(content, newId);
            });


        }
    }

    public ObjectProperty<FontIcon> iconItemSelected() {
        return viewModel.iconItemSelected;
    }
}


