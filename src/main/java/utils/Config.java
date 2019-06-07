package utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

@Log4j2
public final class Config {
    private static final String PROPERTIES_PATH = "/app.properties";
    private static final Configurations configs = new Configurations();
    private static PropertiesConfiguration config;

    static {
        try {
            initialize();
        } catch (ConfigurationException | NullPointerException e) {
            log.error("Cannot load " + PROPERTIES_PATH, e);
        }
    }

    private static void initialize() throws ConfigurationException {
        log.debug("Loading configuration " + PROPERTIES_PATH);
        final String path = Config.class.getClassLoader().getResource("app.properties").getPath();
        config = configs.properties(new File(path));
        log.debug("Config " + PROPERTIES_PATH + " has been loaded.");
        log.debug("Keys:");
        config.getKeys().forEachRemaining(log::debug);
    }

    public static String getValue(String key) {
        return config.getString(key);
    }

    public static PropertiesConfiguration getInstance() {
        return config;
    }
}
