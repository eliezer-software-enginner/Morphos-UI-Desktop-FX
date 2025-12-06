package my_app.screens.PrimitiveListFormScreen;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my_app.FileManager;
import my_app.components.Components;
import my_app.contexts.TranslationContext;
import my_app.data.PrimitiveData;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.ArrayList;

public class PrimitiveListFormScreenViewModel {
    private final TranslationContext.Translation translation = TranslationContext.instance().get();
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

    void handleClickOnSave(VBox inputLinesContainer, TextField variabelNameInput) {
        //recuperar info e concatenar novos dados
        var type = typeSelected.get();
        var variableName = variabelNameInput.getText();

        if (type == null) throw new RuntimeException("Você deve escolher o tipo!");
        if (variableName.trim().isEmpty()) throw new RuntimeException("Nome de variável não pode estar vazio!");

        var values = new ArrayList<String>();
        for (var node : inputLinesContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                var input = (TextField) hbox.getChildren().get(1);
                values.add(input.getText());
            }
        }

        if (values.isEmpty()) throw new RuntimeException("A lista está vazia!");

        values.forEach(value -> {
            if (value.trim().isEmpty()) throw new RuntimeException("Toda lista deve estar preenchida!");
        });

        final var primitiveData = new PrimitiveData(variableName, type, values);
        FileManager.addPrimitiveDataInProject(primitiveData);
    }

    void handleClickOnButtonType(String type, VBox inputLinesContainer) {
        typeSelected.set(type);
        inputLinesContainer.getChildren().clear();
        inputLinesContainer.getChildren().add(createInputLine(inputLinesContainer));
    }

    @Component
    public HBox createInputLine(VBox inputLinesContainer) {
        var counter = inputLinesContainer.getChildren().size();
        var text = Typography.caption(translation.register() + ": " + counter + " = ");
        var input = new TextField();
        var btnAdd = Components.ButtonPrimaryOutline("+");

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
            if (!input.getText().trim().isEmpty())
                inputLinesContainer.getChildren().add(createInputLine(inputLinesContainer));
        });

        return new HBox(10, text, input, btnAdd);
    }
}
