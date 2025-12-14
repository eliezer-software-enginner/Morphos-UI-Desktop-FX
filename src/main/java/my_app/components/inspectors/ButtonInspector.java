package my_app.components.inspectors;

import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.components.buttonComponent.ButtonBorderRadius;
import my_app.components.ButtonComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.contexts.TranslationContext;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class ButtonInspector implements Inspector<ButtonComponent> {

    private final ButtonComponent button;
    private final HomeViewModel viewModel;

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public ButtonInspector(ButtonComponent button, HomeViewModel viewModel) {
        this.button = button;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.ColorPickerRow(translation.backgroundColor(), button, "-fx-background-color"),
                Components.LabelWithInput(translation.padding(), button, "-fx-padding"),
                new ButtonBorderRadius(button),
                //new ButtonBorderWidth(currentState),
                Components.LabelWithInput(translation.borderWidth(), button, "-fx-border-width"),
                Components.ColorPickerRow(translation.borderColor(), button, "-fx-border-color"),
                Components.LabelWithInput(translation.fontWeight(), button, "-fx-font-weight"),
                Components.ColorPickerRow(translation.fontColor(), button, "-fx-text-fill"),
                Components.LabelWithInput(translation.textContent(), button, "text-content"),
                Components.LabelWithInput(translation.fontSize(), button, "-fx-font-size"),
                Components.ButtonChooseGraphicContent(button),
                Components.LabelWithComboBox(translation.iconPosition(), button, "positioning-icon"),
                Components.ColorPickerRow(translation.iconColor(), button, "icon-color"),
                Components.LabelWithInput("onClick", button, "on-click"),
                //Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(10),
                new ButtonRemoverComponent(button, this.viewModel));
    }

    @Override
    public void layout(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LayoutXYComponent(button),
                Components.ToogleSwithItemRow("Centralizar", button, canva)
        );
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent("Variable name", button.name.get(), button.name::set)
        );
    }
}
