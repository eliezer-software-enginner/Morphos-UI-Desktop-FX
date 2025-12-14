package my_app.components.inspectors;

import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import my_app.FileManager;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.contracts.Inspector;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import toolkit.Component;

public class CanvaInspector implements Inspector<CanvaComponentV2> {

    private final CanvaComponentV2 component;
    private final HomeViewModel viewModel;

    @Component
    public VBox errorContainer = new VBox();

    TranslationContext.Translation translation = TranslationContext.instance().get();

    public CanvaInspector(CanvaComponentV2 component, HomeViewModel viewModel) {
        this.component = component;
        this.viewModel = viewModel;
    }

    @Override
    public void appearance(VBox father, CanvaComponentV2 canva) {

        final var style = canva.getStyle();

        // Color Picker (Ação atualizada para rastrear estado e remover o fundo Node)
        ColorPicker bgColorPicker = new ColorPicker(
                Color.web(
                        Commons.getValueOfSpecificField(style, "-fx-background-color")));
        bgColorPicker.setOnAction(e -> {
            Color c = bgColorPicker.getValue();
            String hexColor = Commons.ColortoHex(c);

            component.removeCheckeredBackground(); // Remove o quadriculado

            component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-color", hexColor));

            // Atualiza o estado interno
            component.currentBgType = "color";
            component.currentBgContent = hexColor;
        });

        // Botão para escolher imagem do sistema (Ação atualizada para remover o fundo Node)
        Button chooseImgBtn = new Button("Choose Image...");
        chooseImgBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            var file = fc.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();

                //antigo
                /*
                setStyle("-fx-background-image: url('" + uri + "'); " +
                        "-fx-background-size: cover; -fx-background-position: center;");
                 */

                component.removeCheckeredBackground(); // Remove o quadriculado
                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-image", "url('" + uri + "')"));

                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-size", "cover"));
                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-position", "center"));


                // Atualiza o estado interno
                component.currentBgType = "image";
                component.currentBgContent = uri;
            }
        });

        // Botão para tornar o fundo transparente (Chama o novo método)
        Button transparentBtn = new Button("Set Transparent");
        transparentBtn.setOnAction(e -> {
            component.applyCheckeredBackground();
            // applyCheckeredBackground() já atualiza o estado interno
        });


        // Campo para URL (Ação atualizada para remover o fundo Node)
        TextField urlField = new TextField();
        urlField.setPromptText("Paste URl of image");
        Button applyUrl = new Button("Apply URL");
        applyUrl.setOnAction(_ -> {
            String url = urlField.getText();
            if (url != null && !url.isBlank()) {

                component.removeCheckeredBackground(); // Remove o quadriculado

                //setStyle("-fx-background-image: url('" + url + "'); " +
                //      "-fx-background-size: cover; -fx-background-position: center;");

                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-image", "url('" + url + "')"));
                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-size", "cover"));
                component.setStyle(Commons.UpdateEspecificStyle(style, "-fx-background-position", "center"));

                // Atualiza o estado interno
                component.currentBgType = "image";
                component.currentBgContent = url;
            }
        });

        // Configura o layout do painel de aparência
        father.getChildren().setAll(
                bgColorPicker,
                chooseImgBtn,
                urlField,
                applyUrl,
                Components.spacerVertical(10),
                transparentBtn,
                Components.spacerVertical(10),
                Components.LabelWithInput(translation.height(), component, "-fx-pref-height"),
                Components.LabelWithInput(translation.width(), component, "-fx-pref-width")
        );
    }

    @Override
    public void settings(VBox father, CanvaComponentV2 canva) {
        father.getChildren().addAll(Components.LabelWithInputAndButton(
                        translation.screenName(), translation.update(),
                        component, "screen-name", () -> {
                            FileManager.updateScreenNameInProject(component.screenFatherId, "name", component.name);
                            //  viewModel.toggleRefreshScreenTabs();
                        }),
                Components.LabelWithInputAndButton(
                        "view model name", translation.update(),
                        component, "view-model-name", () -> {
                            FileManager.updateScreenNameInProject(component.screenFatherId, "viewModelName",
                                    component.viewModelName);
                            //  viewModel.toggleRefreshScreenTabs();
                        })
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
