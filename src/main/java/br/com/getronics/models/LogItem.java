package br.com.getronics.models;

import br.com.getronics.utils.enums.E_LogType;
import br.com.getronics.utils.enums.styles.E_Colors;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

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
        final Label lblHora = new Label("[" + time + "]:");
        final Label lblMsg = new Label(mensagem);
        final String FONT_FAMILY = "Mark Pro";

        hbox.setMaxWidth(Double.MAX_VALUE);

        // 1-) Setup the mensage:
        lblHora.setTextFill(E_Colors.PRIMARY_DEEP.getColor());
        lblHora.setFont(Font.font(FONT_FAMILY, FontWeight.MEDIUM, 12));

        lblMsg.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 14));
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(650);

        // 3. Styling:
        switch (logType) {
            case SUCCESS -> {
                lblMsg.setTextFill(E_Colors.PRIMARY_MAIN.getColor());
                lblMsg.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 15));
            }
            case ERROR -> {
                lblMsg.setTextFill(E_Colors.ERROR_RED.getColor());
                lblMsg.setFont(Font.font(FONT_FAMILY, FontWeight.LIGHT, 13));
            }
            case INFO -> {
                lblMsg.setTextFill(E_Colors.NEUTRAL_MEDIUM.getColor());
                lblMsg.setFont(Font.font(FONT_FAMILY, FontWeight.THIN, FontPosture.ITALIC, 12));
            }
            default -> throw new IllegalArgumentException("Favor, informar o tipo da mensagem de log [E_LogType]");
        }

        hbox.getChildren().addAll(lblHora, lblMsg);

        // Hover effect:
        hbox.setOnMouseEntered(_ -> hbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);"));
        hbox.setOnMouseExited(_ -> hbox.setStyle("-fx-background-color: transparent;"));

        return hbox;
    }

    public HBox getContainer() {
        return container;
    }
}