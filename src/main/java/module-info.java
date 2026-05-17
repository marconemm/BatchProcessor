module batch.processor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.apache.logging.log4j;
    requires org.slf4j;
    requires tools.jackson.databind;
    requires org.apache.poi.ooxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.jspecify;

    exports br.com.getronics;
    exports br.com.getronics.core;

    opens br.com.getronics to javafx.fxml, javafx.graphics, tools.jackson.databind;
    opens br.com.getronics.core to javafx.fxml, javafx.graphics, tools.jackson.databind;
}