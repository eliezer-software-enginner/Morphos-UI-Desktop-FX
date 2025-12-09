package my_app.screens.Home.components.leftside;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import my_app.components.Components;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.data.Commons;
import my_app.themes.ThemeManager;
import my_app.themes.Typography;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import toolkit.Component;
import toolkit.theme.MaterialTheme;

public class OptionHeader extends HBox {

    private final HomeViewModel viewModel;
    @Component
    Label label;

    @Component
    Region spacer = new Region();

    @Component
    Button btnAdd = Components.ButtonPrimary();//plus icon
    MaterialTheme theme = MaterialTheme.getInstance();
    ThemeManager themeManager = ThemeManager.Instance();

    // Adicione um campo para armazenar a referência atualizada do Canva
    private CanvaComponentV2 currentCanva;

    public OptionHeader(
            LeftSide.Field field,
            CanvaComponentV2 currentCanva, BooleanProperty expanded,
            HomeViewModel viewModel) {

        this.viewModel = viewModel;
        // Armazena a referência inicial
        this.currentCanva = currentCanva;

        label = Typography.caption(field.name());

        getChildren().add(label);
        getChildren().add(spacer);
        getChildren().add(btnAdd);

        setup();
        styles();

        String type = field.nameEngligh().toLowerCase();

        btnAdd.setOnAction(_ -> {
            viewModel.addComponent(type);
            viewModel.headerSelected.set(type);
            expanded.set(true);
        });

        btnAdd.setOnMouseEntered(_ -> {
            updateIconColor(theme.getHoverColor());
        });
        btnAdd.setOnMouseExited(_ -> {
            updateIconColor(themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
        });


        nodeSelectedListener(viewModel, type);

        // Ajuste em setOnMouseExited para usar o novo estado
        setOnMouseExited(_ -> {
            String selectedType = viewModel.nodeSelected.get() != null
                    ? viewModel.nodeSelected.get().type()
                    : null;

            if (selectedType == null || !selectedType.equalsIgnoreCase(type)) {
                setStyle("-fx-background-color: transparent;");

                label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(),
                        "-fx-text-fill",
                        themeManager.themeIsWhite() ? "black" : "white")
                );

                updateIconColor(themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
            } else {
                // Se for o selecionado, volta para a cor de seleção quando o mouse sair.
                setStyle("-fx-background-color:%s;".formatted(theme.getFocusColorStyle()));
                label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(), "-fx-text-fill", "white"));
                updateIconColor(Color.WHITE);
            }
        });

        setOnMouseEntered(_ -> {
            setStyle("-fx-background-color: %s;-fx-background-radius:10px;-fx-text-fill:white;-fx-fill:white;"
                    .formatted(theme.getHoverColorSecondaryStyle()));

            label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(),
                    "-fx-text-fill",
                    "white")
            );

            updateIconColor(Color.WHITE);
        });

        // Lógica de clique do botão Add Component
        btnAdd.setOnAction(_ -> {
            viewModel.addComponent(type);
            // REMOVEMOS: ComponentsContext.headerSelected.set(type); // Não é mais
            // necessário se o AddComponent chamar SelectNode
            expanded.set(true);
        });

        // Lógica de clique no cabeçalho
        setOnMouseClicked(_ -> expanded.set(!expanded.get()));

        ThemeManager.Instance().themeProperty.addListener((_, _, newState) -> {
            label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(),
                    "-fx-text-fill",
                    themeManager.themeIsWhite() ? "black" : "white")
            );

            updateIconColor(themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
        });
    }

    private void nodeSelectedListener(HomeViewModel viewModel, String type) {
        viewModel.nodeSelected.addListener((_, _, newSelected) -> {
            // Pega o tipo do item recém-selecionado (pode ser null)
            String newType = newSelected != null ? newSelected.type() : null;

            // Verifica se o tipo do novo item selecionado corresponde ao 'type' deste
            // OptionHeader
            if (newType != null && newType.equalsIgnoreCase(type)) {
                // Aplica a cor de seleção
                setStyle("-fx-background-color:%s;".formatted(theme.getFocusColorStyle()));
                updateIconColor(Color.WHITE);
                label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(),
                        "-fx-text-fill", "white")
                );
            } else if (newSelected == null) {
                // Se a seleção foi limpa (nodeSelected.set(null)), limpa o estilo
                if (!isHover()) {
                    setStyle("-fx-background-color: transparent;");
                }
            } else {
                // Se outro item de outro tipo foi selecionado, limpa o estilo deste
                if (!isHover()) {
                    setStyle("-fx-background-color: transparent;");
                    updateIconColor(themeManager.themeIsWhite() ? Color.BLACK : Color.WHITE);
                    label.setStyle(Commons.UpdateEspecificStyle(label.getStyle(),
                            "-fx-text-fill",
                            themeManager.themeIsWhite() ? "black" : "white")
                    );
                }
            }
        });
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

        HBox.setHgrow(spacer, Priority.ALWAYS);
    }

    void styles() {
        //btnAdd.setStyle("-fx-background-color:transparent;");
        var styleUpdated = Commons.UpdateEspecificStyle(btnAdd.getStyle(), "-fx-background-color", "transparent");
        btnAdd.setStyle(styleUpdated);

        //label.setStyle("-fx-text-fill: #F8FAFC;-fx-font-size:15px;");
    }


    public void updateCanva(CanvaComponentV2 newCanva) {
        this.currentCanva = newCanva;
    }
}
