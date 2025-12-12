package my_app.screens.Home.components.canvaComponent;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import my_app.FileManager;
import my_app.components.*;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.imageComponent.ImageComponentv2;
import my_app.components.shared.HeightComponent;
import my_app.components.shared.WidthComponent;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.screens.Home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import static my_app.screens.Home.HomeViewModel.SelectedComponent;

public class CanvaComponentV2 extends Pane implements ViewContractv2<CanvaComponentDatav2> {

    // --- CONSTANTES ---
    private static final String TRANSPARENT_BG_TYPE = "transparent";

    // CSS para o fundo quadriculado (tons de cinza claro/branco)
// CORREÇÃO ESSENCIAL: O valor de -fx-background-image deve estar em uma única linha contínua
    // ou formatado de forma que o parser JavaFX não introduza erros com as quebras de linha/indentação.
    // Garantindo que todos os gradientes sejam separados apenas por vírgulas.
// CORREÇÃO ESSENCIAL: O valor CSS foi condensado em uma única string contínua,
// separando as propriedades por ponto e vírgula e garantindo que a lista de gradientes
// em -fx-background-image seja uma lista coesa separada apenas por vírgulas.
    // REMOVIDA: A constante CHECKERED_BG_STYLE foi removida e substituída por um Node.

    // NOVO: O Node que representa o fundo quadriculado (inicializado uma vez)
    private final Region checkerboardBackground = createCheckerboardBackground();
    // --- ESTADO INTERNO ---
    private String currentBgType = "color"; // Novo: Para rastrear o tipo de fundo
    private String currentBgContent = Commons.CanvaBgColorDefault; // Novo: Para armazenar o conteúdo (cor/url)
    boolean isDeleted = false;

    public String name;
    public String screenFatherId;

    //Aqui é o nome de viewmodel que será gerado no json
    public String viewModelName;

    TranslationContext.Translation translation = TranslationContext.instance().get();

    HomeViewModel viewModel;
    private boolean enableAnimation = true;

    public List<TextComponentData> text_components = new ArrayList<>();
    public List<ButtonComponentData> button_components = new ArrayList<>();
    public List<ImageComponentData> image_components = new ArrayList<>();
    public List<InputComponentData> input_components = new ArrayList<>();
    public List<MenuComponentData> menu_components = new ArrayList<>();
    // REMOVIDA: public List<FlexComponentData> flex_components = new ArrayList<>();

    // ADICIONADA: Nova lista de componentes de Coluna
    public List<ColumnComponentData> column_components = new ArrayList<>();

    public List<CustomComponentData> custom_components = new ArrayList<>();

    public CanvaComponentV2(HomeViewModel viewModel) {
        this.viewModel = viewModel;

        config();

        setOnMouseClicked(e -> {
            if (e.getTarget() == this) { // só dispara se clicou no fundo do Canva
                // 1. Defina o tipo para o Canva (ex: "canva", "main", etc.)
                String canvaType = "canva"; // Use uma string consistente

                // 2. Crie o novo objeto SelectedComponent
                SelectedComponent newSelection = new SelectedComponent(canvaType, this);

                // 3. Defina a propriedade com o objeto correto
                this.viewModel.nodeSelected.set(newSelection);
                System.out.println("Canva selecionado");
            }
        });

    }


    /**
     * Aplica os dados do Canva, incluindo a nova lógica para fundo transparente.
     */
    @Override
    public void applyData(CanvaComponentDatav2 data) {
        if (data != null) {

            IO.println("deve mostrar um texto: " + data.text_components.size());

            setPrefWidth(data.width);
            setPrefHeight(data.height);

            setId(data.identification);
            this.screenFatherId = data.screenFatherId;
            this.name = data.name;
            this.viewModelName = data.viewModelName;

            // Ajustando o padding
            setPadding(
                    new Insets(data.padding_top, data.padding_right, data.padding_bottom, data.padding_left));

            var bgType = data.bg_type;
            var bgContent = data.bgContent;

            // --- NOVO TRATAMENTO DE FUNDO ---
            if (bgType.equals("color")) {
                setStyle("-fx-background-color:%s;".formatted(bgContent));
            } else if (bgType.equals("image")) {
                setStyle("-fx-background-image: url('" + bgContent + "');" +
                        "-fx-background-size: cover; -fx-background-position: center;");
            } else if (bgType.equals(TRANSPARENT_BG_TYPE)) {
                // NOVO: Aplica o fundo quadriculado
                applyCheckeredBackground();
            }
            // ---------------------------------

            // Atualiza o estado interno para o getData()
            this.currentBgType = bgType;
            this.currentBgContent = bgContent;

            // ... (Loop de componentes mantido) ...
            // (Seus loops de componentes aqui)
            for (TextComponentData it : data.text_components) {
                var comp = new TextComponent(it.text(), this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("text", comp);
                this.addElementDragable(comp, false);
            }

            for (ButtonComponentData it : data.button_components) {
                var comp = new ButtonComponent(this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("button", comp);
                this.addElementDragable(comp, false);
            }

            for (ImageComponentData it : data.image_components) {
                var comp = new ImageComponentv2(this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("image", comp);
                this.addElementDragable(comp, false);
            }

            for (InputComponentData it : data.input_components) {
                var comp = new InputComponent(this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("input", comp);
                this.addElementDragable(comp, false);
            }

            for (CustomComponentData it : data.custom_components) {
                var comp = new CustomComponent(this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("custom component", comp);
                if (it.in_canva) {
                    this.addElementDragable(comp, false);
                }
            }

            for (MenuComponentData it : data.menu_components) {
                var comp = new MenuComponent(this.viewModel, this);
                comp.applyData(it);
                viewModel.addItemOnDataMap("menu component", comp);
                this.addElementDragable(comp, false);
            }

            //todo finalizar o restante
        }
    }


    public void addElementDragable(Node node, boolean putInCenter) {
        //evitar adição de no duplicado
        for (Node child : getChildren()) {
            // Se o filho for o TilePane de fundo, ignore-o e continue a checagem.
            if (child == checkerboardBackground) {
                continue;
            }

            // Checagem de duplicação para componentes válidos (que devem ter ID)
            if (node.getId() != null && node.getId().equals(child.getId())) {
                IO.println("Node already added in canva!");
                return;
            }
        }

        // posição inicial centralizada
        double relX = 0.5;
        double relY = 0.5;

        if (putInCenter) {
            node.setLayoutX((getWidth() - node.prefWidth(-1)) * relX);
            node.setLayoutY((getHeight() - node.prefHeight(-1)) * relY);
        }

        enableDrag(node, relX, relY);

        node.setOnMouseClicked(e -> {
            this.viewModel.selectNode(node);
            Shake(node);
        });

        AnimateOnEntry(node);

        getChildren().add(node);
    }

    void AnimateOnEntry(Node node) {
        if (this.enableAnimation) {
            ScaleTransition st = new ScaleTransition(Duration.millis(400), node);
            st.setFromX(0.5);
            st.setFromY(0.5);
            st.setToX(1);
            st.setToY(1);

            st.play();
        }

    }

    // achacoalhar
    public void Shake(Node node) {
        if (this.enableAnimation) {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -1)),
                    new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 1)),
                    new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), -1)),
                    new KeyFrame(Duration.millis(400), new KeyValue(node.translateXProperty(), 1)),
                    new KeyFrame(Duration.millis(500), new KeyValue(node.translateXProperty(), -1)),
                    new KeyFrame(Duration.millis(600), new KeyValue(node.translateXProperty(), 0)));
            timeline.setCycleCount(1);
            timeline.play();
        }

    }

    private void enableDrag(Node node, double relX, double relY) {
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];

        node.setOnMousePressed(e -> {
            offsetX[0] = e.getSceneX() - node.getLayoutX();
            offsetY[0] = e.getSceneY() - node.getLayoutY();
        });

        node.setOnMouseDragged(e -> {
            double x = e.getSceneX() - offsetX[0];
            double y = e.getSceneY() - offsetY[0];

            // pega as dimensões reais do node
            var bounds = node.getBoundsInLocal();
            double nodeWidth = bounds.getWidth();
            double nodeHeight = bounds.getHeight();

            // clamp para garantir que fique 100% dentro do canva
            x = Math.max(0, Math.min(x, getWidth() - nodeWidth));
            y = Math.max(0, Math.min(y, getHeight() - nodeHeight));

            node.setLayoutX(x);
            node.setLayoutY(y);

            node.getProperties().put("relX", x / (getWidth() - nodeWidth));
            node.getProperties().put("relY", y / (getHeight() - nodeHeight));
        });

        // quando o canva for redimensionado, reposiciona proporcionalmente
        widthProperty().addListener((obs, oldW, newW) -> updateRelativePosition(node));
        heightProperty().addListener((obs, oldH, newH) -> updateRelativePosition(node));
    }

    private void updateRelativePosition(Node node) {
        Object relX = node.getProperties().get("relX");
        Object relY = node.getProperties().get("relY");

        if (relX instanceof Double && relY instanceof Double) {
            var bounds = node.getBoundsInLocal();
            double nodeWidth = bounds.getWidth();
            double nodeHeight = bounds.getHeight();

            node.setLayoutX((Double) relX * (getWidth() - nodeWidth));
            node.setLayoutY((Double) relY * (getHeight() - nodeHeight));

        }
    }

    /**
     * Atualização do painel de aparência.
     */
    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {

        // Color Picker (Ação atualizada para rastrear estado e remover o fundo Node)
        ColorPicker bgColorPicker = new ColorPicker(
                Color.web(
                        Commons.getValueOfSpecificField(getStyle(), "-fx-background-color")));
        bgColorPicker.setOnAction(e -> {
            Color c = bgColorPicker.getValue();
            String hexColor = Commons.ColortoHex(c);

            removeCheckeredBackground(); // Remove o quadriculado
            setStyle("-fx-background-color:%s;".formatted(hexColor));

            // Atualiza o estado interno
            this.currentBgType = "color";
            this.currentBgContent = hexColor;
        });

        // Botão para escolher imagem do sistema (Ação atualizada para remover o fundo Node)
        Button chooseImgBtn = new Button("Choose Image...");
        chooseImgBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            var file = fc.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();

                removeCheckeredBackground(); // Remove o quadriculado
                setStyle("-fx-background-image: url('" + uri + "'); " +
                        "-fx-background-size: cover; -fx-background-position: center;");

                // Atualiza o estado interno
                this.currentBgType = "image";
                this.currentBgContent = uri;
            }
        });

        // Botão para tornar o fundo transparente (Chama o novo método)
        Button transparentBtn = new Button("Set Transparent");
        transparentBtn.setOnAction(e -> {
            applyCheckeredBackground();
            // applyCheckeredBackground() já atualiza o estado interno
        });


        // Campo para URL (Ação atualizada para remover o fundo Node)
        TextField urlField = new TextField();
        urlField.setPromptText("Paste URl of image");
        Button applyUrl = new Button("Apply URL");
        applyUrl.setOnAction(_ -> {
            String url = urlField.getText();
            if (url != null && !url.isBlank()) {

                removeCheckeredBackground(); // Remove o quadriculado
                setStyle("-fx-background-image: url('" + url + "'); " +
                        "-fx-background-size: cover; -fx-background-position: center;");

                // Atualiza o estado interno
                this.currentBgType = "image";
                this.currentBgContent = url;
            }
        });

        // Configura o layout do painel de aparência
        father.getChildren().setAll(
                bgColorPicker,
                chooseImgBtn,
                urlField,
                applyUrl,
                Components.spacerVertical(10),
                transparentBtn,
                Components.spacerVertical(10),
                new WidthComponent(this),
                new HeightComponent(this));

    }

    /**
     * Remove o Node do fundo quadriculado se estiver presente.
     */
    private void removeCheckeredBackground() {
        this.getChildren().remove(checkerboardBackground);
    }

    /**
     * Aplica o fundo quadriculado (Node-based) e remove qualquer fundo CSS.
     */
    private void applyCheckeredBackground() {
        // 1. Garante que o fundo do Canva seja transparente (para que o TilePane apareça)
        this.setStyle("-fx-background-color: transparent;");

        // 2. Adiciona o TilePane do quadriculado como o primeiro filho (índice 0)
        //    para garantir que fique atrás de todos os elementos dragáveis.
        if (!this.getChildren().contains(checkerboardBackground)) {
            // Adiciona na posição 0 para ser o background
            IO.println("Aqui");
            this.getChildren().add(0, checkerboardBackground);

            // 3. ESSENCIAL: Força o redimensionamento imediato do TilePane após a adição.
            checkerboardBackground.resize(this.getWidth(), this.getHeight());
        }

        // NOVO: 4. Garante que todos os outros filhos (componentes de design)
        // estejam na frente, corrigindo o problema de Z-order dos componentes já carregados.
        // O TilePane permanece no fundo.
        for (Node child : this.getChildren()) {
            if (child != checkerboardBackground) {
                child.toFront();
            }
        }


        // 5. Atualiza o estado interno
        this.currentBgType = TRANSPARENT_BG_TYPE;
        this.currentBgContent = "";
    }

    /**
     * Implementação da nova estratégia: Cria um TilePane preenchido com Regions
     * para formar o padrão quadriculado, contornando o erro do parser CSS.
     */
    /**
     * Implementação da nova estratégia: Cria um TilePane preenchido com Regions
     * para formar o padrão quadriculado, contornando o erro do parser CSS.
     */
    private Region createCheckerboardBackground() {
        TilePane tileContainer = new TilePane(0, 0); // Espaçamento zero

        // O TilePane deve cobrir o tamanho do Canva, garantindo que ele se redimensione
        tileContainer.prefWidthProperty().bind(this.widthProperty());
        tileContainer.prefHeightProperty().bind(this.heightProperty());

        final int TILE_SIZE_FX = 16;

        String style1 = "-fx-background-color: #F0F0F0;"; // Cinza claro
        String style2 = "-fx-background-color: #CCCCCC;"; // Cinza escuro

        // ... (lógica de criação e estilização dos tiles mantida) ...
        final int MAX_COLUMNS = 100;
        final int MAX_ROWS = 100;
        final int TOTAL_TILES_COUNT = MAX_COLUMNS * MAX_ROWS;

        for (int k = 0; k < TOTAL_TILES_COUNT; k++) {
            Region tile = new Region();
            tile.setPrefSize(TILE_SIZE_FX, TILE_SIZE_FX);

            int row = k / MAX_COLUMNS;
            int col = k % MAX_COLUMNS;

            if ((row + col) % 2 == 0) {
                tile.setStyle(style1);
            } else {
                tile.setStyle(style2);
            }
            tileContainer.getChildren().add(tile);
        }

        // Faz o Node ser ignorado pelos cálculos de layout dos elementos irmãos
        tileContainer.setManaged(false);

        // CORREÇÃO ESSENCIAL: Permite que eventos de mouse passem para o nó subjacente.
        tileContainer.setMouseTransparent(true); // <--- ESTA É A LINHA QUE RESOLVE

        // ESSENCIAL: Adiciona listeners para forçar o TilePane a se redimensionar
        this.widthProperty().addListener((obs, oldW, newW) -> {
            tileContainer.resize(newW.doubleValue(), this.getHeight());
        });

        this.heightProperty().addListener((obs, oldH, newH) -> {
            tileContainer.resize(this.getWidth(), newH.doubleValue());
        });

        return tileContainer;
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().clear();
    }

    @Override
    public void otherSettings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().addAll(Components.LabelWithInputAndButton(
                        translation.screenName(), translation.update(),
                        this, "screen-name", () -> {
                            FileManager.updateScreenNameInProject(screenFatherId, "name", name);
                            //  viewModel.toggleRefreshScreenTabs();
                        }),
                Components.LabelWithInputAndButton(
                        "view model name", translation.update(),
                        this, "view-model-name", () -> {
                            FileManager.updateScreenNameInProject(screenFatherId, "viewModelName",
                                    this.viewModelName);
                            //  viewModel.toggleRefreshScreenTabs();
                        })
        );
    }

    void config() {
        setBorder(new Border(
                new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        null,
                        new BorderWidths(1))));

        // setPrefHeight(Double.MAX_VALUE);
        // setMaxWidth(Double.MAX_VALUE);

        setPrefSize(Commons.CanvaWidthDefault, Commons.CanvaHeightDefault); // tamanho inicial padrão

        setPadding(new Insets(0));

        setStyle("-fx-background-color:%s;".formatted(Commons.CanvaBgColorDefault));

        setId(String.valueOf(System.currentTimeMillis()));

        // ComponentsContext.nodeSelected.set(this);

        String canvaType = "canva"; // Use uma string consistente

        // 2. Cria o novo objeto SelectedComponent
        final var newSelection = new SelectedComponent(canvaType, this);

        // 3. Define a propriedade com o objeto correto
        this.viewModel.nodeSelected.set(newSelection);

    }

    @Override
    public CanvaComponentDatav2 getData() {

        Insets padding = this.getPadding();
        int paddingTop = (int) padding.getTop();
        int paddingRight = (int) padding.getRight();
        int paddingBottom = (int) padding.getBottom();
        int paddingLeft = (int) padding.getLeft();

        double width = this.getPrefWidth();
        double height = this.getPrefHeight();

        // --- NOVO: Usa os campos de estado para o fundo ---
        String bgType = this.currentBgType;
        String bgContent = this.currentBgContent;

        // Se a cor atual for transparente, o getData retorna o tipo correto
        // A lógica complexa de parse de CSS foi evitada usando os campos de estado.
        // ----------------------------------------------------

        return new CanvaComponentDatav2(
                paddingTop, paddingRight, paddingBottom, paddingLeft, width, height, bgType,
                bgContent, this.getId(), null, 0, 0, isDeleted, this.name,
                this.screenFatherId,
                this.viewModelName,

                text_components,
                button_components,
                image_components,
                input_components,
                column_components,
                custom_components,
                menu_components
        );
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

    public void disableAnimation() {
        this.enableAnimation = false;
    }
}
