module br.com.getronics {
    // Swing and AWT:
    requires java.desktop;

    // Static checking:
    requires org.jspecify;

    // JavaFX:
    requires javafx.controls;
    requires javafx.fxml;

    // Log4j2 e SLF4J:
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.slf4j;
    requires com.lmax.disruptor;

    // Ikonli:
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    // Apache POI:
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    // Jackson v3:
    requires tools.jackson.core;
    requires tools.jackson.databind;

    opens br.com.getronics.core to javafx.graphics, javafx.fxml;
    opens br.com.getronics.controllers to javafx.fxml;
    opens br.com.getronics.views to javafx.fxml;

    exports br.com.getronics.core;
    exports br.com.getronics.controllers;
    exports br.com.getronics.utils.enums.views;
    exports br.com.getronics.interfaces;
    exports br.com.getronics;
    opens br.com.getronics to javafx.fxml, javafx.graphics;
}