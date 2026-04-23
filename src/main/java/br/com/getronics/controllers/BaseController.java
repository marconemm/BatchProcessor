package br.com.getronics.controllers;

import br.com.getronics.interfaces.Shutdownable;
import br.com.getronics.utils.enums.E_Project;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.concurrent.atomic.AtomicBoolean;

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
}
