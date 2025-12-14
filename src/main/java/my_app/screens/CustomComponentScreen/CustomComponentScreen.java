package my_app.screens.CustomComponentScreen;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import my_app.components.CustomComponent;
import my_app.contexts.TranslationContext;
import my_app.screens.Home.Home;
import my_app.screens.Home.HomeViewModel;
import toolkit.Component;

import java.util.function.Function;

public class CustomComponentScreen extends BorderPane {
    @Component
    MenuBar mb = new MenuBar();

    @Component
    Home home;

    TranslationContext.Translation translation = TranslationContext.instance().get();
    TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    public CustomComponentScreen(Stage stage, HomeViewModel homeViewModel, Function<CustomComponentScreen, CustomComponentScreen> callable) {
        callable.apply(this);

        this.home = new Home(stage, true);
        Menu menu = new Menu(translation.menu());
        MenuItem is = new MenuItem(translation.save());

        menu.getItems().add(is);
        mb.getMenus().add(menu);

        is.setOnAction(_ -> {
            //todo ainda vou fazer logica aqui
            //só preciso criar o objeto customcomponent e adicionar ao datamap da viewmodel principal

            final var contentCanva = home.getCanva();
            CustomComponent newCustomComponent = new CustomComponent(homeViewModel);
            newCustomComponent.setStyle(contentCanva.getStyle());
            newCustomComponent.setPrefHeight(contentCanva.getPrefHeight());
            newCustomComponent.setPrefWidth(contentCanva.getPrefWidth());

            newCustomComponent.getChildren().addAll(contentCanva.getChildren());

            homeViewModel.addComponent(englishBase.customComponent(), newCustomComponent);

            System.out.println(
                    "Componente personalizado criado e adicionado ao sistema com ID: " + newCustomComponent.getId());

            stage.close();
        });

        this.setTop(mb);
        this.setCenter(home);
    }
}
