package br.com.getronics.controllers;

import br.com.getronics.Model.WorkbookItem;
import br.com.getronics.database.UserConfig;
import br.com.getronics.utils.enums.E_Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class HomeController {
    private final List<File> selectedFilesList;
    private final String CONFIG_FILE;
    private final String CONFIG_DIR;

    private File lastAcessedPath;
    private ObjectMapper mapper = new ObjectMapper();

    @FXML
    private ScrollPane spSelectedFiles;
    @FXML
    private VBox vBoxSelectedFiles; // O VBox que você acabou de criar no ScrollPane

    public HomeController() {
        selectedFilesList = new ArrayList<>();
        CONFIG_DIR = Paths.get(System.getProperty("user.home"),
                "." + E_Project.PROJECT_NAME.getValue()).toString();
        CONFIG_FILE = Paths.get(CONFIG_DIR,
                "." + E_Project.PROJECT_NAME.getValue() + "_config.json").toString();
        lastAcessedPath = new File(loadConfig());
    }

    @FXML
    void selectWorkBooks(ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Planilhas");

        // Only excel files filter:
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel", "*.xlsx", "*.xls")
        );

        if (lastAcessedPath != null && lastAcessedPath.exists()) {
            fileChooser.setInitialDirectory(lastAcessedPath);
        }

        final Window parentWindow = ((Node) e.getSource()).getScene().getWindow();
        final List<File> files = fileChooser.showOpenMultipleDialog(parentWindow);

        if (files != null && !files.isEmpty()) {
            lastAcessedPath = files.getFirst().getParentFile();
            selectedFilesList.clear();
            selectedFilesList.addAll(files);
            saveConfig(lastAcessedPath.getPath());
            atualizarListaVisual();
        }
    }

    private void atualizarListaVisual() {
        vBoxSelectedFiles.getChildren().clear();

        if (!selectedFilesList.isEmpty()) {
            for (File file : selectedFilesList) {
                final WorkbookItem item = new WorkbookItem(file, (fielToRemove) -> {

                    selectedFilesList.remove(fielToRemove.getFile());
                    atualizarListaVisual();
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

    private void saveConfig(final String path) {
        try {
            final UserConfig config = new UserConfig();

            config.setLastDirectory(path);
            mapper.writeValue(new File(CONFIG_FILE), config);
        } catch (IOException ioe) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);

            getLogger().error("saveConfig: {}", ioe.getLocalizedMessage());
            alert.setTitle("Erro");
            alert.setHeaderText(ioe.getLocalizedMessage());
            alert.show();
        }
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
        spSelectedFiles.applyCss();
        spSelectedFiles.layout();

        Platform.runLater(() -> {
            spSelectedFiles.setHvalue(1.0);
            spSelectedFiles.setVvalue(1.0);
        });
    }
}
