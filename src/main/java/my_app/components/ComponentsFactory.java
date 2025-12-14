package my_app.components;

import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.*;
import my_app.data.contracts.ViewComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

public final class ComponentsFactory {

    private static final TranslationContext.Translation EN =
            TranslationContext.instance().getInEnglishBase();

    private ComponentsFactory() {
    }

    public static ViewComponent<?> createNew(
            String type,
            HomeViewModel viewModel,
            CanvaComponentV2 canva
    ) {

        if (type == null) return null;

        var normalized = type.trim().toLowerCase();

        if (normalized.equals(EN.button())) {
            return new ButtonComponent("Im new here");
        }

        if (normalized.equals(EN.input())) {
            return new InputComponent("Im new here");
        }

        if (normalized.equals(EN.text())) {
            return new TextComponent("Im new here");
        }

        if (normalized.equals(EN.image())) {
            return new ImageComponent(
                    ComponentsContext.class
                            .getResource("/assets/images/mago.jpg")
                            .toExternalForm(),
                    viewModel
            );
        }

        if (normalized.equals(EN.columnItems())) {
            return new ColumnComponent(viewModel, canva);
        }

        if (normalized.equals(EN.menuComponent())) {
            return new MenuComponent(viewModel);
        }

        return null;
    }

    public static ViewComponent<?> fromData(
            ComponentData data,
            HomeViewModel viewModel,
            CanvaComponentV2 canva
    ) {

        if (data == null) return null;

        var type = data.type();

        if (type.equalsIgnoreCase(EN.button())) {
            var c = new ButtonComponent();
            c.applyData((ButtonComponentData) data);
            return c;
        }

        if (type.equalsIgnoreCase(EN.text())) {
            var c = new TextComponent();
            c.applyData((TextComponentData) data);
            return c;
        }

        if (type.equalsIgnoreCase(EN.image())) {
            var c = new ImageComponent();
            c.applyData((ImageComponentData) data);
            return c;
        }

        if (type.equalsIgnoreCase(EN.input())) {
            var c = new InputComponent();
            c.applyData((InputComponentData) data);
            return c;
        }

        if (type.equalsIgnoreCase(EN.menuComponent())) {
            var c = new MenuComponent(viewModel);
            c.applyData((MenuComponentData) data);
            return c;
        }

        if (type.equalsIgnoreCase(EN.customComponent())) {
            var c = new CustomComponent(viewModel);
            c.applyData((CustomComponentData) data);
            return c;
        }

        return null;
    }

    public static ViewComponent<?> cloneFrom(
            ViewComponent<?> existing,
            HomeViewModel viewModel,
            CanvaComponentV2 canva,
            int index,
            String variableValue
    ) {

        var data = existing.getData();
        var clone = fromData(data, viewModel, canva);

        if (clone == null) return null;

        if (index != -1 && variableValue != null) {
            if (clone instanceof TextComponent t) {
                t.setText(t.getText().replace("${boom}", variableValue));
            }

            if (clone instanceof ButtonComponent b) {
                b.setText(b.getText().replace("${boom}", variableValue));
            }

            if (clone instanceof InputComponent i) {
                i.setText(i.getText().replace("${boom}", variableValue));
            }
        }

        return clone;
    }
}
