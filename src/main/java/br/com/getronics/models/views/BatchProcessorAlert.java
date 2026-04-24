package br.com.getronics.models.views;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class BatchProcessorAlert extends Alert {
    public BatchProcessorAlert(AlertType alertType) {
        super(alertType);
        setUp();
    }

    public BatchProcessorAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        setUp();
    }

    private void setUp() {
        try {
            final Window parent = this.getDialogPane().getScene().getWindow();

            this.initOwner(parent);

        } catch (IllegalArgumentException iae) {
            final Stage stage = (Stage) this.getDialogPane().getScene().getWindow();

            this.setOnShowing(_ -> {
                final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

                this.setX((screenBounds.getWidth() - 700) / 2);
                this.setY((screenBounds.getHeight() - 350) / 2);
            });

            stage.setAlwaysOnTop(true);
        }
    }
}
