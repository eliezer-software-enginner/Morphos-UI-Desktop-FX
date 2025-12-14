package my_app.components.inspectors;

import my_app.components.*;
import my_app.components.imageComponent.ImageComponent;
import my_app.data.contracts.Inspector;
import my_app.data.contracts.ViewComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public class InspectorRegistry {

    public static Inspector<?> resolve(ViewComponent<?> component, HomeViewModel vm) {
        return switch (component) {
            case ButtonComponent btn -> new ButtonInspector(btn, vm);
            case ColumnComponent columnComponent -> new ColumnInspector(columnComponent, vm);
            case CustomComponent customComponent -> new CustomComponentInspector(customComponent, vm);
            case InputComponent inputComponent -> new InputInspector(inputComponent, vm);
            case MenuComponent menuComponent -> new MenuInspector(menuComponent, vm);
            case TextComponent textComponent -> new TextInspector(textComponent, vm);
            case ImageComponent imageComponent -> new ImageInspector(imageComponent, vm);
            case CanvaComponentV2 canvaComponentV2 -> new CanvaInspector(canvaComponentV2, vm);
        };
        // throw new IllegalArgumentException("No inspector for " + component.getClass());
    }
}
