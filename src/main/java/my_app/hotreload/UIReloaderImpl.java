// UIReloaderImpl1.java
package my_app.hotreload;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class UIReloaderImpl implements Reloader {

    private static final String APP_CLASS_NAME = "my_app.App";
    private static final String REINIT_METHOD_NAME = "initializeScene"; // O novo método estático em App

    @Override
    public void reload(Object context) {
        if (context instanceof Stage mainStage) {

            Platform.runLater(() -> {

                try {
                    // 1. Obtém o ClassLoader atual (HotReloadClassLoader)
                    ClassLoader currentClassLoader = this.getClass().getClassLoader();

                    // 2. Carrega a App class (que contém initializeScene) NO NOVO ClassLoader
                    Class<?> newAppClass = currentClassLoader.loadClass(APP_CLASS_NAME);
                    System.out.println("[UIReloader] App class loaded by: " + newAppClass.getClassLoader().getClass().getSimpleName());

                    // 3. A anotação @CoesionApp foi excluída, então usamos o App original (System CL)
                    // para buscar a anotação e os estilos
                    Class<?> originalAppClass = Class.forName(APP_CLASS_NAME, true, ClassLoader.getSystemClassLoader());
                    CoesionApp globalAnnotation = originalAppClass.getAnnotation(CoesionApp.class);

                    if (globalAnnotation == null) {
                        System.err.println("Annotation @CoesionApp not found on App class. Check exclusions.");
                        return;
                    }

                    // 4. Aplica estilos globais
                    List<String> globalStyles = Arrays.asList(globalAnnotation.stylesheets());
                    Scene mainScene = mainStage.getScene();
                    if (mainScene != null) {
                        mainScene.getStylesheets().clear();
                        applyStylesToScene(mainScene, globalStyles, originalAppClass);
                    }


                    // 🛑 5. Tenta encontrar e chamar o método de re-inicialização
                    try {
                        // Busca o método estático 'initializeScene' na NOVA CLASSE App
                        Method reinitMethod = newAppClass.getDeclaredMethod(REINIT_METHOD_NAME, Stage.class);
                        reinitMethod.setAccessible(true);

                        System.out.println("[UIReloader] Invoking static initialization method on NEW App class: " + REINIT_METHOD_NAME);
                        // Chama o método estático, passando o Stage principal.
                        // Toda a lógica de UI dentro de initializeScene será resolvida pelo HotReloadClassLoader.
                        reinitMethod.invoke(null, mainStage);

                        System.out.println("[UIReloader] UI updated via new App." + REINIT_METHOD_NAME + "().");


                    } catch (NoSuchMethodException e) {
                        System.err.println("[UIReloader] Required re-initialization method " + REINIT_METHOD_NAME + "(Stage) not found in new my_app.App. UI changes will not be applied.");
                    }


                    System.out.println("[UIReloader] UI styles updated and re-initialization attempt finished.");

                } catch (Exception e) {
                    System.err.println("[UIReloader] Error during UI reload process.");
                    e.printStackTrace();
                }
            });
        }
    }

    private void applyStylesToScene(Scene scene, List<String> stylesPaths, Class<?> contextClassForResources) {
        if (scene == null || stylesPaths.isEmpty()) return;

        for (String stylePath : stylesPaths) {
            try {
                URL resource = contextClassForResources.getResource(stylePath);
                if (resource != null) {
                    String cssUrl = resource.toExternalForm();
                    if (!scene.getStylesheets().contains(cssUrl)) {
                        scene.getStylesheets().add(cssUrl);
                        System.out.println("[UIReloader] CSS reloaded: " + stylePath);
                    }
                } else {
                    System.err.println("[UIReloader] Resource not found: " + stylePath);
                }
            } catch (Exception e) {
                System.err.println("[UIReloader] Error loading resource: " + stylePath + " using class " + contextClassForResources.getName());
                e.printStackTrace();
            }
        }
    }
}