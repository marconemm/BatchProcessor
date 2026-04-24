package br.com.getronics.models.views;

import br.com.getronics.utils.BatchProcessorException;
import br.com.getronics.utils.enums.E_LogType;
import br.com.getronics.utils.enums.styles.E_Colors;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogItem {

    private final HBox container;
    private final String mensagem;
    private final E_LogType logType;

    public LogItem(String msg, E_LogType logType) {
        this.mensagem = msg;
        this.logType = logType;
        this.container = createLayout();
    }

    private HBox createLayout() {
        final HBox hbox = new HBox();
        final String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        final Label lblHora = new Label("[" + time + "]: ");
        final Label lblMsg = new Label(" - " + mensagem);
        final FontIcon icon = new FontIcon("fas-check-square");

        hbox.setMaxWidth(Double.MAX_VALUE);
        hbox.getStyleClass().add("log-item-container");
        VBox.setMargin(hbox, new Insets(0, 0, 5, 0));

        // 1-) Setup the mensage:
        lblHora.getStyleClass().add("log-label-time");
        lblMsg.setMaxWidth(hbox.getMaxWidth());
        HBox.setHgrow(lblMsg, Priority.ALWAYS);

        // 3. Styling:
        switch (logType) {
            case SUCCESS -> {
                hbox.getStyleClass().add("log-success");
                lblMsg.getStyleClass().add("log-success");
                icon.setIconColor(Paint.valueOf(E_Colors.PRIMARY_MAIN.getHex()));
            }
            case ERROR -> {
                hbox.getStyleClass().add("log-erro");
                lblMsg.getStyleClass().add("log-erro");
                icon.setIconLiteral("fas-exclamation-circle");
                icon.setIconColor(Paint.valueOf(E_Colors.ERROR_RED.getHex()));
            }
            case INFO -> {
                hbox.getStyleClass().add("log-info");
                lblMsg.getStyleClass().add("log-info");
                icon.setIconLiteral("fas-info-circle");
                icon.setIconColor(Paint.valueOf(E_Colors.NEUTRAL_MEDIUM.getHex()));
            }
            default -> throw new BatchProcessorException("Favor, informar o tipo da mensagem de log [E_LogType]");
        }

        hbox.getChildren().addAll(lblHora, icon, lblMsg);

        return hbox;
    }

    public HBox getContainer() {
        return container;
    }
}