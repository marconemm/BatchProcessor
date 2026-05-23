package br.com.getronics.controllers; //[cite: 1]

import br.com.getronics.interfaces.Shutdownable; //[cite: 2]
import br.com.getronics.utils.enums.views.E_Fxml;
import br.com.getronics.utils.enums.views.E_windowLayout;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.net.URI;

public class AboutController implements Shutdownable { //[cite: 2]
    private boolean isOpening;
    @FXML
    private Button btnBack;
    @FXML
    private Hyperlink lnkGithub;

    public AboutController() {
        isOpening = false;
    }

    @FXML
    public void initialize() {
        if (btnBack != null) {
            btnBack.setOnAction(_ -> handleBack());
        }
    }

    @FXML
    private void handleOpenGithub() {
        if (isOpening)
            return;

        isOpening = true;

        final Thread t = new Thread(() -> {
            final BaseController baseController = new BaseController();

            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(lnkGithub.getText()));
                } else {
                    baseController.openNativeBrowser(lnkGithub.getText());
                }
            } catch (Exception e) {
                baseController.openNativeBrowser(lnkGithub.getText());
            } finally {
                isOpening = false;
            }
        });

        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleBack() {
        if (WrapperController.getInstance() != null) {
            WrapperController.getInstance().openNewWindow(E_Fxml.HOME, E_windowLayout.CENTER_FULL);
        }
    }
}