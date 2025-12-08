package my_app.contexts;

public class ComponentsContext {

    /*
    public SimpleObjectProperty<SelectedComponent> nodeSelected = new SimpleObjectProperty<>();

    public ObservableMap<String, ObservableList<ViewContract<?>>> dataMap = FXCollections
            .observableHashMap();

    public SimpleStringProperty headerSelected = new SimpleStringProperty(null);

    public SimpleBooleanProperty leftItemsStateRefreshed = new SimpleBooleanProperty(false);

    private CanvaComponent mainCanvaComponent;

    public void reset() {
        // mainCanvaComponent = canvaComponent;

        String idOfComponentSelected = null;
        nodeSelected.set(null);
        headerSelected.set(null);
        dataMap.clear();
        refreshSubItems();
    }

    public void refreshSubItems() {
        leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());
    }




    public record SelectedComponent(String type, Node node) {
    }

    public boolean currentNodeIsSelected(String nodeId) {

        SelectedComponent selected = nodeSelected.get();

        // 1. Verifica se algo está selecionado (selected != null)
        // 2. Verifica se o Node dentro do SelectedComponent não é nulo (selected.node()
        // != null)
        // 3. Compara o ID do Node selecionado com o nodeId fornecido
        return selected != null && selected.node() != null && selected.node().getId().equals(nodeId);
    }

    //todo mover para CanvaMapper em fromStateScreenToCanva()
    @Deprecated
    public void loadJsonState_(File projectFile, CanvaComponent canvaComponent, Stage stage) {
        canvaComponent.getChildren().clear();

        mainCanvaComponent = canvaComponent;

        String idOfComponentSelected = null;
        nodeSelected.set(null);
        headerSelected.set(null);
        dataMap.clear();

        ObjectMapper om = new ObjectMapper();

        if (projectFile == null || !projectFile.exists() || projectFile.length() == 0) {
            return;
        }

        try {
            final var projectData = om.readValue(projectFile, Project.class);
            final var state = projectData.screens().getFirst();

            state.canva.name = state.name;
            //var state = om.readValue(file, StateJson_v2.class);
            mainCanvaComponent.applyData(state.canva);

            if (state.id_of_component_selected != null) {
                idOfComponentSelected = state.id_of_component_selected;
            }

            for (TextComponentData data : state.text_components) {
                //TextComponent comp = new TextComponent(data.text(), this, mainCanvaComponent);
                //    comp.applyData(data);
                // nodes.add(comp);

                // subItemsContext.addItem("text", data.identification());
                //   addItem("text", comp);

                if (data.in_canva()) {
                    //     mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura os botões
            for (ButtonComponentData data : state.button_components) {
                // ButtonComponent comp = new ButtonComponent(this, canvaComponent);

                //  comp.applyData(data);
                // nodes.add(comp);
                // subItemsContext.addItem("button", data.identification());
                //  addItem("button", comp);

                if (data.in_canva()) {
                    //      mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura as imagens
            for (ImageComponentData data : state.image_components) {
                //   ImageComponent comp = new ImageComponent(this, canvaComponent);
                //    comp.stage = stage;

                //    comp.applyData(data);
                // nodes.add(comp);

                ///   addItem("image", comp);
                if (data.in_canva()) {
                    //        mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            // Restaura inputs
            for (InputComponentData data : state.input_components) {
                //  InputComponent comp = new InputComponent("", this, canvaComponent);

                //   comp.applyData(data);
                // nodes.add(comp);

                //  addItem("input", comp);

                if (data.in_canva()) {
                    //   mainCanvaComponent.addElementDragable(comp, false);

                }
            }

            for (CustomComponentData data : state.custom_components) {
                // var comp = new CustomComponent(this, canvaComponent);

                //   comp.applyData(data);
                // nodes.add(comp);

                //  addItem("component", comp);

                if (data.in_canva) {
                    //      mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            for (ColumnComponentData data : state.column_components) {
                //   var comp = new ColumnComponent(this, mainCanvaComponent);

                //    comp.applyData(data);
                // nodes.add(comp);

                //   addItem("column items", comp);

                if (data.in_canva()) {
                    //      mainCanvaComponent.addElementDragable(comp, false);
                }
            }

            SearchNodeById(idOfComponentSelected).ifPresent(node -> selectNode(node.getCurrentNode()));

            leftItemsStateRefreshed.set(!leftItemsStateRefreshed.get());

            headerSelected.set(state.type_of_component_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addItem(String type, ViewContract<?> nodeWrapper) {
        dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList())
                .add(nodeWrapper);
    }


    public String getNodeType(Node node) {
        if (node == null) {
            return null;
        }
        String nodeId = node.getId();

        // Itera sobre o mapa para encontrar a chave (tipo) que contém o Node.
        for (var entry : dataMap.entrySet()) {
            if (entry.getValue().stream().anyMatch(n -> node.getId().equals(nodeId))) {
                return entry.getKey();
            }
        }
        return null;
    }

    // --- NOVO MÉTODO SELECTNODE ---
    public void selectNode(Node node) {
        if (node == null) {
            nodeSelected.set(null);
            headerSelected.set(null); // Desseleciona o header também
        } else {
            String type = getNodeType(node);
            if (type != null) {
                SelectedComponent newSelection = new SelectedComponent(type, node);
                nodeSelected.set(newSelection);
                headerSelected.set(type); // Mantemos o headerSelected por compatibilidade com a UI
                System.out.println("Selecionado: " + node + " (Type: " + type + ")");
            } else {
                // Lidar com o caso onde o nó existe mas não está no dataMap
                System.err.println("Erro: Node encontrado, mas não está registrado no dataMap. ID: " + node.getId());
                nodeSelected.set(null);
                headerSelected.set(null);
            }
        }
        refreshSubItems();
    }

    //here for example is when i only want to select the node for editing inside custom component
    public void selectNodePartially(ViewContract<?> node) {
        var comp = (ComponentData) node.getData();
        SelectedComponent newSelection = new SelectedComponent(comp.type(), node.getCurrentNode());
        nodeSelected.set(newSelection);
        System.out.println("Selecionado: " + node + " (Type: " + comp.type() + ")");
    }

//    public ObservableList<ViewContract<?>> getItemsByType(String type) {
//        var data = dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList());
//        IO.println("getItemsByType(): " + data.size());
//        return data;
//    }


    public List<ViewContract<?>> getItemsByType(String type) {
        ObservableList<ViewContract<?>> originalList =
                dataMap.computeIfAbsent(type, _ -> FXCollections.observableArrayList());

        // Retorna uma lista simples filtrada (List)
        return originalList.stream()
                .filter(component -> !component.isDeleted())
                .collect(java.util.stream.Collectors.toList());
    }

    TranslationContext.Translation englishBase = TranslationContext.instance().getInEnglishBase();

    //inicio
    public void addComponent(String type, CanvaComponentV2 currentCanva) {

        if (type == null || type.isBlank()) {
            return;
        }

        ViewContract<?> node = null;
        var content = "Im new here";

        var typeNormalized = type.trim().toLowerCase();

        if (type.equalsIgnoreCase(englishBase.button())) {
            node = new ButtonComponent(content, this);
        } else if (type.equalsIgnoreCase(englishBase.input())) {
            // node = new InputComponent(content, this, currentCanva);

        } else if (type.equalsIgnoreCase(englishBase.text())) {
            node = new TextComponent(content, this, mainCanvaComponent);

        } else if (type.equalsIgnoreCase(englishBase.image())) {
            node = new ImageComponent(
                    ComponentsContext.class.getResource("/assets/images/mago.jpg").toExternalForm(),
                    this);

        } else if (type.equalsIgnoreCase(englishBase.component())) {
            //  new ShowComponentScene(currentCanva, this).stage.show();
            return;
        } else if (type.equalsIgnoreCase(englishBase.columnItems())) {
            node = new ColumnComponent(this, mainCanvaComponent);
        }

        if (node != null) {

            // 1. Adiciona o nó ao dataMap
            addItem(typeNormalized, node);

            // 2. CRIA E ATUALIZA o nodeSelected com o novo objeto SelectedComponent
            // ESTA É A LINHA CORRIGIDA

            SelectedComponent newSelection = new SelectedComponent(typeNormalized, node.getCurrentNode());
            nodeSelected.set(newSelection);

            // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
            headerSelected.set(typeNormalized);

            // 4. Adiciona o nó à tela (Canva)
            currentCanva.addElementDragable(node.getCurrentNode(), true);

            // 5. Notifica a UI lateral para atualizar a lista
            refreshSubItems();
        }
    }

     //fim

    public void addCustomComponent(ViewContract<?> customComponent, CanvaComponent mainCanva) {
        // mainCanvaComponent = mainCanva;
        // nodes.add(customComponent); // Adiciona à lista mestre
        System.out.println("(addCustomComponent) -> mainCanva in custom component: " + mainCanva);
        addItem(englishBase.component(), customComponent);

        SelectedComponent newSelection = new SelectedComponent(englishBase.component(), customComponent.getCurrentNode());
        nodeSelected.set(newSelection);

        // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
        headerSelected.set(englishBase.component());

        // 4. Adiciona o nó à tela (Canva)
        mainCanva.addElementDragable(customComponent.getCurrentNode(), true);

        // 5. Notifica a UI lateral para atualizar a lista
        refreshSubItems();
    }

    public void addComponent(ViewContract<?> nodeWrapper, CanvaComponent currentCanva) {
        var data = (ComponentData) nodeWrapper.getData();
        var node = nodeWrapper.getCurrentNode();
        var type = data.type();
        // 1. Adiciona o nó ao dataMap
        addItem(type, nodeWrapper);

        // 2. CRIA E ATUALIZA o nodeSelected com o novo objeto SelectedComponent
        // ESTA É A LINHA CORRIGIDA

        SelectedComponent newSelection = new SelectedComponent(type, node);
        nodeSelected.set(newSelection);

        // 3. Atualiza o headerSelected (para manter a compatibilidade da UI)
        headerSelected.set(type);

        // 4. Adiciona o nó à tela (Canva)
        currentCanva.addElementDragable(node, true);

        // 5. Notifica a UI lateral para atualizar a lista
        refreshSubItems();
    }

    public void duplicateComponentInCanva(ViewContract<?> nodeWrapper, CanvaComponent currentCanva) {
        var data = nodeWrapper.getData();

        //inicio
        if (data instanceof CustomComponentData d) {
            var copyComponent = new CustomComponent(this, currentCanva);
            copyComponent.applyData(d);
            copyComponent.setId(String.valueOf(System.currentTimeMillis()));
            this.addComponent(copyComponent, currentCanva);
        }

        if (data instanceof ButtonComponentData d) {
            var copyComponent = new ButtonComponent(this, currentCanva);
            copyComponent.applyData(d);
            copyComponent.setId(String.valueOf(System.currentTimeMillis()));
            this.addComponent(copyComponent, currentCanva);
        }
        if (data instanceof ImageComponentData d) {
            var copyComponent = new ImageComponent(this, currentCanva);
            copyComponent.applyData(d);
            copyComponent.setId(String.valueOf(System.currentTimeMillis()));
            this.addComponent(copyComponent, currentCanva);
        }
        if (data instanceof InputComponentData d) {
            var copyComponent = new InputComponent(this, currentCanva);
            copyComponent.applyData(d);
            copyComponent.setId(String.valueOf(System.currentTimeMillis()));
            this.addComponent(copyComponent, currentCanva);
        }
        if (data instanceof TextComponentData d) {
            var copyComponent = new TextComponent(this, currentCanva);
            copyComponent.applyData(d);
            copyComponent.setId(String.valueOf(System.currentTimeMillis()));
            this.addComponent(copyComponent, currentCanva);
        }

         //fim
    }

    public Optional<ViewContract<?>> SearchNodeById(String nodeId) {
        return dataMap.values()
                .stream()
                .flatMap(list -> list.stream()) // Achata todas as listas em um único stream
                .filter(node -> node.getCurrentNode().getId().equals(nodeId))
                .findFirst();
    }


    // public static void SelectNode(Node node) {
    // nodeSelected.set(node);
    // refreshSubItems();
    // System.out.println("Selecionado: " + node);
    // }


    */
}
