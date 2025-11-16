package my_app.scenes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled.*;


public class IconsScene extends Scene {
    ThemeManager themeManager = ThemeManager.Instance();

    @Component
    Text title = Typography.title("Icons");

    Map<String, List<Ikon>> iconsMap = Map.of("AntDesignIcons-Filled", List.of(ACCOUNT_BOOK, ALERT, ALIPAY_CIRCLE, ALIPAY_SQUARE, ALIWANGWANG, AMAZON_CIRCLE, AMAZON_SQUARE, ANDROID),
            "AntDesignIcons-Outlined", List.of(org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined.ACCOUNT_BOOK, org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined.ALERT));

    @Component
    TilePane header = new TilePane();

    @Component
    TilePane content = new TilePane(10, 10, Typography.body("Select a icon pack to view icons here"));
    @Component
    VBox layout = new VBox(title, Components.spacerVertical(10), header, Components.spacerVertical(10), content);

    StringProperty iconMapSelected = new SimpleStringProperty();
    public StringProperty iconIdSelected = new SimpleStringProperty();
    public ObjectProperty<FontIcon> iconItemSelected = new SimpleObjectProperty<>();

    Map<String, List<HBox>> itemsCreatedBuffer = new HashMap<>();

    private final Stage stage = new Stage();

    public IconsScene() {
        var screenSize = Commons.ScreensSize.LARGE;
        super(new VBox(), screenSize.width, screenSize.heigh);
        stage.setResizable(false);

        setRoot(layout);

        setup();
        styles();
    }

    void setup() {

        for (var entry : iconsMap.entrySet()) {
            var btn = Components.ButtonPrimary();
            btn.setText(entry.getKey());
            btn.setStyle("""
                    -fx-border-radius:7px;-fx-border-width:1px;-fx-border-color:gray;
                    -fx-padding:10px 15px;-fx-background-color:transparent;
                    """);

            btn.setOnMouseClicked(ev -> {
                iconMapSelected.set(entry.getKey());
            });
            header.getChildren().add(btn);

            //creating buffer
            var nodes = entry.getValue().stream().map(it -> createItem(it, entry.getKey())).toList();
            itemsCreatedBuffer.put(entry.getKey(), nodes);
        }


        iconMapSelected.addListener((_, _, newV) -> {
            //dont re-create childs anymore, i guess
            //iconsMap.get(newV).forEach(it -> content.getChildren().add(createItem(it, newV)));

            //new : only pass buffer created once to the content whithout recreation
            content.getChildren().setAll(itemsCreatedBuffer.get(newV));
            iconItemSelected.set(null);//clear highlight
            iconIdSelected.set(null);
        });

        iconIdSelected.addListener((_, _, newId) -> {
            for (Node node : content.getChildren()) {

                var classes = node.getStyleClass();
                classes.clear();

                if (newId != null && node.getId().equals(newId)) {
                    classes.add("icon-item-selected");
                } else {
                    classes.add("icon-item");
                }
            }
        });

        content.setPrefColumns(5);

        ThemeManager.Instance().addScene(this);
    }

    public void show() {
        stage.setScene(this);
        stage.show();
    }

    void styles() {
        // layout.setStyle("-fx-background-color:#15161A;");

        layout.setPadding(new Insets(20));

        Commons.UseDefaultStyles(this);
        layout.getStyleClass().add("background-color");
        header.setVgap(10);
        header.setHgap(10);
        header.setPrefColumns(8);
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


