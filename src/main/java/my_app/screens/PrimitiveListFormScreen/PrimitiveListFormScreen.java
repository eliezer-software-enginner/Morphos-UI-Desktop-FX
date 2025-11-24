package my_app.screens.PrimitiveListFormScreen;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import toolkit.Component;

public class PrimitiveListFormScreen extends VBox {
    @Component
    Text text = new Text("Escolha o tipo");
    @Component
    HBox buttonsContainer = new HBox();

    @Component
    VBox inputLinesContainer = new VBox();

    PrimitiveListFormScreenViewModel viewModel = new PrimitiveListFormScreenViewModel();

    {
        getChildren().addAll(text, buttonsContainer, inputLinesContainer);
        setup();
    }

    void setup() {
        viewModel.createButtonsType(buttonsContainer, inputLinesContainer);
        //viewModel.createInputLines(inputLinesContainer);
    }
}
