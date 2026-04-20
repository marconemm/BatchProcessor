package br.com.getronics.controllers;

import br.com.getronics.Model.WorkbookItem;
import br.com.getronics.database.UserConfig;
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
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class HomeController {
    private final List<File> selectedWorkBooksList;
    private final String CONFIG_FILE;
    private final ObjectMapper mapper = new ObjectMapper();
    private File lastAccessedPath;
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

    public HomeController() {
        final String CONFIG_DIR = Paths.get(System.getProperty("user.home"),
                "." + E_Project.PROJECT_NAME.getValue()).toString();

        selectedWorkBooksList = new ArrayList<>();
        CONFIG_FILE = Paths.get(CONFIG_DIR,
                "." + E_Project.PROJECT_NAME.getValue() + "_config.json").toString();
        lastAccessedPath = new File(loadConfig());
    }

    @FXML
    private void selectWorkBooks(ActionEvent e) {
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
            lastAccessedPath = files.getFirst().getParentFile();
            selectedWorkBooksList.clear();
            lblProgressBar.setVisible(false);
            pbWorkBooks.setProgress(0);
            selectedWorkBooksList.addAll(files);
            saveConfig(lastAccessedPath.getPath());
            updateListView();
        }
    }

    private void updateListView() {
        vBoxSelectedFiles.getChildren().clear();

        if (!selectedWorkBooksList.isEmpty()) {
            for (File file : selectedWorkBooksList) {
                final WorkbookItem item = new WorkbookItem(file, (fielToRemove) -> {

                    selectedWorkBooksList.remove(fielToRemove.getFile());
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

    @FXML
    private void startProcess() {
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
        processTask.setOnSucceeded(e -> {
            getLogger().info("Processamento concluído com sucesso!");
            btnStart.setDisable(false);
            lblProgressBar.textProperty().unbind();
            lblProgressBar.setText("Concluído!");
            pbWorkBooks.progressProperty().unbind();
        });

        processTask.setOnFailed(e -> {
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

    private void saveConfig(final String path) {
        final UserConfig config = new UserConfig();

        config.setLastDirectory(path);
        mapper.writeValue(new File(CONFIG_FILE), config);
    }

    private String loadConfig() {
        try {
            final File file = new File(CONFIG_FILE);
            final File parentFile = file.getParentFile();

            if (parentFile != null && !parentFile.exists()) {
                // mkdirs with 's' to create all the file tree:
                if (!parentFile.mkdirs()) {
                    throw new IOException(String.format("Não foi possível criar a \"%s\"", parentFile.getPath()));
                }
            }

            if (file.exists()) {
                final UserConfig config = mapper.readValue(file, UserConfig.class);

                return config.getLastDirectory();
            }
        } catch (IOException ioe) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);

            getLogger().error("loadConfig: {}", ioe.getLocalizedMessage());
            alert.setTitle("Erro");
            alert.setHeaderText(ioe.getLocalizedMessage());
            alert.show();
        }

        return System.getProperty("user.home"); // By default.
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
        System.out.println(arquivo.getName() + "Processado...");
    }

    private void addLog(final String msg) {
        //TODO: Adicionar logs (interface), aqui...
    }

    public void selectOutputDir(ActionEvent e) {
        //TODO: Selecionar o caminho de destino dos logs aqui...
    }
}
