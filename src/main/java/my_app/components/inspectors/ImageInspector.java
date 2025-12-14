package my_app.components.inspectors;

import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.imageComponent.ImageBackgroundComponentv2;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.imageComponent.PreserveRatioComponentv2;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.HeightComponent;
import my_app.components.shared.WidthComponent;
import my_app.contexts.TranslationContext;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import toolkit.Component;

public class ImageInspector implements Inspector<ImageComponent> {

    private final ImageComponent component;
    private final HomeViewModel viewModel;

    @Component
    public VBox errorContainer = new VBox();

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public ImageInspector(ImageComponent component, HomeViewModel viewModel) {
        this.component = component;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                new WidthComponent(component),
                new HeightComponent(component),
                new PreserveRatioComponentv2(component),
                new ImageBackgroundComponentv2(component),
                Components.LabelWithComboBox("Clip", component, "clip-image-as-circle"),
                //Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(10),
                errorContainer,
                Components.spacerVertical(20),
                new ButtonRemoverComponent(component, this.viewModel)
                // new FitComponent(this)
        );
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {

        father.getChildren().setAll(
                Components.LayoutXYComponent(component),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), component, canva),
                Components.LabelWithTextContent("Variable name", component.name.get(), component.name::set)
        );
    }

    @Override
    public void layout(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(component),
                Components.ToogleSwithItemRow("Centralizar", component, canva)
        );
    }

}
