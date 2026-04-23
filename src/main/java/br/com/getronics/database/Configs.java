package br.com.getronics.database;

import br.com.getronics.utils.enums.E_Project;
import javafx.scene.control.Alert;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Configs {
    final ObjectMapper mapper;
    private final String CONFIG_FILE;
    private final Map<String, String> originalProperties = new HashMap<>();
    private final String[] securityKeys = {
            "jdk.xml.maxGeneralEntitySizeLimit",
            "jdk.xml.totalEntitySizeLimit",
            "jdk.xml.entityExpansionLimit",
            "jdk.xml.maxParameterEntitySizeLimit",
            "jdk.xml.maxElementDepth"
    };
    private String lastWorkBooksDir, lastOutPutDir, lastFileName;

    public Configs() {
        final String CONFIG_DIR = Paths.get(System.getProperty("user.home"),
                "." + E_Project.PROJECT_NAME.getValue()).toString();

        CONFIG_FILE = Paths.get(CONFIG_DIR,
                "." + E_Project.PROJECT_NAME.getValue() + "_config.json").toString();
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
            originalProperties.put(key, System.getProperty(key)); // Backup
            System.setProperty(key, "0"); // Define as 0 (unlimited)
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

    public void update(final Configs updatedConfigs) {
        final File configFile = new File(CONFIG_FILE);

        setLastWorkBooksDir(updatedConfigs.getLastWorkBooksDir());
        setLastOutputDir(updatedConfigs.getLastOutPutDir());
        setLastFileName(updatedConfigs.getLastOutputFile());

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
                final Configs savedConfig = mapper.readValue(configFile, Configs.class);

                update(savedConfig);
            }
        } catch (IOException ioe) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);

            getLogger().error("loadConfig: {}", ioe.getLocalizedMessage());
            alert.setTitle("Erro");
            alert.setHeaderText(ioe.getLocalizedMessage());
            alert.show();
        }
    }
}
