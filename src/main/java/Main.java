import br.com.getronics.utils.enums.views.E_Fxml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Main extends Application {

    @Override
    public void start(final Stage mainStage) throws IOException {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(E_Fxml.WRAPPER.getFilename()));
            final Parent root = loader.load();
            final Scene scene = new Scene(root);
            final String cssPath = Objects.requireNonNull(
                    getClass().getResource("/styles/index.css")
            ).toExternalForm();

            scene.getStylesheets().add(cssPath);
            mainStage.setTitle("Gerador De Lançamentos Em Lote");
            mainStage.setScene(scene);
            mainStage.setMinWidth(960);
            mainStage.setMinHeight(540);
            mainStage.show();
        } catch (IOException ioe) {
            getLogger().error("start: {}", ioe.getLocalizedMessage());
            throw ioe;
        }
    }
}
