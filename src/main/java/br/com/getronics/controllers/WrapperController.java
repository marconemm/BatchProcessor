package br.com.getronics.controllers;

import br.com.getronics.utils.enums.views.E_Fxml;
import br.com.getronics.utils.enums.views.E_windowLayout;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import static org.apache.logging.log4j.LogManager.getLogger;

public class WrapperController {
    @FXML
    private BorderPane mainPane;

    @FXML
    public void initialize() {
        openNewWindow(E_Fxml.HOME, E_windowLayout.CENTER_FULL);
    }

    @FXML
    private void openNewWindow(final E_Fxml fxml, final E_windowLayout layout) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml.getFilename())
            );
            final Parent newWindow = loader.load();

            mainPane.setCenter(newWindow);
            setLayout(layout);
        } catch (NullPointerException npe) {
            final String err = "Wasn't possible to find the file \"" + fxml.getFilename() + "\".";

            getLogger().error("openNewWindow: {}\n{}", err, npe.getLocalizedMessage());
        } catch (IOException ioe) {
            getLogger().error("openNewWindow: {}", ioe.getLocalizedMessage());
        }
    }

    @FXML
    void openConfigWindow() {
        openNewWindow(E_Fxml.SETTINGS, E_windowLayout.CENTER_FULL);
    }

    @FXML
    void openHomeView() {
        openNewWindow(E_Fxml.HOME, E_windowLayout.CENTER_FULL);
    }

    @FXML
    void openAboutWindow() {
        openNewWindow(E_Fxml.ABOUT, E_windowLayout.CENTER_RIGHT_BOTTOM);
    }

    @FXML
    void closeApplication() {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sair do Sistema");
        alert.setHeaderText("Você está prestes a sair.");
        alert.setContentText("Deseja realmente fechar o programa?");

        final ButtonType btnSim = new ButtonType("Sim");
        final ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSim, btnNao);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnSim) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void setLayout(final E_windowLayout layout) {
        switch (layout) {
            case CENTER_FULL -> {
                toggleNode(mainPane.getLeft(), false);
                toggleNode(mainPane.getRight(), false);
                toggleNode(mainPane.getBottom(), false);
            }
            case LEFT_CENTER -> {
                toggleNode(mainPane.getLeft(), true);
                toggleNode(mainPane.getRight(), false);
                toggleNode(mainPane.getBottom(), false);
            }
            case CENTER_RIGHT -> {
                toggleNode(mainPane.getLeft(), false);
                toggleNode(mainPane.getRight(), true);
                toggleNode(mainPane.getBottom(), false);
            }
            case CENTER_RIGHT_BOTTOM -> {
                toggleNode(mainPane.getLeft(), false);
                toggleNode(mainPane.getRight(), true);
                toggleNode(mainPane.getBottom(), true);
            }
            case CENTER_BOTTOM -> {
                toggleNode(mainPane.getLeft(), false);
                toggleNode(mainPane.getRight(), false);
                toggleNode(mainPane.getBottom(), true);
            }
            case LEFT_CENTER_RIGHT -> {
                toggleNode(mainPane.getLeft(), true);
                toggleNode(mainPane.getRight(), true);
                toggleNode(mainPane.getBottom(), false);
            }
            default -> {
                toggleNode(mainPane.getLeft(), true);
                toggleNode(mainPane.getRight(), true);
                toggleNode(mainPane.getBottom(), true);
            }
        }
    }

    private void toggleNode(final Node node, final boolean show) {
        if (node != null) {
            node.setVisible(show);
            node.setManaged(show);
        }
    }
}