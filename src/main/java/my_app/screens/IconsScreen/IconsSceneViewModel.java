package my_app.screens.IconsScreen;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import my_app.components.Components;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconsSceneViewModel {
    private final ThemeManager themeManager = ThemeManager.Instance();
    public final Map<String, List<Ikon>> iconsMap = IconDatabase.ICONS;

    private final Map<String, List<HBox>> itemsCreatedBuffer = new HashMap<>();
    public final StringProperty iconMapSelected = new SimpleStringProperty();
    public StringProperty iconIdSelected = new SimpleStringProperty();
    public ObjectProperty<FontIcon> iconItemSelected = new SimpleObjectProperty<>();


    public void init() {
    }

    public void handleClickOnHeaderItem(Map.Entry<String, List<Ikon>> entry) {
        iconMapSelected.set(entry.getKey());
    }

    public void handleClickOnIconMap(TilePane content, String newV) {
        //new : only pass buffer created once to the content whithout recreation
        content.getChildren().setAll(itemsCreatedBuffer.get(newV));
        iconItemSelected.set(null);//clear highlight
        iconIdSelected.set(null);
    }

    public void highlightItem(TilePane content, String newId) {
        for (Node node : content.getChildren()) {

            var classes = node.getStyleClass();
            classes.clear();

            if (newId != null && node.getId().equals(newId)) {
                classes.add("icon-item-selected");
            } else {
                classes.add("icon-item");
            }
        }
    }

    public void createBufferOfCurrentEntry(Map.Entry<String, List<Ikon>> entry) {
        var nodes = entry.getValue().stream().map(it -> createItem(it, entry.getKey())).toList();
        itemsCreatedBuffer.put(entry.getKey(), nodes);
    }

    @Component
    public void createItemForHeader(TilePane tilePane, Map.Entry<String, List<Ikon>> entry) {
        var btn = Components.ButtonPrimary();
        btn.setText(entry.getKey());
        btn.setStyle("""
                -fx-border-radius:7px;-fx-border-width:1px;-fx-border-color:gray;
                -fx-padding:10px 15px;-fx-background-color:transparent;
                """);

        btn.setOnMouseClicked(ev -> handleClickOnHeaderItem(entry));
        tilePane.getChildren().add(btn);
    }

    @Component
    HBox createItem(Ikon ikon, String key) {
        var icon = FontIcon.of(ikon, 16, themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
        var title = Typography.caption(ikon.toString());
        var root = new HBox(icon, title);

        String id = key + ";" + ikon;
        root.setId(id);

        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);

        root.getStyleClass().add("icon-item");

        root.setOnMouseClicked(ev -> {
            IO.println(id);
            iconIdSelected.set(id);
            iconItemSelected.set(icon);
        });
        return root;
    }
}
