package my_app.screens.ScreenCreateProject;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import my_app.FileManager;
import my_app.data.PrefsDatav2;
import my_app.scenes.AppScenes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScreenCreateProjectViewModelTest {
    // Dependências que serão simuladas (Mocks)
    @Mock
    private Stage mockStage;

    @Mock
    private File mockFile; // Usado para simular o arquivo salvo

    // Mocks Estáticos: FileManager e AppScenes são classes estáticas.
    // Usamos Mockito.mockStatic para interceptar chamadas estáticas.
    // Você faria isso dentro dos métodos @Test (veja abaixo).

    // A classe a ser testada, onde os Mocks serão injetados
    // NOTA: @InjectMocks não funciona bem com o construtor da VM,
    // então faremos a injeção manualmente.
    private ScreenCreateProjectViewModel viewModel;

    @BeforeEach
    void setUp() throws Exception {
        // Inicializa todos os mocks definidos acima
        MockitoAnnotations.openMocks(this);

        // Simula o FileManager.loadDataInPrefsv2() para ter um estado inicial conhecido
        try (var mockedFileManager = mockStatic(FileManager.class)) {
            // Simula o retorno de PrefsData com projetos recentes.
            var mockPrefsData = new PrefsDatav2(null, "pt-br", List.of("path/to/project1", "path/to/project2"));
            // mockedFileManager.when(FileManager::loadDataInPrefsv2).thenReturn(mockPrefsData);

            // Cria a ViewModel (que chama loadRecentProjects no construtor)
            viewModel = new ScreenCreateProjectViewModel(mockStage);
        }
    }

    // --- Testes de Inicialização e Dados ---

    @Test
    void testInitialDataLoading() {
        // Verifica se os projetos recentes foram carregados corretamente no setUp
        assertEquals(2, viewModel.recentProjects.size());
        assertTrue(viewModel.recentProjects.contains("path/to/project1"));
    }

    // --- Testes de Validação ---

    @Test
    void testValidation_EmptyInput() {
        viewModel.inputTextProperty.set("");

        // Chama o comando (apenas a validação será executada antes do 'return')
        viewModel.handleClickCreateProject();

        // Verifica o estado: A mensagem de erro deve ser definida
        assertNotNull(viewModel.errorMessageProperty.get());
        assertEquals("O nome do projeto está vazio!", viewModel.errorMessageProperty.get());
    }

    @Test
    void testValidation_TooShortInput() {
        viewModel.inputTextProperty.set("abc");
        viewModel.handleClickCreateProject();

        assertEquals("O nome do projeto está muito curto!", viewModel.errorMessageProperty.get());
    }

    // --- Testes de Comando de Abrir Projeto Existente ---

    @Test
    void testHandleOpenExistingProject_Success() {
        String path = "path/to/project/existing.json";
        Scene mockScene = mock(Scene.class); // Mock para a cena de Home

        try (var mockedFileManager = mockStatic(FileManager.class);
             var mockedAppScenes = mockStatic(AppScenes.class)) {

            // Simula o comportamento das classes estáticas
            mockedAppScenes.when(() -> AppScenes.HomeScene(mockStage)).thenReturn(mockScene);

            // A chamada real deve acontecer
            viewModel.handleOpenExistingProject(path);

            // 1. Verifica se o FileManager foi chamado para definir o projeto ativo
            mockedFileManager.verify(() -> FileManager.setLastProject(path), times(1));

            // 2. Verifica se a navegação foi acionada
            verify(mockStage, times(1)).setScene(mockScene);

            // 3. Verifica se a mensagem de erro está limpa
            assertNull(viewModel.errorMessageProperty.get());
        }
    }

    @Test
    void testHandleOpenExistingProject_FileManagerFails() {
        String path = "path/to/project/existing.json";

        try (var mockedFileManager = mockStatic(FileManager.class)) {
            // Simula uma exceção na camada de I/O
            mockedFileManager.when(() -> FileManager.setLastProject(path))
                    // 2. Define o que o método deve fazer (thenThrow)
                    .thenThrow(new RuntimeException("Simulated I/O Error"));

            // A chamada real
            viewModel.handleOpenExistingProject(path);

            // Verifica o estado: A mensagem de erro deve ser definida
            assertNotNull(viewModel.errorMessageProperty.get());
            assertTrue(viewModel.errorMessageProperty.get().contains("Simulated I/O Error"));
        }
    }

    // --- Testes de Comando de Criação de Projeto ---

    @Test
    void testHandleClickCreateProject_Success() {
        viewModel.inputTextProperty.set("NovoProjeto");
        Scene mockScene = mock(Scene.class);
        // Removendo: FileChooser mockFc = mock(FileChooser.class); // Não é necessário aqui

        // Define o mock File para ser retornado pelo diálogo
        File mockFile = mock(File.class);

        try (var mockedFileManager = mockStatic(FileManager.class);
             var mockedAppScenes = mockStatic(AppScenes.class);
             var mockedFileChooser = mockConstruction(FileChooser.class, (mock, context) -> {

                 // --- CORREÇÃO AQUI ---
                 // 1. Criar um mock para a lista de ExtensionFilters
                 ObservableList<FileChooser.ExtensionFilter> mockFilterList = mock(ObservableList.class);

                 // 2. Configurar o FileChooser mockado para retornar a lista mockada
                 when(mock.getExtensionFilters()).thenReturn(mockFilterList);

                 // 3. Configurar o showSaveDialog (comportamento de sucesso)
                 when(mock.showSaveDialog(mockStage)).thenReturn(mockFile);

             })) {

            mockedAppScenes.when(() -> AppScenes.HomeScene(mockStage)).thenReturn(mockScene);

            // A chamada real
            viewModel.handleClickCreateProject();

            // 1. Verifica se a função de salvar do FileManager foi chamada
            mockedFileManager.verify(() -> FileManager.saveProjectAndAddToRecents(eq("NovoProjeto"), eq(mockFile)), times(1));

            // 2. Verifica se a notificação de sucesso foi emitida
            assertEquals("Project was created!", viewModel.showToastProperty.get());

            // 3. Verifica se a navegação foi acionada
            verify(mockStage, times(1)).setScene(mockScene);

            // Opcional: Verifique se o método add foi chamado na lista mockada
            // O mockFilterList não é diretamente acessível fora da lambda,
            // mas o teste passa se o NPE for resolvido.

        } catch (Exception e) {
            fail("Exceção inesperada: " + e.getMessage());
        }
    }
}