package my_app.screens.Home.components.leftside;

import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import my_app.contexts.ComponentsContext;
import my_app.screens.Home.Home;
import toolkit.Component;
import toolkit.theme.MaterialTheme;

public class OptionHeader extends HBox {

    @Component
    Label label = new Label();

    @Component
    Region spacer = new Region();

    @Component
    Button btnAdd = new Button();

    ComponentsContext componentsContext;
    MaterialTheme theme = MaterialTheme.getInstance();

    public OptionHeader(
            LeftSide.Field field,
            Home home, BooleanProperty expanded,
            ComponentsContext componentsContext) {

        this.componentsContext = componentsContext;

        label.setText(field.name());

        getChildren().add(label);
        getChildren().add(spacer);
        getChildren().add(btnAdd);

        setup();
        styles();

        String type = field.nameEngligh().toLowerCase();

        btnAdd.setOnAction(_ -> {
            componentsContext.addComponent(type, home);
            componentsContext.headerSelected.set(type);
            expanded.set(true);
        });

        btnAdd.setOnMouseEntered(_ -> {
            updateIconColor(theme.getHoverColor());
        });
        btnAdd.setOnMouseExited(_ -> {
            updateIconColor(Color.WHITE);
        });

        componentsContext.nodeSelected.addListener((_, _, newSelected) -> {
            // Pega o tipo do item recém-selecionado (pode ser null)
            String newType = newSelected != null ? newSelected.type() : null;

            // Verifica se o tipo do novo item selecionado corresponde ao 'type' deste
            // OptionHeader
            if (newType != null && newType.equalsIgnoreCase(type)) {
                // Aplica a cor de seleção
                setStyle("-fx-background-color:%s;".formatted(theme.getFocusColorStyle()));
            } else if (newSelected == null) {
                // Se a seleção foi limpa (nodeSelected.set(null)), limpa o estilo
                if (!isHover()) {
                    setStyle("-fx-background-color: transparent;");
                }
            } else {
                // Se outro item de outro tipo foi selecionado, limpa o estilo deste
                if (!isHover()) {
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        // Ajuste em setOnMouseExited para usar o novo estado
        setOnMouseExited(_ -> {
            String selectedType = componentsContext.nodeSelected.get() != null
                    ? componentsContext.nodeSelected.get().type()
                    : null;

            if (selectedType == null || !selectedType.equalsIgnoreCase(type)) {
                setStyle("-fx-background-color: transparent;");
            } else {
                // Se for o selecionado, volta para a cor de seleção quando o mouse sair.
                setStyle("-fx-background-color:%s;".formatted(theme.getFocusColorStyle()));
            }
        });

        setOnMouseEntered(_ -> {
            setStyle("-fx-background-color: %s;-fx-background-radius:10px;"
                    .formatted(theme.getHoverColorSecondaryStyle()));
        });

        // Lógica de clique do botão Add Component
        btnAdd.setOnAction(_ -> {
            componentsContext.addComponent(type, home);
            // REMOVEMOS: ComponentsContext.headerSelected.set(type); // Não é mais
            // necessário se o AddComponent chamar SelectNode
            expanded.set(true);
        });

        // Lógica de clique no cabeçalho
        setOnMouseClicked(_ -> expanded.set(!expanded.get()));

    }

    private void updateIconColor(Color color) {
        var icon = FontIcon.of(
                AntDesignIconsOutlined.PLUS,
                19,
                color);

        btnAdd.setGraphic(icon);
    }

    void setup() {
        this.setSpacing(5);
        this.setMaxWidth(150);
        this.setPadding(new Insets(5));
        this.setAlignment(Pos.CENTER_LEFT);

        var icon = FontIcon.of(
                AntDesignIconsOutlined.PLUS,
                19,
                Color.WHITE);

        btnAdd.setGraphic(icon);

        // label.setFont(Font.font(18));
        // label.setFont(App.FONT_BOLD);

        HBox.setHgrow(spacer, Priority.ALWAYS);
    }

    void styles() {
        btnAdd.setStyle("-fx-background-color:transparent;");
        label.setStyle("-fx-text-fill: #F8FAFC;-fx-font-size:15px;");
    }
}
