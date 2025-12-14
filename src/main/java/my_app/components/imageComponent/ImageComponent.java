package my_app.components.imageComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import my_app.data.Commons;
import my_app.data.ImageComponentData;
import my_app.data.contracts.ViewComponent;
import my_app.screens.Home.HomeViewModel;
import toolkit.Component;

public final class ImageComponent extends ImageView implements ViewComponent<ImageComponentData> {

    final int size = 100;

    public ObjectProperty<FitMode> fitMode = new SimpleObjectProperty<>(FitMode.CONTAIN);

    public StringProperty name = new SimpleStringProperty();
    public String clipType;

    boolean isDeleted = false;

    @Component
    public VBox errorContainer = new VBox();

    public ImageComponent() {
        config();
    }

    public ImageComponent(String sourcePath, HomeViewModel viewModel) {
        super(new Image(sourcePath, true));
        // 'true' ativa carregamento assíncrono
        config();
    }

    void config() {

        setFitWidth(size);
        setFitHeight(size);
        setPreserveRatio(true);

        setId(String.valueOf(System.currentTimeMillis()));
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
                location.fatherId(),
                name.get(), clipType, "image", isDeleted);
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

        if (data.type_of_clip() != null) {
            if (data.type_of_clip().equals("Circle")) {
                var size = data.height() / 2;
                setClip(new Circle(size, size, size));
            }
        }

    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void delete() {
        isDeleted = true;
    }

}
