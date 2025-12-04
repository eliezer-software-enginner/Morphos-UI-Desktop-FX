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
import my_app.data.Commons;
import toolkit.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public record PrimitiveData(String id, String variableName,
                                String type,
                                List<String> values,
                                long createdAt) {
        public PrimitiveData(String variableName, String type, List<String> values) {
            this(UUID.randomUUID().toString(), variableName, type, values, System.currentTimeMillis());
        }

        // Canonical constructor para validar quando id vier nulo
        public PrimitiveData {
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
            }
        }
    }


    void handleClickOnSave(VBox inputLinesContainer, TextField variabelNameInput) {
        //recuperar info e concatenar novos dados
        var type = typeSelected.get();
        var variableName = variabelNameInput.getText();

        var values = new ArrayList<String>();
        for (var node : inputLinesContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                var input = (TextField) hbox.getChildren().get(1);
                values.add(input.getText());
            }
        }

        var primitiveData = new PrimitiveData(variableName, type, values);
        Commons.addPrimitiveData(primitiveData);
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
