package my_app.components.imageComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import my_app.components.Components;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.shared.ButtonRemoverComponent;
import my_app.components.shared.HeightComponent;
import my_app.components.shared.WidthComponent;
import my_app.contexts.ComponentsContext;
import my_app.contexts.TranslationContext;
import my_app.data.Commons;
import my_app.data.ImageComponentData;
import my_app.data.ViewContract;
import toolkit.Component;

public class ImageComponent extends ImageView implements ViewContract<ImageComponentData> {

    final int size = 100;
    ObjectProperty<Node> currentState = new SimpleObjectProperty<>();

    public ObjectProperty<FitMode> fitMode = new SimpleObjectProperty<>(FitMode.CONTAIN);

    public Stage stage;

    public StringProperty name = new SimpleStringProperty();
    public String clipType;
    TranslationContext.Translation translation = TranslationContext.instance().get();

    ComponentsContext componentsContext;

    @Component
    public VBox errorContainer = new VBox();
    @Component
    CanvaComponent currentCanva;

    public ImageComponent(ComponentsContext componentsContext, CanvaComponent canvaComponent) {
        config();
        this.componentsContext = componentsContext;
        this.currentCanva = canvaComponent;
    }

    public ImageComponent(String sourcePath, ComponentsContext componentsContext) {
        super(new Image(sourcePath, true));
        // 'true' ativa carregamento assíncrono
        this.componentsContext = componentsContext;
        config();
    }

    void config() {

        setFitWidth(size);
        setFitHeight(size);
        setPreserveRatio(true);

        setId(String.valueOf(System.currentTimeMillis()));
        currentState.set(this);
    }

    @Override
    public void appearance(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                new WidthComponent(this),
                new HeightComponent(this),
                new PreserveRatioComponent(this),
                new ImageBackgroundComponent(this),
                Components.LabelWithComboBox("Clip", this, "clip-image-as-circle"),
                Components.ButtonPrimary(translation.duplicate(), () -> componentsContext.duplicateComponentInCanva(this, canva)),
                Components.spacerVertical(10),
                errorContainer,
                Components.spacerVertical(20),
                new ButtonRemoverComponent(this, componentsContext)
                // new FitComponent(this)
        );
    }

    @Override
    public void settings(Pane father, CanvaComponent canva) {

        father.getChildren().setAll(
                Components.LayoutXYComponent(this),
                Components.ToogleSwithItemRow(translation.centralizeHorizontally(), this, canva));
    }

    @Override
    public void otherSettings(Pane father, CanvaComponent canva) {
        father.getChildren().setAll(
                Components.LabelWithTextContent("Variable name", name.get(), v -> name.set(v)));
    }

    @Override
    public ImageComponentData getData() {
        Image img = this.getImage();

        String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";
        double width = this.getFitWidth();
        double height = this.getFitHeight();

        double x = this.getLayoutX();
        double y = this.getLayoutY();

        boolean preserveRatio = this.isPreserveRatio();
        var location = Commons.NodeInCanva(this);

        return new ImageComponentData(url, width, height, x, y, preserveRatio, this.getId(),
                location.inCanva(),
                location.fatherId(),
                name.get(), clipType, "image");
    }

    @Override
    public void applyData(ImageComponentData data) {

        this.setId(data.identification());

        this.setImage(new Image(data.url()));

        this.setPreserveRatio(data.preserve_ratio());

        this.setLayoutX(data.x());
        this.setLayoutY(data.y());

        this.setFitHeight(data.height());
        this.setFitWidth(data.width());
        this.name.set(data.name());
        this.clipType = data.type_of_clip();

        if (data.type_of_clip().equals("Circle")) {
            var size = data.height() / 2;
            setClip(new Circle(size, size, size));
        }
    }

    @Override
    public Node getCurrentNode() {
        return this;
    }

}
