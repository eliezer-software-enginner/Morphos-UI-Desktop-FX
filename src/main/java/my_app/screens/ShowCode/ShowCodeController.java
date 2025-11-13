package my_app.screens.ShowCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import my_app.components.CustomComponent;
import my_app.components.TextComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.components.InputComponent;

public class ShowCodeController {
    public String createImports() {
        // imports
        StringBuilder imports = new StringBuilder();
        imports.append("import javafx.scene.Scene;\n")
                .append("import javafx.scene.control.*;\n")
                .append("import javafx.scene.text.*;\n")
                .append("import javafx.scene.image.ImageView;\n")
                .append("import javafx.scene.image.Image;\n")
                .append("import javafx.scene.layout.*;\n")
                .append("import javafx.scene.paint.Color;\n")
                .append("import javafx.stage.Stage;");

        System.out.println(imports.toString());

        return imports.toString();
    }

    public List<String> createComponentsForPreview(ObservableList<Node> nodesInCanva) {

        var componentsInstances = new ArrayList<String>();
        var componentsInsideGetChildren = new ArrayList<String>();
        var componentsInsideMethodSetup = new ArrayList<String>();
        var componentsInsideMethodStyles = new ArrayList<String>();
        var componentsClassCreation = new ArrayList<String>();

        int textCount = 0;
        int btnCount = 0;
        int imgCount = 0;
        int inputCount = 0;
        int customComponentCount = 0;

        //

        // code.append(String.join("\n\t", componentsInsideMethodStyles));

        //

        for (int i = 0; i < nodesInCanva.size(); i++) {
            Node node = nodesInCanva.get(i);

            if (node instanceof CustomComponent customComponent) {
                StringBuilder code = new StringBuilder();
                customComponentCount++;

                code
                        .append("class Component%d extends Pane {\n\t".formatted(customComponentCount));

                for (var child : customComponent.getChildren()) {
                    if (child instanceof TextComponent component) {

                        textCount++;

                        String textText = component.getText();

                        String textCreation = "Text text%d = new Text(\"%s\");".formatted(textCount, textText);
                        componentsInstances.add(textCreation);
                        componentsInsideGetChildren.add("text" + textCount);

                        String setX = String.format(Locale.US, "text%d.setLayoutX(%f);", textCount,
                                component.getLayoutX());
                        String setY = String.format(Locale.US, "text%d.setLayoutY(%f);", textCount,
                                component.getLayoutY());

                        componentsInsideMethodSetup.add(setX);
                        componentsInsideMethodSetup.add(setY);

                        String setStyle = "text%d.setStyle(\"%s\");".formatted(textCount, component.getStyle());
                        componentsInsideMethodStyles.add(setStyle);
                    }

                    if (child instanceof Button component) {

                        btnCount++;

                        String btnText = component.getText();

                        String btnCreation = "Button button%d = new Button(\"%s\");".formatted(btnCount, btnText);
                        componentsInstances.add(btnCreation);
                        componentsInsideGetChildren.add("button" + btnCount);

                        String setX = String.format(Locale.US, "button%d.setLayoutX(%f);", btnCount,
                                component.getLayoutX());
                        String setY = String.format(Locale.US, "button%d.setLayoutY(%f);", btnCount,
                                component.getLayoutY());

                        componentsInsideMethodSetup.add(setX);
                        componentsInsideMethodSetup.add(setY);

                        String setStyle = "button%d.setStyle(\"%s\");".formatted(btnCount, component.getStyle());
                        componentsInsideMethodStyles.add(setStyle);
                    }

                    if (node instanceof ImageComponent component) {

                        imgCount++;

                        String imgViewCreation = "ImageView imgV%d = new ImageView();".formatted(imgCount);
                        componentsInstances.add(imgViewCreation);

                        componentsInsideGetChildren.add("imgV" + imgCount);

                        Image img = component.getImage();

                        String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";

                        String urlstr = "final var url = \"%s\";".formatted(url);

                        String setX = String.format(Locale.US, "imgV%d.setLayoutX(%f);", imgCount,
                                component.getLayoutX());
                        String setY = String.format(Locale.US, "imgV%d.setLayoutY(%f);", imgCount,
                                component.getLayoutY());

                        String setImageStr = "imgV%d.setImage(new Image(url));".formatted(imgCount, urlstr);

                        var h = component.getFitHeight();
                        var w = component.getFitWidth();
                        String wstr = "imgV%d.setFitWidth(%.0f);".formatted(imgCount, w);
                        String hstr = "imgV%d.setFitHeight(%.0f);".formatted(imgCount, h);

                        // inside setup
                        componentsInsideMethodSetup.add(urlstr);
                        componentsInsideMethodSetup.add(wstr);
                        componentsInsideMethodSetup.add(hstr);

                        componentsInsideMethodSetup.add(setImageStr);
                        componentsInsideMethodSetup.add(setX);
                        componentsInsideMethodSetup.add(setY);

                        String setStyle = "imgV%d.setStyle(\"%s\");".formatted(imgCount, component.getStyle());
                        componentsInsideMethodStyles.add(setStyle);
                    }

                    if (node instanceof InputComponent component) {

                        inputCount++;

                        String textText = component.getText();

                        String textCreation = "TextField input%d = new TextField(\"%s\");".formatted(inputCount,
                                textText);
                        componentsInstances.add(textCreation);
                        componentsInsideGetChildren.add("input" + inputCount);

                        String setX = String.format(Locale.US, "input%d.setLayoutX(%f);", inputCount,
                                component.getLayoutX());
                        String setY = String.format(Locale.US, "input%d.setLayoutY(%f);", inputCount,
                                component.getLayoutY());
                        String setPromptText = "input%d.setPromptText(\"%s\");".formatted(inputCount,
                                component.getPromptText());

                        componentsInsideMethodSetup.add(setX);
                        componentsInsideMethodSetup.add(setY);
                        componentsInsideMethodSetup.add(setPromptText);

                        String setStyle = "input%d.setStyle(\"%s\");".formatted(inputCount, component.getStyle());
                        componentsInsideMethodStyles.add(setStyle);
                    }

                }

                // componentsInstances.

                code.append(String.join("\n\t", componentsInstances));

                code.append("\n\t{\n");
                // restante aqui da implementação

                // getChildren().addAll(
                code.append("\n\t\tgetChildren().addAll(\n\t\t");
                code.append(String.join(",\n\t\t", componentsInsideGetChildren));
                code.append("\n\t\t);\n");
                // )

                code.append("\t\tsetup();\n");
                code.append("\t\tstyles();\n");

                code.append("\t}\n\n");

                // p.setBack

                // setup(){
                code.append("\tvoid setup(){\n\t\t");

                String config = "this.setPrefSize(%.0f, %.0f);\n\t\t".formatted(
                        customComponent.getPrefWidth(),
                        customComponent.getPrefHeight());
                code.append(config);

                // code.append();
                code.append(String.join("\n\t\t", componentsInsideMethodSetup));
                code.append("\n\t  }\n\n");
                // }

                // styles(){
                code.append("\tvoid styles(){\n\t\t");
                code.append("setStyle(\"%s\");\n\t\t".formatted(customComponent.getStyle()));
                code.append(String.join("\n\t\t", componentsInsideMethodStyles));
                code.append("\n\t  }\n\n");
                // }

                code.append("}");

                System.out.println(code.toString());

                componentsClassCreation.add(code.toString());

            }

        }

        return componentsClassCreation;
    }

    public String createRestOfCode(
            CanvaComponent canvaComponent) {

        ObservableList<Node> nodesInCanva = canvaComponent.getChildren();
        // codigo da classe

        var componentsInstances = new ArrayList<String>();
        var componentsInsideGetChildren = new ArrayList<String>();
        var componentsInsideMethodSetup = new ArrayList<String>();
        var componentsInsideMethodStyles = new ArrayList<String>();

        int textCount = 0;
        int btnCount = 0;
        int imgCount = 0;
        int inputCount = 0;
        int customComponentCount = 0;

        for (int i = 0; i < nodesInCanva.size(); i++) {
            Node node = nodesInCanva.get(i);

            if (node instanceof TextComponent) {
                var component = (TextComponent) node;

                textCount++;

                String textText = component.getText();

                String textCreation = "Text text%d = new Text(\"%s\");".formatted(textCount, textText);
                componentsInstances.add(textCreation);
                componentsInsideGetChildren.add("text" + textCount);

                String setX = String.format(Locale.US, "text%d.setLayoutX(%f);", textCount, node.getLayoutX());
                String setY = String.format(Locale.US, "text%d.setLayoutY(%f);", textCount, node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "text%d.setStyle(\"%s\");".formatted(textCount, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof Button) {
                var component = (Button) node;

                btnCount++;

                String btnText = component.getText();

                String btnCreation = "Button button%d = new Button(\"%s\");".formatted(btnCount, btnText);
                componentsInstances.add(btnCreation);
                componentsInsideGetChildren.add("button" + btnCount);

                String setX = String.format(Locale.US, "button%d.setLayoutX(%f);", btnCount, node.getLayoutX());
                String setY = String.format(Locale.US, "button%d.setLayoutY(%f);", btnCount, node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "button%d.setStyle(\"%s\");".formatted(btnCount, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof ImageComponent component) {

                imgCount++;

                String imgViewCreation = "ImageView imgV%d = new ImageView();".formatted(imgCount);
                componentsInstances.add(imgViewCreation);

                componentsInsideGetChildren.add("imgV" + imgCount);

                Image img = component.getImage();

                String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";

                String urlstr = "final var url = \"%s\";".formatted(url);

                String setX = String.format(Locale.US, "imgV%d.setLayoutX(%f);", imgCount, node.getLayoutX());
                String setY = String.format(Locale.US, "imgV%d.setLayoutY(%f);", imgCount, node.getLayoutY());

                String setImageStr = "imgV%d.setImage(new Image(url));".formatted(imgCount, urlstr);

                var h = component.getFitHeight();
                var w = component.getFitWidth();
                String wstr = "imgV%d.setFitWidth(%.0f);".formatted(imgCount, w);
                String hstr = "imgV%d.setFitHeight(%.0f);".formatted(imgCount, h);

                // inside setup
                componentsInsideMethodSetup.add(urlstr);
                componentsInsideMethodSetup.add(wstr);
                componentsInsideMethodSetup.add(hstr);

                componentsInsideMethodSetup.add(setImageStr);
                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "imgV%d.setStyle(\"%s\");".formatted(imgCount, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof InputComponent component) {

                inputCount++;

                String textText = component.getText();

                String textCreation = "TextField input%d = new TextField(\"%s\");".formatted(inputCount, textText);
                componentsInstances.add(textCreation);
                componentsInsideGetChildren.add("input" + inputCount);

                String setX = String.format(Locale.US, "input%d.setLayoutX(%f);", inputCount, node.getLayoutX());
                String setY = String.format(Locale.US, "input%d.setLayoutY(%f);", inputCount, node.getLayoutY());
                String setPromptText = "input%d.setPromptText(\"%s\");".formatted(inputCount,
                        component.getPromptText());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);
                componentsInsideMethodSetup.add(setPromptText);

                String setStyle = "input%d.setStyle(\"%s\");".formatted(inputCount, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof CustomComponent component) {

                customComponentCount++;

                String compCreation = "Component%d component%d = new Component%d();".formatted(
                        customComponentCount, customComponentCount, customComponentCount);

                componentsInstances.add(compCreation);
                componentsInsideGetChildren.add("component" + customComponentCount);

                String setX = String.format(Locale.US, "component%d.setLayoutX(%f);", customComponentCount,
                        node.getLayoutX());
                String setY = String.format(Locale.US, "component%d.setLayoutY(%f);", customComponentCount,
                        node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "component%d.setStyle(\"%s\");".formatted(customComponentCount, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

        }

        StringBuilder code = new StringBuilder();
        //

        // code.append(String.join("\n\t", componentsInsideMethodStyles));

        //
        code
                .append("class Screen extends Pane {\n\t");

        // componentsInstances.

        code.append(String.join("\n\t", componentsInstances));

        code.append("\n\t{\n");
        // restante aqui da implementação

        // getChildren().addAll(
        code.append("\n\t\tgetChildren().addAll(\n\t\t");
        code.append(String.join(",\n\t\t", componentsInsideGetChildren));
        code.append("\n\t\t);\n");
        // )

        code.append("\t\tsetup();\n");
        code.append("\t\tstyles();\n");

        code.append("\t}\n\n");

        // p.setBack

        // setup(){
        code.append("\tvoid setup(){\n\t\t");

        String config = "this.setPrefSize(%.0f, %.0f);\n\t\t".formatted(
                canvaComponent.getPrefWidth(),
                canvaComponent.getPrefHeight());
        code.append(config);

        // String config = "this.setPrefSize(%.0f, %.0f);\n\t\t".formatted(
        // canvaComponent.getPrefWidth(),
        // canvaComponent.getPrefHeight());
        // code.append(config);

        code.append(String.join("\n\t\t", componentsInsideMethodSetup));
        code.append("\n\t  }\n\n");
        // }

        // styles(){
        code.append("\tvoid styles(){\n\t\t");
        code.append("setStyle(\"%s\");\n\t\t".formatted(canvaComponent.getStyle()));
        code.append(String.join("\n\t\t", componentsInsideMethodStyles));
        code.append("\n\t  }\n\n");
        // }

        code.append("}");

        System.out.println(code.toString());
        return code.toString();
    }

}
