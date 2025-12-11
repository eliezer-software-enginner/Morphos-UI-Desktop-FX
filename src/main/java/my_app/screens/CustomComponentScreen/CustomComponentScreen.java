package my_app.screens.CustomComponentScreen;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import my_app.contexts.TranslationContext;
import my_app.screens.Home.Home;
import my_app.themes.ThemeManager;
import toolkit.Component;

import java.util.function.Function;

public class CustomComponentScreen extends BorderPane {
    public Stage stage = new Stage();

    @Component
    MenuBar mb = new MenuBar();

    @Component
    //Home home = new Home(stage, componentsContext, true);
    Home home = new Home(stage, true);

    ThemeManager themeManager = ThemeManager.Instance();
    TranslationContext.Translation translation = TranslationContext.instance().get();

    public CustomComponentScreen(Function<CustomComponentScreen, CustomComponentScreen> callable) {
        callable.apply(this);
        Menu menu = new Menu(translation.menu());
        MenuItem is = new MenuItem(translation.save());

        menu.getItems().add(is);
        mb.getMenus().add(menu);

        is.setOnAction(_ -> {
            // O 'home.canva' é o CanvaComponent com o conteúdo que o usuário desenhou (aqui
            // é o 'contentCanva').
            // var contentCanva = home.canva;
            // style-> "-fx-background-color:#1a4d4d;"
            // 1. Cria o CustomComponent
//            CustomComponent newCustomComponent = new CustomComponent(componentsContext, contentCanva);
//            newCustomComponent.setStyle(contentCanva.getStyle());
//            newCustomComponent.setPrefHeight(contentCanva.getPrefHeight());
//            newCustomComponent.setPrefWidth(contentCanva.getPrefWidth());
//            newCustomComponent.mainComponentsContext = mainComponentsContext;

            // 2. Transfere os filhos do canva temporário (home.canva) para o customComp.
            // **IMPORTANTE:** Isso move os Nodes, tirando-os do 'contentCanva'.
            // Se você precisar que os Nodes permaneçam no 'contentCanva', você precisa
            // CLONAR.
            //   newCustomComponent.getChildren().addAll(contentCanva.getChildren());

            // 3. Adiciona o nó à lista global e à sidebar.
            // O mainCanvaComponent aqui é usado apenas para a lógica interna (embora o
            // addCustomComponent não o use visualmente).

            //   mainComponentsContext.addCustomComponent(newCustomComponent, mainCanva);

            //   System.out.println(
            //            "Componente personalizado criado e adicionado ao sistema com ID: " + newCustomComponent.getId());

            stage.close();
        });

        this.setTop(mb);
        this.setCenter(home);
    }
}
