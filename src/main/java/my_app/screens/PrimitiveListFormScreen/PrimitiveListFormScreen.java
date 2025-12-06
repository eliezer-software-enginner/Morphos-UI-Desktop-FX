package my_app.screens.PrimitiveListFormScreen;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.themes.Typography;
import toolkit.Component;


public class PrimitiveListFormScreen extends VBox {
    TranslationContext.Translation translation = TranslationContext.instance().get();
    @Component
    Button btnSave = Components.ButtonPrimary(translation.save());
    @Component
    Label variabelNameText = Typography.body(translation.variableName());
    @Component
    TextField variabelNameInput = new TextField();
    @Component
    Label text = Typography.body(translation.chooseType());
    @Component
    HBox buttonsContainer = new HBox(10);
    @Component
    VBox inputLinesContainer = new VBox(10);

    @Component
    VBox errorContainer = new VBox();

    PrimitiveListFormScreenViewModel viewModel = new PrimitiveListFormScreenViewModel();

    public PrimitiveListFormScreen(Runnable callback) {
        getChildren().addAll(new HBox(10, btnSave, errorContainer), variabelNameText, variabelNameInput, text,
                buttonsContainer,
                inputLinesContainer);

        viewModel.createButtonsType(buttonsContainer, inputLinesContainer);
        btnSave.setOnMouseClicked(ev -> {
            try {
                errorContainer.getChildren().clear();
                viewModel.handleClickOnSave(inputLinesContainer, variabelNameInput);
                callback.run();
            } catch (Exception e) {
                errorContainer.getChildren().add(Typography.error(e.getMessage()));
            }

        });
        //viewModel.createInputLines(inputLinesContainer);

        setPadding(new Insets(20, 20, 20, 20));
        setSpacing(15);
        getStyleClass().add("background-color");
    }
}
