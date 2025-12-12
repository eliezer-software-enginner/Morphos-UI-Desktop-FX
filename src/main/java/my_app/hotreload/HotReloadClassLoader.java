package my_app.hotreload;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Set;

public class HotReloadClassLoader extends URLClassLoader {

    private final Set<String> classesToExclude;

    public HotReloadClassLoader(URL[] urls, ClassLoader parent, Set<String> classesToExclude) {
        super(urls, parent);
        this.classesToExclude = classesToExclude != null ? classesToExclude : Collections.emptySet();
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 1. Tenta encontrar a classe JÁ CARREGADA por ESTE ClassLoader
        // Se já foi carregada, não tenta redefinir.
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            if (resolve) resolveClass(loadedClass);
            return loadedClass;
        }

        // 2. Regras de exclusão (Java, JavaFX, classes da lib, classes do App)
        // Se a classe está excluída, DELEGAMOS ao ClassLoader PAI (System ClassLoader),
        // que deve carregar a versão estática.
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("javafx.") ||
                name.startsWith("plantfall.") || classesToExclude.contains(name)) {

            return super.loadClass(name, resolve);
        }

        // 3. Se não está excluída e não foi carregada, tentamos carregar a versão nova
        // na pasta target/classes usando findClass (que define a classe).
        try {
            Class<?> c = findClass(name);
            if (resolve) resolveClass(c);
            return c;
        } catch (ClassNotFoundException e) {
            // Se findClass falhar (o arquivo .class não existe no classesPath),
            // delegamos ao parent (super.loadClass) como fallback.
            return super.loadClass(name, resolve);
        }
    }
}