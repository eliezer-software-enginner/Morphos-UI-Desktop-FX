package my_app.screens.IconsScreen;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;

import java.lang.IO;
import java.util.Map;
import java.util.function.Function;


public class IconsScreen extends VBox {
    @Component
    Text title = Typography.title("Icons");

    @Component
    VBox header = new VBox(10);

    TranslationContext.Translation translation = TranslationContext.instance().get();
    private final ThemeManager themeManager = ThemeManager.Instance();


    @Component
    TilePane content = new TilePane(10, 10, Typography.body(translation.selectIconsPackToViewIconsHere()));

    @Component
    ScrollPane scrollPane = new ScrollPane(content);

    private final IconsScreenViewModel viewModel = new IconsScreenViewModel();

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
        var separator = new Separator();
        header.getChildren().add(separator);

        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);

        content.setPrefColumns(5);

        // --- INICIALIZAÇÃO E CRIAÇÃO DE BUFFER ---
        for (var entry : viewModel.iconsMap.entrySet()) {
            // 1. Cria o botão de cabeçalho na View e passa o comando (handleClickOnHeaderItem)
            createItemForHeader(tilepane, entry);

            // 2. Cria o buffer de ícones (que são Nodes da View)
            var nodes = entry.getValue().stream().map(it -> createItem(it, entry.getKey())).toList();
            viewModel.itemsCreatedBuffer().put(entry.getKey(), nodes); // VM armazena o buffer de Nodes
        }

        // --- LISTENERS MVVM: View reage ao estado da ViewModel ---

        // 1. Reage à mudança do mapa de ícones (lista principal)
        viewModel.currentIconItems.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                // A View manipula seu próprio TilePane (content)
                content.getChildren().setAll(newV);
            }
        });

        // 2. Reage à mudança do ícone selecionado (highlight)
        viewModel.iconIdSelected.addListener((_, oldId, newId) -> {
            highlightItem(content, newId); // View chama a lógica de estilo
        });
    }

    // MVVM CORRIGIDO: Lógica de manipulação de UI (classes CSS) está na View
    private void highlightItem(TilePane content, String newId) {
        for (Node node : content.getChildren()) {
            var classes = node.getStyleClass();
            classes.clear();

            if (newId != null && node.getId() != null && node.getId().equals(newId)) {
                classes.add("icon-item-selected");
            } else {
                classes.add("icon-item");
            }
        }
    }

    // --- FÁBRICAS DE COMPONENTES (MOVidas da ViewModel) ---

    // 1. Cria item do cabeçalho
    @Component
    private void createItemForHeader(TilePane tilePane, Map.Entry<String, java.util.List<Ikon>> entry) {
        var btn = Components.ButtonPrimary();
        btn.setText(entry.getKey());
        btn.setStyle("""
                -fx-border-radius:7px;-fx-border-width:1px;-fx-border-color:gray;
                -fx-padding:10px 15px;-fx-background-color:transparent;
                """);

        // Comando: Chama o método de comando na ViewModel
        btn.setOnMouseClicked(ev -> viewModel.handleClickOnHeaderItem(entry.getKey()));
        tilePane.getChildren().add(btn);
    }

    // 2. Cria item de ícone individual
    @Component
    private HBox createItem(Ikon ikon, String key) {
        var icon = FontIcon.of(ikon, 16, themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
        var title = Typography.caption(ikon.toString());
        var root = new HBox(icon, title);

        String id = key + ";" + ikon;
        root.setId(id);

        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("icon-item");

        // Comando: Atualiza o estado na ViewModel
        root.setOnMouseClicked(ev -> {

            java.lang.IO.println(id);
            IO.println(id);
            viewModel.iconIdSelected.set(id);
            viewModel.iconItemSelected.set(icon);
        });
        return root;
    }


    public ObjectProperty<FontIcon> iconItemSelected() {
        return viewModel.iconItemSelected;
    }
}