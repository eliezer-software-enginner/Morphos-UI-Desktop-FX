package my_app.screens.ShowCodeScreen;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import my_app.FileManager;
import my_app.components.*;
import my_app.components.imageComponent.ImageComponent;
import my_app.screens.Home.HomeViewModel;
import my_app.screens.Home.components.canvaComponent.CanvaComponentV2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowCodeController {
    private final HomeViewModel viewModel;

    // --- COLEÇÕES DE DADOS (Helper Class para reduzir parâmetros) ---
    private record CodeCollections(
            List<String> instances,
            List<String> children,
            List<String> setup,
            List<String> styles,
            List<String> onClickHandlers) {
    }
    // ------------------------------------------------------------------

    public ShowCodeController(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public String createImports() {
        return """
                import javafx.scene.Scene;
                import javafx.scene.input.MouseEvent;
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

    private String generateViewModelCode(String viewModelName, ArrayList<String> methods) {
        if (methods.isEmpty() || viewModelName == null || viewModelName.isEmpty()) {
            return "";
        }

        String methodsCode = String.join("\n\n\t", methods);

        return """
                
                // -------------------------------------------------------------------
                // VIEW MODEL - Contém a lógica de negócio e handlers de eventos
                // -------------------------------------------------------------------
                class %s {
                
                    // Construtor, injeção de dependências, etc., podem ser adicionados aqui
                
                    %s
                }
                
                
                """.formatted(viewModelName, methodsCode);
    }

    /**
     * Adiciona as linhas de código comuns para configuração de layout e estilo de um componente.
     */
    private void addCommonComponentCode(
            String finalName,
            Node node,
            String style,
            List<String> componentsInsideGetChildren,
            List<String> componentsInsideMethodSetup,
            List<String> componentsInsideMethodStyles) {

        // 1. Adicionar à lista de children do Pane
        componentsInsideGetChildren.add(finalName);

        // 2. Setup (Layout)
        String setX = String.format(Locale.US, "%s.setLayoutX(%f);", finalName, node.getLayoutX());
        String setY = String.format(Locale.US, "%s.setLayoutY(%f);", finalName, node.getLayoutY());

        componentsInsideMethodSetup.add(setX);
        componentsInsideMethodSetup.add(setY);

        // 3. Styles
        String setStyle = "%s.setStyle(\"%s\");".formatted(finalName, style);
        componentsInsideMethodStyles.add(setStyle);
    }

    public String createRestOfCode(CanvaComponentV2 canvaComponent) {

        final String viewModelName = canvaComponent.getData().viewModelName != null && !canvaComponent.getData().viewModelName.isEmpty() ?
                canvaComponent.getData().viewModelName : "AppViewModel";
        final String viewModelInstanceName = viewModelName.substring(0, 1).toLowerCase() + viewModelName.substring(1);

        final var onClickHandlers = new ArrayList<String>();
        final var viewModelMethods = new ArrayList<String>();

        ObservableList<Node> nodesInCanva = canvaComponent.getChildren();

        final var componentsInstances = new ArrayList<String>();
        final var componentsInsideGetChildren = new ArrayList<String>();
        final var componentsInsideMethodSetup = new ArrayList<String>();
        final var componentsInsideMethodStyles = new ArrayList<String>();

        final var listOf_Instances = new ArrayList<String>();
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
        int emptyComponentCount_columnItem_Count = 0;

        for (int i = 0; i < nodesInCanva.size(); i++) {
            Node node = nodesInCanva.get(i);

            if (node instanceof TextComponent component) {
                final String variableName = component.name.get();
                if (variableName == null) textCount++;

                String finalName = variableName != null ? variableName : "text" + textCount;

                String textCreation = "Text %s = new Text(\"%s\");".formatted(finalName, component.getText());
                componentsInstances.add(textCreation);

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );
            }

            if (node instanceof ButtonComponent component) {
                String variableName = component.name.get();
                if (variableName == null) btnCount++;

                String finalName = variableName != null ? variableName : "btn" + btnCount;

                String btnCreation = "Button %s = new Button(\"%s\");".formatted(finalName, component.getText());
                componentsInstances.add(btnCreation);

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );

                // Lógica Única de Eventos de Clique
                String onClickMethodName = component.getData().nameOfOnClickMethod();

                if (onClickMethodName != null && !onClickMethodName.isEmpty()) {
                    String handler = "%s.setOnMouseClicked(e -> %s.%s());".formatted(
                            finalName, viewModelInstanceName, onClickMethodName
                    );
                    onClickHandlers.add(handler);

                    String methodSignature = "public void %s() {\n\t\t// Lógica de %s\n\t}".formatted(onClickMethodName, onClickMethodName);
                    if (viewModelMethods.stream().noneMatch(s -> s.contains("public void " + onClickMethodName + "()"))) {
                        viewModelMethods.add(methodSignature);
                    }
                }
            }

            if (node instanceof ImageComponent component) {
                String variableName = component.name.get();
                if (variableName == null) imgCount++;

                String finalName = variableName != null ? variableName : "imgV" + imgCount;

                String imgViewCreation = "ImageView %s = new ImageView();".formatted(finalName);
                componentsInstances.add(imgViewCreation);

                // Lógica Única de Image
                Image img = component.getImage();
                String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";
                String urlstr = "final var url = \"%s\";".formatted(url);
                String setImageStr = "%s.setImage(new Image(url));".formatted(finalName);
                var h = component.getFitHeight();
                var w = component.getFitWidth();
                String wstr = "%s.setFitWidth(%.0f);".formatted(finalName, w);
                String hstr = "%s.setFitHeight(%.0f);".formatted(finalName, h);

                // inside setup (Unique and Common)
                componentsInsideMethodSetup.add(urlstr);
                componentsInsideMethodSetup.add(wstr);
                componentsInsideMethodSetup.add(hstr);
                componentsInsideMethodSetup.add(setImageStr);

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );
            }

            if (node instanceof InputComponent component) {
                String variableName = component.name.get();
                if (variableName == null) inputCount++;

                String finalName = variableName != null ? variableName : "input" + inputCount;

                String textCreation = "TextField %s = new TextField(\"%s\");".formatted(finalName, component.getText());
                componentsInstances.add(textCreation);

                // Lógica Única de Input
                String setPromptText = "%s.setPromptText(\"%s\");".formatted(finalName, component.getPromptText());
                componentsInsideMethodSetup.add(setPromptText);

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );
            }

            if (node instanceof ColumnComponent component) {
                final String variableName = component.name.get();
                if (variableName == null) columnComponentCount++;

                final String finalName = variableName != null ? variableName : "columnItens" + columnComponentCount;

                final var childIdWhenSelfIsEmpty = component.getData().alternativeChildId();
                final var childIdWhenSelfHasData = component.getData().childId();
                final var nodeWrapper_whenSelfIsEmpty = this.viewModel.findNodeById(childIdWhenSelfIsEmpty);
                final var nodeWrapper_whenSelfHasData = this.viewModel.findNodeById(childIdWhenSelfHasData);

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

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );

                // Lógica Única de ColumnComponent (load method)
                final String methodName = "load" + finalName + "()";
                listOfLoadColumnItems_MethodsInvocation.add(methodName + ";");
                final var dataTableListVariableName = component.dataTableVariableName;
                var methodBuilder = new StringBuilder();
                methodBuilder.append("\tvoid %s{".formatted(methodName));
                if (dataTableListVariableName != null)
                    methodBuilder.append("\n\t\tfor(var item : %s){".formatted(dataTableListVariableName));

                if (nodeWrapper_whenSelfHasData instanceof TextComponent comp) {
                    final String text = comp.getText();
                    String comp_Creation = "\n\t\t\tfinal var component = new Text(\"%s\".replace(\"${boom}\", item));".formatted(text);

                    methodBuilder.append(comp_Creation);
                    methodBuilder.append("\n\t\t\t%s.getChildren().add(component);".formatted(finalName));
                }

                if (dataTableListVariableName != null) methodBuilder.append("\n\t\t}");
                methodBuilder.append("\n\t}");
                listOfLoadColumnItems_MethodsDeclaration.add(methodBuilder.toString());

                if (dataTableListVariableName != null) {
                    final List<String> list = FileManager.getValuesFromVariableName(dataTableListVariableName);
                    List<String> quotedValues = new ArrayList<>();
                    for (String value : list) {
                        quotedValues.add("\"%s\"".formatted(value));
                    }
                    String joinedValues = String.join(", ", quotedValues);
                    String finalAssignment = "List<String> %s = List.of(%s);".formatted(
                            dataTableListVariableName,
                            joinedValues
                    );
                    listOf_Instances.add(finalAssignment);
                }
            }

            if (node instanceof CustomComponent component) {
                customComponentCount++;

                String finalName = "component" + customComponentCount;
                String compCreation = "Component%d %s = new Component%d();".formatted(
                        customComponentCount, finalName, customComponentCount);

                componentsInstances.add(compCreation);

                // --- Código Comum Extraído ---
                addCommonComponentCode(
                        finalName, node, component.getStyle(),
                        componentsInsideGetChildren, componentsInsideMethodSetup, componentsInsideMethodStyles
                );
            }
        }

        return generateViewModelCode(viewModelName, viewModelMethods) + getFinalCode(canvaComponent, viewModelName,
                viewModelInstanceName, onClickHandlers,
                listOf_Instances,
                listOfChildWhenColumnIsEmptyInstances, listOfRepeatableChildForColumn_Instances,
                componentsInstances, componentsInsideGetChildren,
                listOfLoadColumnItems_MethodsInvocation, componentsInsideMethodSetup,
                componentsInsideMethodStyles, listOfLoadColumnItems_MethodsDeclaration);
    }

    private static String getFinalCode(
            CanvaComponentV2 canvaComponent,
            String viewModelName,
            String viewModelInstanceName,
            ArrayList<String> onClickHandlers,
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

        code
                .append("class Screen extends Pane {\n\t");

        if (!viewModelName.isEmpty()) {
            code.append("\n\t%s %s = new %s();\n\t".formatted(viewModelName, viewModelInstanceName, viewModelName));
        }

        code.append(String.join("\n\t", listOf_Instances));
        code.append("\n\t");

        code.append(String.join("\n\t", listOfChildWhenColumnIsEmptyInstances));
        code.append("\n\t");
        code.append(String.join("\n\t", listOfRepeatableChildForColumn_Instances));
        code.append("\n\t");
        code.append(String.join("\n\t", componentsInstances));

        code.append("\n\t{\n");

        code.append("\n\t\tgetChildren().addAll(\n\t\t");
        code.append(String.join(",\n\t\t", componentsInsideGetChildren));
        code.append("\n\t\t);\n");

        code.append("\t\tsetup();\n");
        code.append("\t\tstyles();\n");

        if (!onClickHandlers.isEmpty()) {
            code.append("\n\t\t// Lógica de Eventos de Clique (usando ViewModel)\n");
            code.append(String.join("\n\t\t", onClickHandlers));
        }

        code.append("\n\t\t");
        code.append(String.join("\n\t\t", listOfLoadColumnItems_MethodsInvocation));
        code.append("\n");

        code.append("\t}\n\n");

        code.append("\tvoid setup(){\n\t\t");

        String config = "this.setPrefSize(%.0f, %.0f);\n\t\t".formatted(
                canvaComponent.getPrefWidth(),
                canvaComponent.getPrefHeight());
        code.append(config);

        code.append(String.join("\n\t\t", componentsInsideMethodSetup));
        code.append("\n\t  }\n\n");

        code.append("\tvoid styles(){\n\t\t");
        code.append("setStyle(\"%s\");\n\t\t".formatted(canvaComponent.getStyle()));
        code.append(String.join("\n\t\t", componentsInsideMethodStyles));
        code.append("\n\t  }");

        code.append("\n\n");
        code.append(String.join("\n\n", listOfLoadColumnItems_MethodsDeclaration));

        code.append("\n\n}");

        System.out.println(code.toString());
        return code.toString();
    }

    // O método 'createComponentsForPreview' não será refatorado profundamente, pois
    // lida com a lógica interna do CustomComponent, mas é mantido aqui.
    public List<String> createComponentsForPreview(ObservableList<Node> nodesInCanva) {

        var componentsClassCreation = new ArrayList<String>();
        int customComponentCount = 0;

        for (int i = 0; i < nodesInCanva.size(); i++) {
            Node node = nodesInCanva.get(i);

            if (node instanceof CustomComponent customComponent) {
                StringBuilder code = new StringBuilder();
                customComponentCount++;

                var collections = new CodeCollections(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                int textCount = 0;
                int btnCount = 0;
                int imgCount = 0;
                int inputCount = 0;

                code.append("class Component%d extends Pane {\n\t".formatted(customComponentCount));

                for (var child : customComponent.getChildren()) {

                    // Note: Os contadores aqui são locais e precisam ser incrementados dentro do loop interno.

                    if (child instanceof TextComponent component) {
                        textCount++;
                        String finalName = "text" + textCount;

                        String textCreation = "Text %s = new Text(\"%s\");".formatted(finalName, component.getText());
                        collections.instances().add(textCreation);

                        addCommonComponentCode(
                                finalName, component, component.getStyle(),
                                collections.children(), collections.setup(), collections.styles());
                    }

                    if (child instanceof Button component) {
                        btnCount++;
                        String finalName = "button" + btnCount;

                        String btnCreation = "Button %s = new Button(\"%s\");".formatted(finalName, component.getText());
                        collections.instances().add(btnCreation);

                        addCommonComponentCode(
                                finalName, component, component.getStyle(),
                                collections.children(), collections.setup(), collections.styles());
                    }

                    if (child instanceof ImageComponent component) {
                        imgCount++;
                        String finalName = "imgV" + imgCount;

                        String imgViewCreation = "ImageView %s = new ImageView();".formatted(finalName);
                        collections.instances().add(imgViewCreation);

                        // Lógica Única de Image
                        Image img = component.getImage();
                        String url = (img != null && img.getUrl() != null) ? img.getUrl() : "";
                        String urlstr = "final var url = \"%s\";".formatted(url);
                        String setImageStr = "%s.setImage(new Image(url));".formatted(finalName);
                        var h = component.getFitHeight();
                        var w = component.getFitWidth();
                        String wstr = "%s.setFitWidth(%.0f);".formatted(finalName, w);
                        String hstr = "%s.setFitHeight(%.0f);".formatted(finalName, h);

                        // inside setup (Unique and Common)
                        collections.setup().add(urlstr);
                        collections.setup().add(wstr);
                        collections.setup().add(hstr);
                        collections.setup().add(setImageStr);

                        addCommonComponentCode(
                                finalName, component, component.getStyle(),
                                collections.children(), collections.setup(), collections.styles());
                    }

                    if (child instanceof InputComponent component) {
                        inputCount++;
                        String finalName = "input" + inputCount;

                        String textCreation = "TextField %s = new TextField(\"%s\");".formatted(finalName, component.getText());
                        collections.instances().add(textCreation);

                        // Lógica Única de Input
                        String setPromptText = "%s.setPromptText(\"%s\");".formatted(finalName, component.getPromptText());
                        collections.setup().add(setPromptText);

                        addCommonComponentCode(
                                finalName, component, component.getStyle(),
                                collections.children(), collections.setup(), collections.styles());
                    }

                }

                code.append(String.join("\n\t", collections.instances()));

                code.append("\n\t{\n");
                code.append("\n\t\tgetChildren().addAll(\n\t\t");
                code.append(String.join(",\n\t\t", collections.children()));
                code.append("\n\t\t);\n");

                code.append("\t\tsetup();\n");
                code.append("\t\tstyles();\n");

                code.append("\t}\n\n");

                code.append("\tvoid setup(){\n\t\t");

                String config = "this.setPrefSize(%.0f, %.0f);\n\t\t".formatted(
                        customComponent.getPrefWidth(),
                        customComponent.getPrefHeight());
                code.append(config);

                code.append(String.join("\n\t\t", collections.setup()));
                code.append("\n\t  }\n\n");

                code.append("\tvoid styles(){\n\t\t");
                code.append("setStyle(\"%s\");\n\t\t".formatted(customComponent.getStyle()));
                code.append(String.join("\n\t\t", collections.styles()));
                code.append("\n\t  }\n\n");

                code.append("}");

                System.out.println(code.toString());

                componentsClassCreation.add(code.toString());

            }

        }

        return componentsClassCreation;
    }

}