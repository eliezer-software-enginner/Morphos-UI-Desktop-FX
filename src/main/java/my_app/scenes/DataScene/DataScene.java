package my_app.scenes.DataScene;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import my_app.contexts.DataStore;

public class DataScene extends Scene {

    private VBox dataListContainer; // Onde os dados criados serão exibidos
    Stage stage = new Stage();

    public DataScene() {
        super(new VBox(), 1200, 650);

        VBox root = (VBox) this.getRoot();
        root.setSpacing(15);
        root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20;");

        // Botão voltar
        Button btnBack = new Button("Voltar");
        //btnBack.setOnAction(e -> primaryStage.setScene(mainScene));
        btnBack.setOnAction(ev -> stage.close());
        root.getChildren().add(btnBack);

        // Botão para criar novo dado
        Button btnCreateData = new Button("Create Data");
        root.getChildren().add(btnCreateData);

        // Container para as Rows de criação de dados
        VBox createDataContainer = new VBox(10);
        root.getChildren().add(createDataContainer);

        // Container que lista todos os dados criados
        dataListContainer = new VBox(10);
        root.getChildren().add(dataListContainer);

        btnCreateData.setOnAction(e -> addNewDataRow(createDataContainer));
    }

    // Adiciona uma nova Row para criação de dado
    private void addNewDataRow(VBox container) {
        HBox row = new HBox(10);
        row.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #ffffff;");

        // Select tipo
        ComboBox<String> typeSelect = new ComboBox<>(
                FXCollections.observableArrayList("text", "number", "table", "boolean"));
        typeSelect.setPromptText("Select Type");

        // Input para nome do dado
        TextField nameInput = new TextField();
        nameInput.setPromptText("Data Name");
        nameInput.setDisable(true); // habilita somente após escolher tipo

        // Input para valor
        TextField valueInput = new TextField(); // default, será trocado se boolean
        valueInput.setDisable(true);

        // Botão salvar
        Button btnSave = new Button("Save");
        btnSave.setDisable(true);

        row.getChildren().addAll(typeSelect, nameInput, valueInput, btnSave);
        container.getChildren().add(row);

        // Lógica de interação
        typeSelect.setOnAction(ev -> {
            nameInput.setDisable(false);
            valueInput.setDisable(false);

            if (typeSelect.getValue().equals("boolean")) {
                ComboBox<String> booleanSelect = new ComboBox<>(FXCollections.observableArrayList("True", "False"));
                booleanSelect.setPromptText("Value");
                row.getChildren().set(2, booleanSelect); // substitui o input por select boolean
            } else {
                TextField valueField = new TextField();
                valueField.setPromptText("Value");
                row.getChildren().set(2, valueField);
            }
        });

        // Habilita botão salvar quando nome é digitado
        nameInput.textProperty().addListener((obs, oldText, newText) -> {
            btnSave.setDisable(newText.trim().isEmpty());
        });

        btnSave.setOnAction(ev -> {
            String type = typeSelect.getValue();
            String name = nameInput.getText();
            String value;

            if (type.equals("boolean")) {
                ComboBox<String> boolSelect = (ComboBox<String>) row.getChildren().get(2);
                value = boolSelect.getValue();
            } else {
                TextField valField = (TextField) row.getChildren().get(2);
                value = valField.getText();
            }

            addDataToList(type, name, value);

            // Remove a row de criação após salvar
            container.getChildren().remove(row);
        });
    }

    // Adiciona o dado criado no container principal
    private void addDataToList(String type, String name, String value) {
        // Cria e salva no DataStore
        DataStore.DataItem item = new DataStore.DataItem(type, name, value);
        DataStore.getInstance().addData(item);

        // Row visual
        HBox dataRow = new HBox(10);
        dataRow.setStyle(
                "-fx-padding: 8; -fx-border-color: black; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #d9edf7;");

        Label lblType = new Label("Type: " + type);
        Label lblName = new Label("Name: " + name);

        Node valueNode;
        if ("boolean".equals(type)) {
            ComboBox<String> valueSelect = new ComboBox<>(FXCollections.observableArrayList("True", "False"));
            valueSelect.valueProperty().bindBidirectional(item.value); // ligação bidirecional
            valueNode = valueSelect;
        } else {
            TextField valueField = new TextField();
            valueField.textProperty().bindBidirectional(item.value); // ligação bidirecional
            valueNode = valueField;
        }

        dataRow.getChildren().addAll(lblType, lblName, valueNode);
        dataListContainer.getChildren().add(dataRow);
    }

    public void show() {
        stage.setScene(this);
        stage.show();
    }
}
