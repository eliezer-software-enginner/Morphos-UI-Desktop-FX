package my_app.screens.Home.components.leftside;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.screens.Home.Home;
import my_app.themes.Typography;

import java.util.ArrayList;
import java.util.List;

import static my_app.themes.Typography.BodySecondary;

public class LeftSide extends VBox {
    private final TranslationContext.Translation translation = TranslationContext.instance().get();

        Text appName = new Text(Commons.AppName);
        ImageView iv = Commons.CreateImageView("/assets/images/m.png");

        HBox logo = new HBox(iv, appName);
        Label title = BodySecondary(translation.VisualElements());
        // new Text("Visual Elements");
        // List<String> optionsText = List.of("Text", "Button", "Input", "Image",
        // "Component", "Column items");
        List<String> optionsText = List.of(translation.Text(), translation.Button(), translation.Input(), translation.Image(), translation.Component());

        List<Option> options = new ArrayList<>();
        VBox errorContainer = new VBox();

        public LeftSide(Home home, ComponentsContext componentsContext) {

                config();
                styles();

                getChildren().addAll(logo, title);

                var spacer = new Region();
                spacer.setMaxHeight(10);
                spacer.setPrefHeight(10);

                getChildren().add(spacer);

                optionsText.forEach(title -> options.add(new Option(title, home, componentsContext)));

                getChildren().addAll(options);
                getChildren().add(errorContainer);
        }

        private final int WIDTH = 230;

        void config() {
                iv.setFitHeight(50);
                iv.setFitWidth(50);

                logo.setAlignment(Pos.CENTER_LEFT);
                var iv = (ImageView) logo.getChildren().get(0);
                iv.setPreserveRatio(true);

                // Faz com que o LeftSide ocupe a altura toda
                setMaxHeight(Double.MAX_VALUE);

                setPrefWidth(WIDTH);
                setMaxWidth(WIDTH);
                setMinWidth(WIDTH);

                // Espaçamento horizontal entre conteúdo e borda
                setPadding(new Insets(10, 10, 0, 10)); // top, right, bottom, left
        }

        void styles() {
                getStyleClass().add("background-color");
                appName.setStyle("-fx-fill:#fff;-fx-font-size:17px;");
        }

        public void notifyError(String message) {
                PauseTransition delay = new PauseTransition(Duration.millis(700));

                var errorText = Typography.error(message);
                errorText.setWrapText(true);

                delay.setOnFinished(_ -> errorContainer.getChildren().add(errorText));
                delay.play();
        }

        public void removeError() {
                errorContainer.getChildren().clear();
        }

}
