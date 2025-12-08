package my_app;

// BaseFxTest.java (Crie este arquivo no seu diretório de testes)

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import javafx.application.Platform;

// Extensão JUnit para inicializar o toolkit JavaFX uma vez
public class BaseFxTest implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // Toolkit já iniciado
        }
    }
}