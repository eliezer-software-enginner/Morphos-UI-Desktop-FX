package my_app.screens.Home.components.leftside;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.ArrayList;
import java.util.List;

public class LeftSide extends VBox {
    private final TranslationContext.Translation translation = TranslationContext.instance().get();

    @Component
    Label appName = Typography.h2(Commons.AppName);

    @Component
    ImageView iv = Commons.CreateImageView("/assets/images/m.png");

    @Component
    HBox logo = new HBox(iv, appName);

    @Component
    Label title = Typography.BodySecondary(translation.visualElements());
    private final TranslationContext.Translation enlishBase = TranslationContext.instance().getInEnglishBase();

    record Field(String name, String nameEngligh) {
    }

    List<Field> optionsField = List.of(
            new Field(translation.text(), enlishBase.text()),
            new Field(translation.button(), enlishBase.button()),
            new Field(translation.input(), enlishBase.input()),
            new Field(translation.image(), enlishBase.image()),
            new Field(translation.customComponent(), enlishBase.customComponent()),
            new Field(translation.columnItems(), enlishBase.columnItems())
    );


    @Component
    List<Option> options = new ArrayList<>();

    @Component
    VBox errorContainer = new VBox();

    @Component // Este campo precisa ser atualizado
    CanvaComponentV2 currentCanva;


    public LeftSide(CanvaComponentV2 currentCanva, HomeViewModel viewModel) {

        this.currentCanva = currentCanva;
        config();
        styles();

        getChildren().addAll(logo, title);

        var spacer = new Region();
        spacer.setMaxHeight(10);
        spacer.setPrefHeight(10);

        getChildren().add(spacer);

        optionsField.forEach(field -> options.add(new Option(field, this.currentCanva, viewModel)));

        getChildren().addAll(options);
        getChildren().add(errorContainer);
    }

    void config() {
        iv.setFitHeight(50);
        iv.setFitWidth(50);

        logo.setAlignment(Pos.CENTER_LEFT);
        iv.setPreserveRatio(true);

        // Faz com que o LeftSide ocupe a altura toda
        setMaxHeight(Double.MAX_VALUE);

        int WIDTH = 250;
        setPrefWidth(WIDTH);
        setMaxWidth(WIDTH);
        setMinWidth(WIDTH);

        // Espaçamento horizontal entre conteúdo e borda
        setPadding(new Insets(10, 10, 0, 10)); // top, right, bottom, left
        setSpacing(5);
    }

    void styles() {
        getStyleClass().add("background-color");
    }

    public void notifyError(String message) {
        PauseTransition delay = new PauseTransition(Duration.millis(700));

        var errorText = Typography.error(message);
        errorText.setWrapText(true);

        delay.setOnFinished(_ -> errorContainer.getChildren().add(errorText));
        delay.play();
    }

    public void removeError() {
        errorContainer.getChildren().clear();
    }

    // Certifique-se de que essa anotação está na classe Home
    public void updateCanva(CanvaComponentV2 newCanva) {
        // 1. Atualize a referência para o canva dentro de LeftSide
        // (Não é estritamente necessário, mas boa prática se LeftSide for usá-lo)
        // Se você não o armazena como campo, apenas itere sobre os Options.

        // 2. Itere sobre as opções e atualize o canva em cada uma delas.
        for (Option option : options) {
            option.updateCanva(newCanva); // Chama o novo método em Option
        }
    }

}
