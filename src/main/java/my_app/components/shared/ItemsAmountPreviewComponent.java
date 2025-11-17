package my_app.components.shared;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import my_app.components.ColumnComponent;
import my_app.themes.Typography;

// ItemsAmountPreviewComponent.java

public class ItemsAmountPreviewComponent extends HBox {

    Label title = Typography.caption("Children amount(preview):");
    TextField tf = new TextField();

    // A propriedade agora é local, mas precisamos saber quem é o alvo
    // (columnTarget)
    public ItemsAmountPreviewComponent(ColumnComponent columnTarget) {

        config();

        // Inicializa o TextField com o valor atual
        tf.setText(String.valueOf(columnTarget.childrenAmountState.get()));
        tf.setPrefWidth(50); // Define uma largura pequena para o campo

        tf.textProperty().addListener((obs, old, newVal) -> {
            try {
                // Remove caracteres não numéricos para garantir entrada limpa
                String cleanVal = newVal.replaceAll("[^\\d]", "");
                if (cleanVal.isEmpty()) {
                    // Impede que o campo fique vazio (mínimo 1 item, por exemplo)
                    tf.setText("0");
                    return;
                }

                int newAmount = Integer.parseInt(cleanVal);

                // Definir um limite máximo e mínimo
                if (newAmount > 100)
                    newAmount = 100; // Limite de 100
                if (newAmount < 1)
                    newAmount = 0; // Mínimo de 0

                // Garante que o TextField e o State estejam sincronizados
                tf.setText(String.valueOf(newAmount));
                columnTarget.childrenAmountState.set(newAmount);

                // ** AQUI CHAMAMOS A FUNÇÃO DE RECRIAÇÃO **
                columnTarget.recreateChildren();

            } catch (NumberFormatException err) {
                // Ignora se for um erro de formato que não seja apenas o filtro (cleanVal)
            }
        });

        getChildren().addAll(title, tf);
    }

    void config() {

        setSpacing(10);
    }
}