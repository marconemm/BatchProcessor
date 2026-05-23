package br.com.getronics.controllers;

import br.com.getronics.interfaces.Shutdownable;
import br.com.getronics.utils.enums.E_Project;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.logging.log4j.LogManager.getLogger;

public final class BaseController implements Shutdownable {

    public boolean isToCloseApplication() {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        final AtomicBoolean result = new AtomicBoolean(false);

        alert.setTitle("Fechar o " + E_Project.PROJECT_NAME.getValue());
        alert.setHeaderText("Deseja realmente fechar o programa?");

        final ButtonType btnYes = new ButtonType("Sim");
        final ButtonType btnNo = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnYes, btnNo);
        alert.showAndWait().ifPresent(response -> result.set(response == btnYes));

        return result.get();
    }

    public void stopApplication(final Shutdownable content) {
        content.stop();
    }

    public void openNativeBrowser(final String url) {
        final String OS = System.getProperty("os.name").toLowerCase();

        try {
            if (OS.contains("win")) {
                // Windows:
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                // Linux OS:
                new ProcessBuilder("xdg-open", url).start();
            } else if (OS.contains("mac")) {
                // MacOS:
                new ProcessBuilder("open", url).start();
            } else {
                getLogger().warn("openNativeBrowser(): OS unknown.");
            }
        } catch (Exception ex) {
            getLogger().error("openNativeBrowser(): {}", ex.getLocalizedMessage());
        }
    }
}
