package br.com.getronics.models.interfaces;

import static org.apache.logging.log4j.LogManager.getLogger;

public interface Shutdownable {
    default void stop() {
        getLogger().warn("stop(): Não deveria passar por aqui!");
    }
}
