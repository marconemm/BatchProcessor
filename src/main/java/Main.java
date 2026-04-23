import br.com.getronics.controllers.BaseController;
import br.com.getronics.controllers.WrapperController;
import br.com.getronics.interfaces.Shutdownable;
import br.com.getronics.utils.enums.views.E_Fxml;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Main extends Application {
    final Rectangle2D screenBounds;
    final double minWidth;
    final double minHeight;
    final double sizeScale;

    public Main() {
        screenBounds = Screen.getPrimary().getBounds();
        sizeScale = 0.7;
        minWidth = screenBounds.getWidth() * sizeScale;
        minHeight = screenBounds.getHeight() * sizeScale;
    }

    @Override
    public void start(final Stage mainStage) throws IOException {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(E_Fxml.WRAPPER.getFilename()));
            final Parent root = loader.load();
            final Scene scene = new Scene(root);

            String cssPath = Objects.requireNonNull(
                    getClass().getResource("/styles/index.css")
            ).toExternalForm();
            scene.getStylesheets().add(cssPath);

            cssPath = Objects.requireNonNull(
                    getClass().getResource("/styles/home.css")
            ).toExternalForm();
            scene.getStylesheets().add(cssPath);

            mainStage.setTitle("Gerador De Lançamentos Em Lote");
            mainStage.setScene(scene);
            mainStage.setMinWidth(minWidth);
            mainStage.setMinHeight(minHeight);
            mainStage.setOnCloseRequest(wEvent -> {
                final BaseController baseController = new BaseController();
                final boolean isToClose = baseController.isToCloseApplication();

                if (isToClose) {
                    final WrapperController wrapperController = loader.getController();
                    final Shutdownable content = wrapperController.getCurrentController();

                    getLogger().info("start(): fechando a aplicação...");
                    baseController.stopApplication(content);
                    Platform.exit();
                    System.exit(0);
                } else {
                    // 1-) Stops the window event propagation:
                    wEvent.consume();
                }
            });
            mainStage.show();
        } catch (IOException ioe) {
            getLogger().error("start: {}", ioe.getLocalizedMessage());
            throw ioe;
        }
    }
}
