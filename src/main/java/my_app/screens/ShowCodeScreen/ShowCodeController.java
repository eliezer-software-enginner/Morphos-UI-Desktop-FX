package my_app.screens.ShowCodeScreen;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import my_app.FileManager;
import my_app.components.ColumnComponent;
import my_app.components.CustomComponent;
import my_app.components.InputComponent;
import my_app.components.TextComponent;
import my_app.components.buttonComponent.ButtonComponent;
import my_app.components.canvaComponent.CanvaComponent;
import my_app.components.imageComponent.ImageComponent;
import my_app.contexts.ComponentsContext;
import my_app.data.ViewContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowCodeController {
    private final ComponentsContext mainComponentsContext;

    public ShowCodeController(ComponentsContext mainComponentsContext) {
        this.mainComponentsContext = mainComponentsContext;
    }

    public String createImports() {
        return """
                import javafx.scene.Scene;
                import javafx.scene.control.*;
                import javafx.scene.text.*;
                import javafx.scene.image.ImageView;
                import javafx.scene.image.Image;
                import javafx.scene.layout.*;
                import javafx.scene.paint.Color;
                import javafx.scene.layout.VBox;
                import javafx.stage.Stage;
                """;
    }

    //Aqui são classes componentes
    // class Component1{
    //
    //    }
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

        final var componentsInstances = new ArrayList<String>();
        final var componentsInsideGetChildren = new ArrayList<String>();
        final var componentsInsideMethodSetup = new ArrayList<String>();
        final var componentsInsideMethodStyles = new ArrayList<String>();

        //exemplo: List<String> list1 = List.of("white","black");
        final var listOf_Instances = new ArrayList<String>();

        //exemplo: Text textEmptyColumn = new Text("Nothing")
        final var listOfChildWhenColumnIsEmptyInstances = new ArrayList<String>();
        final var listOfRepeatableChildForColumn_Instances = new ArrayList<String>();

        final var listOfLoadColumnItems_MethodsInvocation = new ArrayList<String>();
        final var listOfLoadColumnItems_MethodsDeclaration = new ArrayList<String>();

        int textCount = 0;
        int btnCount = 0;
        int imgCount = 0;
        int inputCount = 0;
        int columnComponentCount = 0;
        int customComponentCount = 0;
        int listOf_InstancesCount = 0;
        int emptyComponentCount_columnItem_Count = 0;
        int repeatableComponentCount_columnItem_Count = 0;

        for (int i = 0; i < nodesInCanva.size(); i++) {
            Node node = nodesInCanva.get(i);

            if (node instanceof TextComponent component) {
                final String variableName = component.name.get();
                if (variableName == null) textCount++;

                String textText = component.getText();

                String finalName = variableName != null ? variableName : "text" + textCount;

                String textCreation = "Text %s = new Text(\"%s\");".formatted(finalName, textText);
                componentsInstances.add(textCreation);
                componentsInsideGetChildren.add(finalName);

                String setX = String.format(Locale.US, "%s.setLayoutX(%f);", finalName, node.getLayoutX());
                String setY = String.format(Locale.US, "%s.setLayoutY(%f);", finalName, node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof ButtonComponent component) {
                String variableName = component.name.get();
                if (variableName != null) btnCount++;

                String btnText = component.getText();

                String finalName = variableName != null ? variableName : "btn" + btnCount;

                String btnCreation = "Button %s = new Button(\"%s\");".formatted(finalName, btnText);
                componentsInstances.add(btnCreation);
                componentsInsideGetChildren.add(finalName);

                String setX = String.format(Locale.US, "%s.setLayoutX(%f);", finalName, node.getLayoutX());
                String setY = String.format(Locale.US, "%s.setLayoutY(%f);", finalName, node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof ImageComponent component) {
                String variableName = component.name.get();
                if (variableName != null) imgCount++;

                String finalName = variableName != null ? variableName : "imgV" + imgCount;

                String imgViewCreation = "ImageView %s = new ImageView();".formatted(finalName);
                componentsInstances.add(imgViewCreation);

                componentsInsideGetChildren.add(finalName);

                Image img = component.getImage();

                String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";

                String urlstr = "final var url = \"%s\";".formatted(url);

                String setX = String.format(Locale.US, "%s.setLayoutX(%f);", finalName, node.getLayoutX());
                String setY = String.format(Locale.US, "%s.setLayoutY(%f);", finalName, node.getLayoutY());

                String setImageStr = "%s.setImage(new Image(url));".formatted(finalName);

                var h = component.getFitHeight();
                var w = component.getFitWidth();
                String wstr = "%s.setFitWidth(%.0f);".formatted(finalName, w);
                String hstr = "%s.setFitHeight(%.0f);".formatted(finalName, h);

                // inside setup
                componentsInsideMethodSetup.add(urlstr);
                componentsInsideMethodSetup.add(wstr);
                componentsInsideMethodSetup.add(hstr);

                componentsInsideMethodSetup.add(setImageStr);
                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            if (node instanceof InputComponent component) {
                String variableName = component.name.get();
                if (variableName != null) inputCount++;

                String finalName = variableName != null ? variableName : "input" + inputCount;
                String textText = component.getText();

                String textCreation = "TextField %s = new TextField(\"%s\");".formatted(finalName, textText);
                componentsInstances.add(textCreation);
                componentsInsideGetChildren.add(finalName);

                String setX = String.format("%s.setLayoutX(%f);", finalName, node.getLayoutX());
                String setY = String.format("%s.setLayoutY(%f);", finalName, node.getLayoutY());
                String setPromptText = "%s.setPromptText(\"%s\");".formatted(finalName,
                        component.getPromptText());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);
                componentsInsideMethodSetup.add(setPromptText);

                String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);
            }

            //todo fiz só para filho Text, preciso fazer para os outros
            if (node instanceof ColumnComponent component) {
                final String variableName = component.name.get();
                if (variableName == null) columnComponentCount++;

                final String finalName = variableName != null ? variableName : "columnItens" + columnComponentCount;

                final var childIdWhenSelfIsEmpty = component.getData().alternativeChildId();
                final var childIdWhenSelfHasData = component.getData().childId();
                final ViewContract<?> nodeWrapper_whenSelfIsEmpty = this.mainComponentsContext.findNodeById(childIdWhenSelfIsEmpty);
                final ViewContract<?> nodeWrapper_whenSelfHasData = this.mainComponentsContext.findNodeById(childIdWhenSelfHasData);

                String compWhenEmpty_Creation = "";
                String finalNameForComp_WhenEmpty = "";

                if (nodeWrapper_whenSelfIsEmpty instanceof TextComponent compWhenEmpty) {
                    final String textText = compWhenEmpty.getText();
                    finalNameForComp_WhenEmpty = "textEmptyColumn" + emptyComponentCount_columnItem_Count;
                    compWhenEmpty_Creation = "Text %s = new Text(\"%s\");".formatted(finalNameForComp_WhenEmpty, textText);

                    listOfChildWhenColumnIsEmptyInstances.add(compWhenEmpty_Creation);

                    emptyComponentCount_columnItem_Count++;
                }


                String compCreation = "VBox %s = new VBox(%s);".formatted(finalName, finalNameForComp_WhenEmpty);

                componentsInstances.add(compCreation);
                componentsInsideGetChildren.add(finalName);

                String setX = String.format("%s.setLayoutX(%f);", finalName, node.getLayoutX());
                String setY = String.format("%s.setLayoutY(%f);", finalName, node.getLayoutY());

                componentsInsideMethodSetup.add(setX);
                componentsInsideMethodSetup.add(setY);

                String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, component.getStyle());
                componentsInsideMethodStyles.add(setStyle);

                final String methodName = "load" + finalName + "()";
                listOfLoadColumnItems_MethodsInvocation.add(methodName + ";");
                final var dataTableListVariableName = component.dataTableVariableName;
                var methodBuilder = new StringBuilder();
                methodBuilder.append("\tvoid %s{".formatted(methodName));
                if (dataTableListVariableName != null)
                    methodBuilder.append("\n\t\tfor(var item : %s){".formatted(dataTableListVariableName));

                if (nodeWrapper_whenSelfHasData instanceof TextComponent comp) {
                    //todo pegar dado primitivo ou complexo em cada iteracao
                    final String text = comp.getText();
                    String comp_Creation = "\n\t\t\tfinal var component = new Text(\"%s\".replace(\"${boom}\", item));".formatted(text);

                    methodBuilder.append(comp_Creation);
                    // columnItens.children.add(btn)
                    methodBuilder.append(finalName).append("\n\t\t\tgetChildren().add(component);");
                }

                if (dataTableListVariableName != null) methodBuilder.append("\n\t\t}");//fim do for
                methodBuilder.append("\n\t}");
                listOfLoadColumnItems_MethodsDeclaration.add(methodBuilder.toString());

                if (dataTableListVariableName != null) {
                    // 1. Obter a lista de valores (Exemplo: ["black", "white", "blue"])
                    final List<String> list = FileManager.getValuesFromVariableName(dataTableListVariableName);

                    // Lista para armazenar cada valor entre aspas
                    List<String> quotedValues = new ArrayList<>();

                    // 2. Colocar cada valor entre aspas
                    for (String value : list) {
                        // Exemplo: "black" -> "\"black\""
                        quotedValues.add("\"%s\"".formatted(value));
                    }

                    // 3. Juntar os valores com vírgula e espaço (", ")
                    // Exemplo: "\"black\", \"white\", \"blue\""
                    String joinedValues = String.join(", ", quotedValues);

                    // 4. Construir a linha final no formato desejado
                    String finalAssignment = "List<String> %s = List.of(%s);".formatted(
                            dataTableListVariableName,
                            joinedValues
                    );

                    // 5. Adicionar a linha gerada à sua lista final
                    listOf_Instances.add(finalAssignment);
                }

            }

            //todo ver a questão de obter o name da variable aqui posteriormente
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

        return getFinalCode(canvaComponent, listOf_Instances,
                listOfChildWhenColumnIsEmptyInstances, listOfRepeatableChildForColumn_Instances,
                componentsInstances, componentsInsideGetChildren,
                listOfLoadColumnItems_MethodsInvocation, componentsInsideMethodSetup,
                componentsInsideMethodStyles, listOfLoadColumnItems_MethodsDeclaration);
    }

    private static String getFinalCode(
            CanvaComponent canvaComponent,
            ArrayList<String> listOf_Instances,
            ArrayList<String> listOfChildWhenColumnIsEmptyInstances,
            ArrayList<String> listOfRepeatableChildForColumn_Instances,
            ArrayList<String> componentsInstances,
            ArrayList<String> componentsInsideGetChildren,
            ArrayList<String> listOfLoadColumnItems_MethodsInvocation,
            ArrayList<String> componentsInsideMethodSetup,
            ArrayList<String> componentsInsideMethodStyles,
            ArrayList<String> listOfLoadColumnItems_MethodsDeclaration) {
        StringBuilder code = new StringBuilder();
        //

        // code.append(String.join("\n\t", componentsInsideMethodStyles));

        //
        code
                .append("class Screen extends Pane {\n\t");

        // componentsInstances.
        //code.append(String.join("\n\t", listOf_Instances));

        code.append(String.join("\n\t", listOf_Instances));
        code.append("\n\t");

        code.append(String.join("\n\t", listOfChildWhenColumnIsEmptyInstances));
        code.append("\n\t");
        code.append(String.join("\n\t", listOfRepeatableChildForColumn_Instances));
        code.append("\n\t");
        code.append(String.join("\n\t", componentsInstances));

        code.append("\n\t{\n");
        // restante aqui da implementação

        // getChildren().addAll(
        code.append("\n\t\tgetChildren().addAll(\n\t\t");
        code.append(String.join(",\n\t\t", componentsInsideGetChildren));
        code.append("\n\t\t);\n");
        // )

        code.append("\t\tsetup();\n");
        code.append("\t\tstyles();\n\t\t");
        code.append(String.join("\n\t\t", listOfLoadColumnItems_MethodsInvocation));
        code.append("\n");

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
        code.append("\n\t  }");
        // }

        // loadColumnItem1(){
        code.append("\n\n");
        code.append(String.join("\n\n", listOfLoadColumnItems_MethodsDeclaration));
        // }

        code.append("\n\n}");

        System.out.println(code.toString());
        return code.toString();
    }


}


