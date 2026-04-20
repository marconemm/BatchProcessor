package br.com.getronics.controllers;

import br.com.getronics.Model.WorkbookItem;
import br.com.getronics.database.Configs;
import br.com.getronics.utils.enums.E_Project;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class HomeController {
    private final List<File> selectedWorkBooksList;
    private final String CONFIG_FILE;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Configs savedConfigs;

    private File lastAccessedPath;

    @FXML
    private TextField inputTextOutputDir;
    @FXML
    private ScrollPane spWorkBooks;
    @FXML
    private VBox vBoxSelectedFiles;
    @FXML
    private Button btnStart;
    @FXML
    private ProgressBar pbWorkBooks;
    @FXML
    private Label lblProgressBar;
    @FXML
    private FontIcon fiPickOutputDir;

    public HomeController() {
        final String CONFIG_DIR = Paths.get(System.getProperty("user.home"),
                "." + E_Project.PROJECT_NAME.getValue()).toString();
        selectedWorkBooksList = new ArrayList<>();
        CONFIG_FILE = Paths.get(CONFIG_DIR,
                "." + E_Project.PROJECT_NAME.getValue() + "_config.json").toString();
        savedConfigs = loadConfig();

        lastAccessedPath = new File(savedConfigs.getLastWorkBooksDir());
    }

    public void initialize() {
        inputTextOutputDir.setText(savedConfigs.getLastOutPutDir());
    }

    public void selectWorkBooks(ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Planilhas");

        // Only excel files filter:
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel", "*.xlsx", "*.xls")
        );

        if (lastAccessedPath != null && lastAccessedPath.exists()) {
            fileChooser.setInitialDirectory(lastAccessedPath);
        }

        final Window parentWindow = ((Node) e.getSource()).getScene().getWindow();
        final List<File> files = fileChooser.showOpenMultipleDialog(parentWindow);

        if (files != null && !files.isEmpty()) {
            final Configs configs = new Configs();

            lastAccessedPath = files.getFirst().getParentFile();
            selectedWorkBooksList.clear();
            lblProgressBar.setVisible(false);
            pbWorkBooks.setProgress(0);
            selectedWorkBooksList.addAll(files);
            configs.setLastWorkBooksDir(lastAccessedPath.getPath());
            saveConfig(configs);
            updateListView();
            btnStart.setDisable(false);
        }
    }

    public void startProcess() {
        // 1. To avoid double clicks:
        btnStart.setDisable(true);
        pbWorkBooks.setProgress(0);
        lblProgressBar.setVisible(true);
        getLogger().info("Iniciando processamento das planilhas...");

        // 2. Process task:
        final Task<Void> processTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                final int total = selectedWorkBooksList.size();

                for (int i = 0; i < total; i++) {
                    final File selectedWorkBook = selectedWorkBooksList.get(i);
                    final int atual = i + 1;

                    updateMessage(String.format("Análise(s): %d/%d", atual, total));
                    processarPlanilha(selectedWorkBook);
                    getLogger().debug("Arquivo \"{}\" processado com sucesso!", selectedWorkBook.getName());
                    Thread.sleep(500);

                    // Update the progress bar:
                    updateProgress(i + 1, total);
                }
                return null;
            }
        };

        // 3. Bind the task:
        pbWorkBooks.progressProperty().bind(processTask.progressProperty());
        lblProgressBar.textProperty().bind(processTask.messageProperty());

        // 4. On finish:
        processTask.setOnSucceeded(_ -> {
            getLogger().info("Processamento concluído com sucesso!");
            btnStart.setDisable(false);
            lblProgressBar.textProperty().unbind();
            lblProgressBar.setText("Concluído!");
            pbWorkBooks.progressProperty().unbind();
        });

        processTask.setOnFailed(_ -> {
            addLog("");
            btnStart.setDisable(false);
            Throwable ex = processTask.getException();
            getLogger().error("processTaskFailed(): Falha no processamento.");
            getLogger().error("processTaskFailed(): {}", ex.getMessage());
        });

        // 5. Rodar em uma Thread separada
        final Thread thread = new Thread(processTask);

        thread.setDaemon(true); // To kill the thread if the app closes.
        thread.start();
    }

    public void selectOutputDir(ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        final String txt = String.format("%s_%s_lotes.txt", E_Project.PROJECT_NAME.getValue(), now.format(formatter));
        final File currFile = new File(inputTextOutputDir.getText());
        final Configs configs = new Configs();

        fileChooser.setTitle("Definir Arquivo de Saída");
        // 1-) Only *.txt:
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt")
        );
        // 2-) Suggest a filename:
        fileChooser.setInitialFileName(txt);

        if (currFile.getParentFile() != null && currFile.getParentFile().exists()) {
            fileChooser.setInitialDirectory(currFile.getParentFile());
        }

        final File finalFile = fileChooser.showSaveDialog(inputTextOutputDir.getScene().getWindow());

        // 3-) Make sure that the finalFile ends with *.txt:
        if (finalFile != null) {
            String path = finalFile.getAbsolutePath();

            if (!path.toLowerCase().endsWith(".txt")) {
                path += ".txt";
            }

            inputTextOutputDir.setText(path);

            // 4-) Save the config:
            configs.setLastOutPutDir(path);
            saveConfig(configs);

            //5-) Update the FontIcon:
            fiPickOutputDir.setIconLiteral("far-folder-open");

        }
    }

    private void updateListView() {
        vBoxSelectedFiles.getChildren().clear();

        if (!selectedWorkBooksList.isEmpty()) {
            for (File file : selectedWorkBooksList) {
                final WorkbookItem item = new WorkbookItem(file, (fielToRemove) -> {

                    selectedWorkBooksList.remove(fielToRemove.getFile());

                    if (selectedWorkBooksList.isEmpty()) {
                        btnStart.setDisable(true);
                    }

                    updateListView();
                    getLogger().info("atualizarListaVisual: Arquivo \"{}\" removido.",
                            fielToRemove.getFile().getName()
                    );
                });

                vBoxSelectedFiles.getChildren().add(item.getContainer());
            }
        } else {
            final Label lblArquivo = new Label("(Nenhuma planilha selecionada)");
            vBoxSelectedFiles.getChildren().add(lblArquivo);
        }

        rolarParaDireita();
    }

    private void saveConfig(final Configs configs) {
        final Configs currentConfig = loadConfig();
        final Configs updatedConfig = currentConfig.update(configs);
        final File configFile = new File(CONFIG_FILE);

        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, updatedConfig);

    }

    private Configs loadConfig() {
        try {
            final File configFile = new File(CONFIG_FILE);
            final File parentFile = configFile.getParentFile();

            if (parentFile != null && !parentFile.exists()) {
                // mkdirs with 's' to create all the configFile tree:
                if (!parentFile.mkdirs()) {
                    throw new IOException(String.format("Não foi possível criar a \"%s\"", parentFile.getPath()));
                }
            }

            if (configFile.exists()) {
                return mapper.readValue(configFile, Configs.class);
            }
        } catch (IOException ioe) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);

            getLogger().error("loadConfig: {}", ioe.getLocalizedMessage());
            alert.setTitle("Erro");
            alert.setHeaderText(ioe.getLocalizedMessage());
            alert.show();
        }

        return new Configs(); // By default.
    }

    private void rolarParaDireita() {
        spWorkBooks.applyCss();
        spWorkBooks.layout();

        Platform.runLater(() -> {
            spWorkBooks.setHvalue(1.0);
            spWorkBooks.setVvalue(1.0);
        });
    }

    private void processarPlanilha(final File arquivo) {
        addLog(arquivo.getName() + " - Processado.");
    }

    private void addLog(final String msg) {
        System.out.println(msg);
        //TODO: Adicionar logs (interface), aqui...
    }
}
