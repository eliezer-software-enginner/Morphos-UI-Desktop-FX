package my_app.screens.SplashScreen;

import javafx.animation.ScaleTransition; // Importação agora na View
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration; // Importação agora na View
import my_app.contexts.TranslationContext;
import my_app.themes.Typography;
import toolkit.Component;

import java.util.Objects;

public class SplashScreen extends VBox {

    TranslationContext translation = TranslationContext.instance();

    @Component
    ImageView logo = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/m.png"))));
    @Component
    Text title = Typography.title(translation.get().splashTranslation().title());
    @Component
    Label description = Typography.subtitle(translation.get().splashTranslation().description());
    @Component
    Label footer = Typography.caption(translation.get().splashTranslation().footer());

    SplashScreenViewModel viewModel;

    public SplashScreen(Stage theirStage) {
        this.viewModel = new SplashScreenViewModel(theirStage);

        getChildren().addAll(logo, title, description, footer);

        // *** NOVA CHAMADA: A View chama a lógica de animação ***
        animateLogo();

        logo.setFitHeight(200);
        logo.setFitWidth(200);

        setAlignment(Pos.CENTER);

        VBox.setMargin(footer, new Insets(30, 0, 0, 0));

        getStyleClass().add("background-color");
        title.setStyle("-fx-font-size:40px;");
    }

    // *** MÉTODO DE ANIMAÇÃO AGORA PERTENCE À VIEW ***
    private void animateLogo() {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1));
        scale.setNode(logo); // Manipula o componente de UI
        scale.setFromX(1);
        scale.setFromY(1);

        scale.setToX(0.5);
        scale.setToY(0.5);

        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        // Quando a animação termina (responsabilidade da View),
        // a View chama o comando de negócio da ViewModel.
        scale.setOnFinished(_ -> viewModel.decideNextScene());

        scale.play();
    }
}