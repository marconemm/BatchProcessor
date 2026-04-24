package br.com.getronics.database;

import br.com.getronics.models.views.BatchProcessorAlert;
import br.com.getronics.utils.BatchProcessorException;
import br.com.getronics.utils.enums.E_Project;
import javafx.scene.control.Alert;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.IOUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

public class UserConfigs {
    final ObjectMapper mapper;

    final String separator;
    private final String CONFIG_FILE;
    private final Map<String, String> originalProperties = new HashMap<>();
    private final String[] securityKeys = {
            "jdk.xml.maxGeneralEntitySizeLimit",
            "jdk.xml.totalEntitySizeLimit",
            "jdk.xml.entityExpansionLimit",
            "jdk.xml.maxParameterEntitySizeLimit",
            "jdk.xml.maxElementDepth",
            "IOUtils.ByteArrayMaxOverride",
            "ZipSecureFile.MinInflateRatio"
    };

    private String lastWorkBooksDir, lastOutPutDir, lastFileName;

    public UserConfigs() {
        final String CONFIG_DIR = Paths.get(System.getProperty("user.home"),
                "." + E_Project.PROJECT_NAME.getValue()).toString();
        final String os = System.getProperty("os.name").toLowerCase();

        CONFIG_FILE = Paths.get(CONFIG_DIR,
                "." + E_Project.PROJECT_NAME.getValue() + "_config.json").toString();
        separator = os.contains("win") ? "\\" : "/";
        mapper = new ObjectMapper();
    }

    public String getLastWorkBooksDir() {
        if (lastWorkBooksDir == null)
            return System.getProperty("user.home");

        return lastWorkBooksDir;
    }

    public void setLastWorkBooksDir(final String lastWorkBooksDir) {
        if (lastWorkBooksDir != null) {
            this.lastWorkBooksDir = lastWorkBooksDir;
        }
    }

    public String getLastOutPutDir() {
        if (lastOutPutDir == null)
            return "<Selecionar Pasta>";

        return lastOutPutDir;
    }

    public void setLastOutputDir(final String lastOutPutDir) {
        if (lastOutPutDir != null) {
            this.lastOutPutDir = lastOutPutDir;
        }
    }

    public String getInitialFileName() {
        if (lastFileName == null) {
            final LocalDateTime now = LocalDateTime.now();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

            return String.format("%s_%s_lotes.txt", E_Project.PROJECT_NAME.getValue(), now.format(formatter));
        }

        return lastFileName;
    }

    public void setLastFileName(String lastFileName) {
        if (!lastFileName.toLowerCase().endsWith(".txt")) {
            lastFileName += ".txt";
        }

        this.lastFileName = lastFileName;
    }

    public void setProperties() {
        for (final String key : securityKeys) {
            // 1. Backup:
            if (key.equals("IOUtils.ByteArrayMaxOverride"))
                originalProperties.put(key, String.valueOf(IOUtils.getByteArrayMaxOverride()));
            else if (key.equals("ZipSecureFile.MinInflateRatio"))
                originalProperties.put(key, String.valueOf(ZipSecureFile.getMinInflateRatio()));
            else
                originalProperties.put(key, System.getProperty(key));

            // 2. Define as 0 (unlimited):
            System.setProperty(key, "0");

            // 3. Extra protection to prevent Apache POI from crashing on large files:
            IOUtils.setByteArrayMaxOverride(5_000_000); // 5MB, if necessary

            // 4. Avoids "Zip Bomb" error if spreadsheet is too dense:
            ZipSecureFile.setMinInflateRatio(0.005);
        }

        update();
    }

    public void unsetProperties() {
        originalProperties.forEach((key, value) -> {
            if (value == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, value);
            }
        });
        originalProperties.clear();
        update();
    }

    public void update(final UserConfigs updatedUserConfigs) {
        final File configFile = new File(CONFIG_FILE);

        setLastWorkBooksDir(updatedUserConfigs.getLastWorkBooksDir());
        setLastOutputDir(updatedUserConfigs.getLastOutPutDir());
        setLastFileName(updatedUserConfigs.getLastOutputFile());

        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, this);
    }

    public void update() {
        final File configFile = new File(CONFIG_FILE);

        setLastWorkBooksDir(getLastWorkBooksDir());
        setLastOutputDir(getLastOutPutDir());
        setLastFileName(getLastOutputFile());

        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, this);
    }

    public String getLastOutputFile() {
        final String os = System.getProperty("os.name").toLowerCase();
        final String separator = os.contains("win") ? "\\" : "/";

        return String.format("%s%s%s", getLastOutPutDir(), separator, getInitialFileName());
    }

    public void fetchData() {
        try {
            final File configFile = new File(CONFIG_FILE);
            final File parentFile = configFile.getParentFile();

            if (!parentFile.exists()) {
                // mkdirs with 's' to create all the configFile tree:
                if (!parentFile.mkdirs()) {
                    throw new IOException(String.format("Não foi possível criar a \"%s\"", parentFile.getPath()));
                }
            }

            if (configFile.exists()) {
                try {
                    final UserConfigs savedConfig = mapper.readValue(configFile, UserConfigs.class);
                    update(savedConfig);
                } catch (JacksonException e) {
                    final String errMsg = String.format("Erro na leitura do arquivo \"%s\".\n" +
                            "Por favor, apague o arquivo acima e reinicie a aplicação.", CONFIG_FILE);

                    throw new BatchProcessorException(errMsg);
                }
            } else {
                update();
            }
        } catch (Exception e) {
            final BatchProcessorAlert alert = new BatchProcessorAlert(Alert.AlertType.ERROR);

            alert.setAlwaysOnTop(true);
            getLogger().error("loadConfig: {}", e.getLocalizedMessage());
            alert.setTitle(E_Project.PROJECT_NAME.getValue() + " - Erro:");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.show();
        }
    }
}
