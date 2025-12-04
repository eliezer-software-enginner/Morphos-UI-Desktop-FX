package my_app.screens.PrimitiveListFormScreen;

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
    VBox inputLinesContainer = new VBox();

    PrimitiveListFormScreenViewModel viewModel = new PrimitiveListFormScreenViewModel();

    {
        getChildren().addAll(btnSave, variabelNameText, variabelNameInput, text, buttonsContainer,
                inputLinesContainer);
        setup();
    }

    void setup() {
        viewModel.createButtonsType(buttonsContainer, inputLinesContainer);
        btnSave.setOnMouseClicked(ev ->
                viewModel.handleClickOnSave(inputLinesContainer, variabelNameInput));
        //viewModel.createInputLines(inputLinesContainer);

        getStyleClass().add("background-color");
    }
}
