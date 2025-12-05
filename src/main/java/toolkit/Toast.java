package toolkit;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Toast extends Label {

    public Toast() {
        this.setStyle("""
                -fx-background-color: #323232;
                -fx-text-fill: white;
                -fx-padding: 10px 20px;
                -fx-background-radius: 8px;
                -fx-alignment: center;
                """);

        this.setManaged(false);
        this.setVisible(false);
        this.setOpacity(0);
    }

    public void show(String message) {
        Platform.runLater(() -> {
            this.setText(message);
            this.setManaged(true);
            this.setVisible(true);

            // Animação de fade-in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                // Espera 2.5s e depois faz o fade-out
                new Thread(() -> {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ignored) {
                    }

                    Platform.runLater(() -> {
                        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
                        fadeOut.setFromValue(1);
                        fadeOut.setToValue(0);
                        fadeOut.setOnFinished(e -> {
                            this.setManaged(false);
                            this.setVisible(false);
                        });
                        fadeOut.play();
                    });
                }).start();
            });

            fadeIn.play();
        });
    }
}
