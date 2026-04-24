package br.com.getronics.models.views;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
        this.setOnShowing(_ -> {
            final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            this.setX((screenBounds.getWidth() - 600) / 2);
            this.setY((screenBounds.getHeight() - 300) / 2);
        });
    }

    public void setAlwaysOnTop(boolean b) {
        final Stage stage = (Stage) this.getDialogPane().getScene().getWindow();

        stage.setAlwaysOnTop(b);
    }
}
