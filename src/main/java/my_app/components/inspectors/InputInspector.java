package my_app.components.inspectors;

import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.InputComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.TranslationContext;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import toolkit.Component;

public class InputInspector implements Inspector<InputComponent> {

    private final InputComponent component;
    private final HomeViewModel viewModel;

    @Component
    public VBox errorContainer = new VBox();

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public InputInspector(InputComponent component, HomeViewModel viewModel) {
        this.component = component;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithInput(translation.fontWeight(), component, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), component, "-fx-text-fill"),
                Components.LabelWithInput(translation.textContent(), component, "text-content"),
                Components.LabelWithInput(translation.fontSize(), component, "-fx-font-size"),
                //Components.LabelWithTextContent(translation.placeholder(), component.getPromptText(), this::component.setPromptText),
                Components.ColorPickerRow(translation.placeholderColor(), component, "-fx-prompt-text-fill"),
                Components.ColorPickerRow(translation.focusColor(), component, "-fx-focus-color"),
                Components.ColorPickerRow(translation.noFocusColor(), component, "-fx-text-box-border"),
                //Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(20),
                new ButtonRemoverComponent(component, this.viewModel));
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent(translation.variableName(), component.name.get(), component.name::set)
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
