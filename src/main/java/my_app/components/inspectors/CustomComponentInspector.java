package my_app.components.inspectors;

import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.CustomComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.TranslationContext;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import toolkit.Component;

public class CustomComponentInspector implements Inspector<CustomComponent> {

    private final CustomComponent component;
    private final HomeViewModel viewModel;

    @Component
    public VBox errorContainer = new VBox();

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public CustomComponentInspector(CustomComponent component, HomeViewModel viewModel) {
        this.component = component;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                // ChildHandlerComponent para onEmptyComponentState removido daqui, se não era usado
                new ButtonRemoverComponent(component, this.viewModel)
        );
    }


    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                //Components.LabelWithTextContent(translation.variableName(), component.name.get(), component.name::set)
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
