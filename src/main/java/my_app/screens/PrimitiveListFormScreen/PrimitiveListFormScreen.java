package my_app.screens.PrimitiveListFormScreen;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import my_app.components.Components;
import toolkit.Component;

public class PrimitiveListFormScreen extends VBox {
    @Component
    Button btnSave = Components.ButtonPrimary("Salvar");
    @Component
    Text variabelNameText = new Text("Nome de variável");
    @Component
    TextField variabelNameInput = new TextField();
    @Component
    Text text = new Text("Escolha o tipo");
    @Component
    HBox buttonsContainer = new HBox();
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
    }
}
