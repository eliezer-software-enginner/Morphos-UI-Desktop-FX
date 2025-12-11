package my_app.screens.Home;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.StateJson_v3;
import my_app.screens.Home.components.RightSidev2;
import my_app.screens.Home.components.ScreenTab;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.screens.Home.components.leftside.LeftSide;
import my_app.themes.Typography;
import my_app.windows.AllWindows;
import toolkit.Component;

import static my_app.components.shared.UiComponents.MenuBarPrimary;

public class Home extends BorderPane {

    // --- DEPENDÊNCIAS ---
    private final HomeViewModel viewModel;
    private final TranslationContext.Translation translation = TranslationContext.instance().get();

    // --- COMPONENTES DE UI ---
    @Component
    private final MenuBar menuBar;

    @Component
    public LeftSide leftSide; // Mantemos público caso precise ser acessado externamente, mas idealmente seria privado

    @Component
    public ScrollPane editor = new ScrollPane();

    @Component
    public VBox canvaHolder = new VBox(5);

    @Component
    public HBox screensTabs = new HBox(5);

    // Wrapper para o RightSide para manter tamanho fixo
    private final StackPane rightWrapper = new StackPane();

    // Flag de configuração (mantida do seu código original)
    private final boolean openComponentScene;

    public Home(Stage stage, boolean openComponentScene) {
        this.openComponentScene = openComponentScene;

        // 1. Inicializa a ViewModel
        this.viewModel = new HomeViewModel(stage);

        // 2. Constrói a UI Estática (Menu, Layouts vazios)
        this.menuBar = createMenuBar();

        configLayout();
        configEditorStyle();

        // Montagem do Layout Principal
        setTop(menuBar);

        // O LeftSide e o RightSide serão criados quando o primeiro Canva for carregado
        // (via listener do activeCanva)

        canvaHolder.getChildren().addAll(screensTabs, editor);
        setCenter(canvaHolder);

        rightWrapper.setMinWidth(Region.USE_PREF_SIZE);
        rightWrapper.setMaxWidth(Region.USE_PREF_SIZE);
        setRight(rightWrapper);

        getStyleClass().add("surface-color");

        // 3. INICIA OS BINDINGS (A Mágica do MVVM)
        initBindings();

        // 4. Pede para a ViewModel carregar os dados iniciais

        viewModel.init(openComponentScene);
    }

    private void configLayout() {
        this.screensTabs.setAlignment(Pos.CENTER_LEFT);
    }

    private void configEditorStyle() {
        editor.setFitToWidth(false);
        editor.setFitToHeight(false);
        editor.getStyleClass().setAll("surface-color");

        // Lógica de estilo transparente (do seu código original)
        if (openComponentScene) {
            editor.setStyle("""
                        -fx-background-color: transparent;
                        -fx-background: transparent;
                    """);
        }
    }

    /**
     * Configura os ouvintes (listeners) que reagem às mudanças na ViewModel.
     */
    private void initBindings() {
        // A. Ouve mudanças na lista de abas (Adição/Remoção de telas)
        viewModel.screenTabs.addListener((ListChangeListener<StateJson_v3>) c -> {
            updateTabsUI();
        });

        // B. Ouve mudanças no Canva Ativo (Troca de aba ou carregamento inicial)
        viewModel.activeCanva.addListener((obs, oldCanva, newCanva) -> {
            if (newCanva != null) {
                updateEditorContent(newCanva);
            }
        });
    }

    /**
     * Reconstrói a barra de abas baseada na lista da ViewModel.
     */
    private void updateTabsUI() {
        screensTabs.getChildren().clear();

        for (var screen : viewModel.screenTabs) {
            // Cria a Tab passando a ViewModel (para lidar com cliques/exclusão)
            ScreenTab tab = new ScreenTab(screen, viewModel);
            screensTabs.getChildren().add(tab);
        }

        // Botão de Adicionar Nova Tela
        var btnAdd = Components.ButtonPrimary("+");
        btnAdd.setOnMouseClicked(ev -> viewModel.addScreen());
        screensTabs.getChildren().add(btnAdd);
    }

    /**
     * Atualiza a área central e as laterais quando o Canva ativo muda.
     */
    private void updateEditorContent(CanvaComponentV2 newCanva) {
        // 1. Atualiza o Centro (ScrollPane)
        editor.setContent(newCanva);

        // Aplica estilos específicos ao Canva se necessário (mantido do original)
        if (openComponentScene) {
            newCanva.setPrefSize(370, 250);
            var style = newCanva.getStyle();
            var updated = Commons.UpdateEspecificStyle(style, "-fx-background-color", "transparent");
            newCanva.setStyle(updated);
        }

        // 2. Recria o LeftSide com a nova referência do Canva
        this.leftSide = new LeftSide(newCanva, viewModel);
        // Se o LeftSide precisar ser notificado explicitamente (método updateCanva que você criou antes)
        this.leftSide.updateCanva(newCanva);
        setLeft(this.leftSide);

        // 3. Recria o RightSide com a nova referência do Canva
        RightSidev2 newRightSide = new RightSidev2(newCanva, viewModel);
        rightWrapper.getChildren().setAll(newRightSide);
    }

    // --- CRIAÇÃO DE MENUS (UI Factory Methods) ---
    // Agora a View é dona da aparência dos menus, delegando ações para a ViewModel

    private MenuBar createMenuBar() {
        MenuBar bar = MenuBarPrimary();
        bar.getMenus().setAll(
                createMenuOptions(),
                createMenuSettings(),
                createMenuDataTable(),
                createMenuUiPath()
        );
        return bar;
    }

    private Menu createMenuOptions() {
        Menu menu = new Menu();
        Label menuText = Typography.caption(translation.common().option());
        menu.setGraphic(menuText);
        menuText.getStyleClass().add("text-primary-color");

        MenuItem itemNovo = new MenuItem(translation.newProject());
        MenuItem itemSalvar = new MenuItem(translation.common().save());
        MenuItem itemLoad = new MenuItem(translation.common().load());
        MenuItem itemSair = new MenuItem(translation.exit());
        MenuItem itemContribute = new MenuItem(translation.optionsMenuMainScene().becomeContributor());

        menu.getItems().addAll(itemNovo, itemSalvar, itemLoad, itemSair, itemContribute);

        // BINDINGS DE AÇÃO: Chama métodos da ViewModel ou Janelas Globais
        itemNovo.setOnAction(_ -> AllWindows.showWindowForCreateNewProject());
        itemSalvar.setOnAction(_ -> viewModel.handleSave());
        itemLoad.setOnAction(_ -> viewModel.handleOpenExistingProject());
        itemSair.setOnAction(_ -> viewModel.exitProject());
        itemContribute.setOnAction(_ -> viewModel.handleBecomeContributor());

        return menu;
    }

    private Menu createMenuSettings() {
        Menu menu = new Menu();
        Label menuText = Typography.caption(translation.settings());
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(_ -> viewModel.handleClickMenuSettings());
        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    private Menu createMenuDataTable() {
        var menu = new Menu();
        Label menuText = Typography.caption("Data table");
        menu.setGraphic(menuText);

        menuText.setOnMouseClicked(ev -> viewModel.handleClickDataTable());

        return menu;
    }

    private Menu createMenuUiPath() {
        Menu menu = new Menu();
        Label menuText = Typography.caption("path of ui file");

        // Data Binding: O texto do menu muda automaticamente se a propriedade na VM mudar
        menuText.textProperty().bind(viewModel.uiPathProperty);

        menu.setGraphic(menuText);
        menuText.getStyleClass().add("text-primary-color");

        return menu;
    }

    public CanvaComponentV2 getCanva() {
        var content = this.editor.getContent();
        if (content == null) {
            throw new RuntimeException("Current canva is null");
        }

        if (content instanceof CanvaComponentV2 canva) {
            return canva;
        }
        throw new RuntimeException("Unexpected child of editor! Must be a canva");
    }
}