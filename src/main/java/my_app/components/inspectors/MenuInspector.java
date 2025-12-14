package my_app.components.inspectors;

import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.MenuComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.MenuDataEditorComponent;
import my_app.contexts.TranslationContext;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import toolkit.Component;

public class MenuInspector implements Inspector<MenuComponent> {

    private final MenuComponent component;
    private final HomeViewModel viewModel;

    @Component
    public VBox errorContainer = new VBox();

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public MenuInspector(MenuComponent component, HomeViewModel viewModel) {
        this.component = component;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Typography.subtitle("Menu Items"),
                new MenuDataEditorComponent(component.itemsState, this.viewModel, component), // Novo editor de lista
                new ButtonRemoverComponent(component, this.viewModel)
        );
    }


    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                //  Components.LabelWithTextContent(translation.variableName(), component.name.get(), component.name::set)
        );
    }

    @Override
    public void layout(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(component),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), component, canva)
        );
    }

}
