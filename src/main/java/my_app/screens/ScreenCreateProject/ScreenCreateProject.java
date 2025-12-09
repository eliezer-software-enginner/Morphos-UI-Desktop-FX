package my_app.screens.ScreenCreateProject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.data.Commons;
import my_app.themes.Typography;
import toolkit.Component;
import toolkit.Toast;

import java.io.File;
import java.util.List;

public class ScreenCreateProject extends VBox {

    ScreenCreateProjectViewModel viewModel;
    Toast toast = new Toast();

    // Novo: Container para o Card de Criação de Projeto (usado como modal)
    private final VBox newProjectCard;

    public ScreenCreateProject(Stage primaryStage) {
        this.viewModel = new ScreenCreateProjectViewModel(primaryStage);

        // Inicializa o card de criação, mas o mantém oculto inicialmente
        this.newProjectCard = createNewProjectCard();
        this.newProjectCard.setVisible(false);
        this.newProjectCard.setManaged(false); // Não ocupa espaço no layout quando invisível

        // Layout Principal: Agora é um VBox que empilha os componentes de cima para baixo
        getChildren().addAll(
                createHeader(),
                this.newProjectCard, // O card de criação de projeto (oculto)
                toast,
                createMainContent() // Botão de Adicionar + Lista de Recentes
        );

        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("background-color");
    }

    private VBox createHeader() {
        var header = new VBox(5,
                Typography.title(Commons.AppName),
                Typography.subtitle(Commons.AppVersion)
        );
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 30, 0)); // Espaçamento inferior
        return header;
    }

    private VBox createMainContent() {
        var createBtn = Components.ButtonPrimary("Criar Novo Projeto");
        createBtn.getStyleClass().add("create-new-project-btn");

        // Ação: Ao clicar, exibe/oculta o card de criação
        createBtn.setOnAction(e -> {
            boolean visible = !newProjectCard.isVisible();
            newProjectCard.setVisible(visible);
            newProjectCard.setManaged(visible);
        });

        var recentProjectsPane = createRecentProjectsList();

        var root = new VBox(20, createBtn, recentProjectsPane);
        root.setPadding(new Insets(0, 100, 0, 100)); // Adiciona padding horizontal para centralizar o conteúdo
        root.setAlignment(Pos.TOP_CENTER);
        return root;
    }


    // Renomeado e adaptado para ser uma lista vertical
    @Component
    private VBox createRecentProjectsList() {
        var title = Typography.subtitle("Projetos Recentes");
        title.getStyleClass().add("recent-projects-title");

        var projectListContainer = new TilePane();
        projectListContainer.setVgap(5);
        projectListContainer.setHgap(10);
        projectListContainer.getStyleClass().add("recent-projects-list");
        projectListContainer.setPrefColumns(4);
        final var scroll = new ScrollPane();
        scroll.setStyle("-fx-background:transparent;");
        scroll.getStyleClass().add("background-color");
        scroll.setContent(projectListContainer);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);


        // Listener reativo
        this.viewModel.recentProjectsProperty.addListener((obs, oldList, newList) -> {
            updateRecentProjectsUI(projectListContainer, newList);
        });

        // Dispara o estado inicial
        updateRecentProjectsUI(projectListContainer, this.viewModel.recentProjectsProperty.get());

        // O layout agora foca apenas na lista de projetos
        var root = new VBox(10, title, scroll);
        root.setPadding(new Insets(20, 0, 0, 0));
        return root;
    }

    // Antigo createNewProjectPane() renomeado para 'Card' e simplificado
    @Component
    private VBox createNewProjectCard() {
        var title = Typography.subtitle("Criar novo projeto");
        var input = new TextField();
        var errorContainer = new VBox();

        // Botão de criação, com botão secundário para fechar o modal/card
        var btnCreate = Components.ButtonPrimary("Criar e Abrir");
        var btnCancel = Components.ButtonSecondary("Cancelar");

        var buttonBar = new HBox(10, btnCancel, btnCreate);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        var root = new VBox(10, title, input, errorContainer, buttonBar);

        input.getStyleClass().add("project-name-input");
        root.getStyleClass().addAll("card-create-project", "new-project-card");

        // Data Binding
        input.textProperty().bindBidirectional(this.viewModel.inputTextProperty);

        // Event Listener: Criação
        btnCreate.setOnAction(ev -> this.viewModel.handleClickCreateProject());

        // Event Listener: Cancelamento (fecha o card)
        btnCancel.setOnAction(ev -> {
            newProjectCard.setVisible(false);
            newProjectCard.setManaged(false);
        });

        // Reativação da lógica de erro (que foi movida para o createNewProjectCard)
        this.viewModel.errorMessageProperty.addListener((obs, oldVal, newVal) -> {
            errorContainer.getChildren().clear();
            if (newVal != null && !newVal.isEmpty()) {
                errorContainer.getChildren().setAll(Typography.error(newVal));
            }
        });

        // Listener para Toast (manter no construtor principal ou em um método separado)
        // ... (o listener do Toast não precisa ser repetido aqui)

        return root;
    }

    /**
     * Lógica para construir a lista de projetos na UI (melhorado com Card de Projeto).
     */
    private void updateRecentProjectsUI(TilePane container, List<String> projects) {
        container.getChildren().clear();

        if (projects == null || projects.isEmpty()) {
            container.getChildren().add(Typography.body("Nenhum projeto recente encontrado."));
            return;
        }

        for (String path : projects) {

            if (path == null) {
                container.getChildren().add(Typography.caption("Error"));
                continue;
            }

            // 1. Extrair nome e caminho
            File projectFile = new File(path);
            String name = projectFile.getName().replace(".json", "");
            String parentDir = projectFile.getParent();

            // 2. Criar a representação do item (um HBox estilizado)
            var nameLabel = Typography.body(name);
            var pathLabel = Typography.caption(parentDir);

            var projectInfo = new VBox(3, nameLabel, pathLabel);

            var openButton = Components.ButtonPrimary("Abrir");
            openButton.getStyleClass().add("open-project-link");

            var item = new HBox(10, projectInfo, openButton);
            item.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(projectInfo, javafx.scene.layout.Priority.ALWAYS); // Faz a info ocupar o máximo de espaço

            item.getStyleClass().add("recent-project-item");

            // Comando: Chama a função de abrir na VM
            item.setOnMouseClicked(e -> this.viewModel.handleOpenExistingProject(path));
            openButton.setOnAction(e -> this.viewModel.handleOpenExistingProject(path));

            container.getChildren().add(item);
        }
    }
}