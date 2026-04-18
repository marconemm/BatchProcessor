package br.com.getronics.Model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.function.Consumer;

import static br.com.getronics.utils.enums.styles.E_Colors.ERROR_RED;
import static br.com.getronics.utils.enums.styles.E_Colors.NEUTRAL_MEDIUM;

public class WorkbookItem {
    private final File file;
    private final HBox container;
    private final Consumer<WorkbookItem> onRemove;

    public WorkbookItem(File file, Consumer<WorkbookItem> onRemove) {
        this.file = file;
        this.onRemove = onRemove;
        this.container = criarLayout();
    }

    private HBox criarLayout() {
        final HBox hbox = new HBox(10);
        final Label lblFileName = new Label(file.getName());
        final Tooltip tooltip = new Tooltip(file.getAbsolutePath());
        final Button btnDelete = new Button();
        final FontIcon trashIcon;

        //Icon:
        trashIcon = new FontIcon("fas-trash-alt");
        trashIcon.setIconSize(18);
        trashIcon.setIconColor(NEUTRAL_MEDIUM.getColor());

        // Hbox:
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMinHeight(20);
        hbox.getStyleClass().add("workbook_item");
        hbox.setOnMouseEntered(e -> {
            hbox.setStyle("-fx-background-color: -fx-color-primary-main");
            lblFileName.setStyle("-fx-text-fill: -fx-color-primary-accent;");
        });

        hbox.setOnMouseExited(e -> {
            hbox.setStyle("-fx-background-color: transparent;");
            lblFileName.setStyle("-fx-text-fill: -fx-color-primary-deep;");
        });

        // Label:
        lblFileName.getStyleClass().add("workbook_item-label");
        HBox.setHgrow(lblFileName, Priority.ALWAYS);
        lblFileName.setMaxWidth(Double.MAX_VALUE);
        lblFileName.setMaxHeight(Double.MAX_VALUE);

        // Tooltip:
        tooltip.setShowDelay(Duration.millis(200));
        lblFileName.setTooltip(tooltip);

        // Button:
        btnDelete.setCursor(Cursor.HAND);
        btnDelete.setPadding(new Insets(5));
        btnDelete.setGraphic(trashIcon);
        btnDelete.setGraphicTextGap(5);
        btnDelete.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        btnDelete.setOnAction(e -> onRemove.accept(this));
        btnDelete.setOnMouseEntered(e -> trashIcon.setIconColor(ERROR_RED.getColor()));
        btnDelete.setOnMouseExited(e -> trashIcon.setIconColor(NEUTRAL_MEDIUM.getColor()));

        hbox.getChildren().addAll(lblFileName, btnDelete);

        return hbox;
    }

    public File getFile() {
        return file;
    }

    public HBox getContainer() {
        return container;
    }
}
