package br.com.getronics.controllers;

import br.com.getronics.core.ExcelToTextMapper;
import br.com.getronics.core.WorkbookReader;
import br.com.getronics.database.UserConfigs;
import br.com.getronics.interfaces.Shutdownable;
import br.com.getronics.models.views.LogItem;
import br.com.getronics.models.views.WorkbookItem;
import br.com.getronics.utils.enums.E_LogType;
import br.com.getronics.utils.enums.styles.E_Colors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class HomeController implements Shutdownable {
    private final List<File> selectedWorkBooksList;
    private final UserConfigs savedUserConfigs;
    private Task<Void> processTask;

    @FXML
    private TextField inputTextOutputFile;
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
    @FXML
    private ScrollPane spLogs;
    @FXML
    private VBox vBoxLog;

    public HomeController() {
        selectedWorkBooksList = new ArrayList<>();
        savedUserConfigs = new UserConfigs();
        savedUserConfigs.fetchData();
    }

    public void initialize() {
        inputTextOutputFile.setText(savedUserConfigs.getLastOutPutDir());
        inputTextOutputFile.textProperty().addListener(
                (_, oldValue, newValue) -> {
                    updateOutputDir();
                    getLogger().debug("inputTextOutputFile Listener: " +
                            "O texto mudou de: \"{}\" para: \"{}\"", oldValue, newValue);
                });
    }

    public void selectWorkBooks(ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Planilhas");

        // Only excel files filter:
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel", "*.xlsx", "*.xls")
        );
        fileChooser.setInitialDirectory(new File(savedUserConfigs.getLastWorkBooksDir()));

        final Window parentWindow = ((Node) e.getSource()).getScene().getWindow();
        final List<File> files = fileChooser.showOpenMultipleDialog(parentWindow);

        if (files != null && !files.isEmpty()) {
            savedUserConfigs.setLastWorkBooksDir(files.getFirst().getParent());
            savedUserConfigs.update();
            selectedWorkBooksList.clear();
            lblProgressBar.setVisible(false);
            pbWorkBooks.setProgress(0);
            selectedWorkBooksList.addAll(files);

            updateListView();
            btnStart.setDisable(false);
        }

        if (!vBoxLog.getChildren().isEmpty()) {
            vBoxLog.getChildren().clear();
        }
    }

    public void startBatchProcess() {
        // 1. To avoid double clicks:
        btnStart.setDisable(true);
        pbWorkBooks.setProgress(0);
        lblProgressBar.setVisible(true);
        getLogger().info("Iniciando processamento das planilhas...");

        // 2. Process task:
        processTask = new Task<>() {
            @Override
            protected Void call() {
                final int totalFiles = selectedWorkBooksList.size();

                try (final PrintWriter writer = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(savedUserConfigs.getInitialFileName())))) {

                    final ExcelToTextMapper mapper = new ExcelToTextMapper();
                    String logTxt;

                    // 2.1 Remove the Excel security limits:
                    setupExcelLimits(true);

                    for (int i = 0; i < totalFiles; i++) {
                        if (isCancelled()) {
                            logTxt = "Processamento cancelado.";
                            updateMessage(logTxt);
                            getLogger().info("isCancelled()[1]: {}", logTxt);
                            break;
                        }

                        final File selectedWorkBook = selectedWorkBooksList.get(i);
                        final int atual = i + 1;

                        logTxt = "Lendo arquivo: " + selectedWorkBook.getName();

                        updateMessage(String.format("Análise(s): %d/%d", atual, totalFiles));
                        addLog(logTxt, E_LogType.INFO);
                        getLogger().info("processTask.call(): {}", logTxt);

                        // 2.2 Write the initial header:
                        writer.println(mapper.getOutputHeader(selectedWorkBook));

                        // 2.3 Process the entire Excel workbook:
                        try (final Workbook workbook = WorkbookFactory.create(selectedWorkBook)) {
                            final Sheet budgetSheet = workbook.getSheetAt(0);

                            WorkbookReader.evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                            // 2.3.1 Fill the mapper.mappedRowsList:
                            for (final Row row : budgetSheet) {
                                //2.3.1.1 Skip the first 5 sheet lines:
                                if (row.getRowNum() < 4) continue;

                                if (isCancelled()) {
                                    logTxt = "processamento interrompido pelo usuário.";
                                    updateMessage(logTxt);
                                    getLogger().info("isCancelled()[2]: {}", logTxt);
                                    break;
                                }

                                mapper.mapRow(row);
                            }

                            // 2.3.2 Write each iem from mapper.mappedRowsList:
                            mapper.sortRowsList();
                            writer.println(mapper.getBatchRow());

                            // 2.3.3 Update the progress message:
                            updateMessage(String.format("Análise(s): %d/%d", atual, totalFiles));

                        } catch (Exception e) {
                            logTxt = "Erro ao ler " + selectedWorkBook.getName() + ": " + e.getMessage();

                            addLog(logTxt, E_LogType.ERROR);
                            getLogger().error("processTask.call(): {}", logTxt);
                        }

                        addLog(selectedWorkBook.getName() + " - Processado.", E_LogType.SUCCESS);
                        getLogger().debug("Arquivo \"{}\" processado com sucesso!", selectedWorkBook.getName());

                        // 2.4 Update the progress bar:
                        updateProgress(i + 1, totalFiles);
                    }
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                } finally {
                    // 2.5 Make sure to reset the Excel security limits:
                    setupExcelLimits(false);
                }
                return null;
            }
        };

        // 3. Bind the task:
        pbWorkBooks.progressProperty().bind(processTask.progressProperty());
        lblProgressBar.textProperty().bind(processTask.messageProperty());

        // 4. On finish:
        processTask.setOnSucceeded(_ -> {
            addLog("Fim dos processamentos.", E_LogType.SUCCESS);
            getLogger().info("processTaskSucess(): Fim dos processamentos.");
            btnStart.setDisable(false);
            lblProgressBar.textProperty().unbind();
            lblProgressBar.setText("Concluído!");
            pbWorkBooks.progressProperty().unbind();
        });

        processTask.setOnFailed(_ -> {
            final Throwable ex = processTask.getException();

            addLog(ex.getMessage(), E_LogType.ERROR);
            btnStart.setDisable(false);
            getLogger().error("processTaskFailed(): Falha no processamento.");
            getLogger().error("processTaskFailed(): {}", ex.getMessage());
        });

        // 5. Run the task in a aside Thread:
        final Thread t1 = new Thread(processTask);

        t1.setDaemon(true); // To kill the t1 if the app closes.
        t1.start();
    }

    @Override
    public void stop() {
        if (processTask != null && processTask.isRunning()) {
            getLogger().info("stop(): Cancelando Task \"{}\"", processTask.getTitle());
            processTask.cancel();
        }
    }

    public void selectOutputDir() {
        final FileChooser fileChooser = new FileChooser();
        File filePath = new File(inputTextOutputFile.getText());

        fileChooser.setTitle("Definir Arquivo de Saída");
        // 1-) Only *.txt:
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt")
        );
        // 2-) Suggest a filename:
        savedUserConfigs.setLastFileName(null);
        fileChooser.setInitialFileName(savedUserConfigs.getInitialFileName());

        if (filePath.exists()) {
            fileChooser.setInitialDirectory(filePath);
        } else {
            filePath = new File(savedUserConfigs.getDocDir());
            fileChooser.setInitialDirectory(filePath);
        }

        final File finalFile = fileChooser.showSaveDialog(inputTextOutputFile.getScene().getWindow());

        // 3-) Save the config:
        if (finalFile != null) {
            savedUserConfigs.setLastFileName(finalFile.getName());
            savedUserConfigs.setLastOutputDir(finalFile.getParent());
            inputTextOutputFile.setText(savedUserConfigs.getLastOutputFile());

            //4-) Update the FontIcon:
            fiPickOutputDir.setIconLiteral("far-folder-open");
        }
    }

    private void updateOutputDir() {
        final String txt = inputTextOutputFile.getText();
        final File currFile = new File(txt);

        // 1-) Validate:
        if (txt.endsWith(".txt")) {
            // 2-) Update the config:
            if (currFile.exists()) {
                savedUserConfigs.setLastOutputDir(txt);
                savedUserConfigs.setLastFileName(txt);
            } else if (currFile.getParentFile().exists()) {
                savedUserConfigs.setLastOutputDir(currFile.getParent());
                savedUserConfigs.setLastFileName(currFile.getName());
            }

            // 3-) Save the config:/home/satnaka/IdeaProjects/BatchProcessor
            inputTextOutputFile.setStyle(" -fx-border-width: 2;" + "-fx-border-color: "
                    + E_Colors.PRIMARY_ACCENT.getHex());
            savedUserConfigs.update();

        } else {
            inputTextOutputFile.setStyle(
                    " -fx-border-width: 2;" + "-fx-border-color: " + E_Colors.ERROR_RED.getHex());
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

    private void rolarParaDireita() {
        spWorkBooks.applyCss();
        spWorkBooks.layout();

        Platform.runLater(() -> {
            spWorkBooks.setHvalue(1.0);
            spWorkBooks.setVvalue(1.0);
        });
    }

    private void addLog(final String msg, final E_LogType logType) {
        Platform.runLater(() -> {
            final LogItem log = new LogItem(msg, logType);

            vBoxLog.getChildren().add(log.getContainer());
            vBoxLog.applyCss();
            vBoxLog.layout();
            spLogs.setHvalue(1.0);
            spLogs.setVvalue(1.0);
        });
    }

    private void setupExcelLimits(final Boolean isToSet) {
        try {
            if (isToSet) {
                savedUserConfigs.setProperties();
            } else {
                savedUserConfigs.unsetProperties();
            }

        } catch (Exception e) {
            final String errMsg = "Erro ao configurar limites de XML: " + e.getMessage();

            addLog(errMsg, E_LogType.ERROR);
            getLogger().error("processTask.call().setupExcelLimits(): {}", errMsg);
        }
    }
}
