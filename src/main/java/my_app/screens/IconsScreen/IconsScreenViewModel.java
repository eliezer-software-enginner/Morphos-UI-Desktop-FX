package my_app.screens.IconsScreen;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import my_app.themes.ThemeManager;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconsScreenViewModel {

    // Serviço (Mantido, mas não deve expor a UI diretamente)
    private final ThemeManager themeManager = ThemeManager.Instance();

    // DADOS
    public final Map<String, List<Ikon>> iconsMap = IconDatabase.ICONS;

    // ESTADO INTERNO: Map armazena os Nodes criados pela View (cache)
    private final Map<String, List<HBox>> itemsCreatedBuffer = new HashMap<>();

    // PROPRIEDADES DE ESTADO (Reativas para a View)
    public final StringProperty iconMapSelected = new SimpleStringProperty();
    public StringProperty iconIdSelected = new SimpleStringProperty();
    public ObjectProperty<FontIcon> iconItemSelected = new SimpleObjectProperty<>();
    public final ObjectProperty<List<HBox>> currentIconItems = new SimpleObjectProperty<>();


    public void init() {
        // Inicializa o buffer, se necessário.
    }

    // --- COMANDOS (Lógica de Negócios) ---

    // Novo comando que recebe APENAS a chave (dado)
    public void handleClickOnHeaderItem(String key) {
        iconMapSelected.set(key);

        // Lógica de Negócios: Atualiza o estado da lista de ícones atual
        currentIconItems.set(itemsCreatedBuffer.get(key));

        iconItemSelected.set(null);
        iconIdSelected.set(null);
    }

    // NOVO: Getter para o buffer. A View usa isso para armazenar os Nodes que ela cria.
    public Map<String, List<HBox>> itemsCreatedBuffer() {
        return itemsCreatedBuffer;
    }

    // REMOVIDO: handleClickOnIconMap
    // REMOVIDO: highlightItem
    // REMOVIDO: createBufferOfCurrentEntry (a View faz o stream e chama o getter do buffer)
    // REMOVIDO: createItemForHeader
    // REMOVIDO: createItem
}