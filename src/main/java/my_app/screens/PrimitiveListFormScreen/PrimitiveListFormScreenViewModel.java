package my_app.screens.PrimitiveListFormScreen;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import my_app.components.Components;
import toolkit.Component;

public class PrimitiveListFormScreenViewModel {
    private final String[] types = {"String", "Float", "Int", "Char"};
    StringProperty typeSelected = new SimpleStringProperty();

    @Component
    void createButtonsType(HBox buttonsContainer, VBox inputLinesContainer) {
        for (String type : types) {
            var btn = Components.ButtonPrimaryOutline(type);
            btn.setOnMouseClicked(ev -> {
                handleClickOnButtonType(type, inputLinesContainer);
            });
            buttonsContainer.getChildren().add(btn);
        }
    }

    void handleClickOnButtonType(String type, VBox inputLinesContainer) {
        typeSelected.set(type);
        inputLinesContainer.getChildren().clear();
        inputLinesContainer.getChildren().add(createInputLine(inputLinesContainer));
    }

    @Component
    public HBox createInputLine(VBox inputLinesContainer) {
        var counter = inputLinesContainer.getChildren().size();
        var text = new Text("Registro: " + counter + " = ");
        var input = new TextField();
        var btnAdd = new Button("+");

        String currentType = typeSelected.get();

        // ======== FORMATTERS POR TIPO ========
        switch (currentType) {

            case "Int" -> {
                // Permite: "-", "123", "-123", "", etc.
                var formatter = new TextFormatter<String>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("^-?\\d*$")) {
                        return change;
                    }
                    return null;
                });
                input.setTextFormatter(formatter);
            }

            case "Float" -> {
                // Permite: "-", ".", "-.", "12.", "12.3", ""
                var formatter = new TextFormatter<String>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("^-?\\d*(\\.\\d*)?$")) {
                        return change;
                    }
                    return null;
                });
                input.setTextFormatter(formatter);
            }

            case "Char" -> {
                // Máximo de 1 caractere
                var formatter = new TextFormatter<String>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.length() <= 1) {
                        return change;
                    }
                    return null;
                });
                input.setTextFormatter(formatter);
            }

            default -> {
                // String → permite tudo (sem formatter)
            }
        }

        // ======== BOTÃO DE + ========
        btnAdd.setOnMouseClicked(ev -> {
            inputLinesContainer.getChildren().add(createInputLine(inputLinesContainer));
        });

        return new HBox(10, text, input, btnAdd);
    }
}
