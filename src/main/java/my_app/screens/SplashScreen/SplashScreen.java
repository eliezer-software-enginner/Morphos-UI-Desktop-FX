package my_app.screens.SplashScreen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
        //super(new VBox(), 500, 400);

        getChildren().addAll(logo, title, description, footer);

        setup();
        styles();

        viewModel.animateLogo(logo);
    }

    void setup() {
        logo.setFitHeight(200);
        logo.setFitWidth(200);

        setAlignment(Pos.CENTER);
        //description.setWrappingWidth(300);

        //titleAndDescriptionContainer.setMaxWidth(description.getWrappingWidth());

        VBox.setMargin(footer, new Insets(30, 0, 0, 0));
    }

    void styles() {
        setStyle("-fx-background-color:#15161A;");
        title.setStyle("-fx-font-size:40px;-fx-fill:white;");
        description.setStyle("-fx-font-size:17px;-fx-fill:white;");
        footer.setStyle("-fx-font-size:11px;-fx-fill:#92CFA7;");
    }

}
