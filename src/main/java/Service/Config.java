package Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class for database connection data config loading.
 */
public class Config {
    /**
     * Config logger
     */
    final Logger configLogger = LogManager.getLogger("Config");
    /**
     * Config properties.
     */
    private Properties properties = new Properties();
    /**
     * Config for singleton.
     */
    private static Config config = new Config();
    /**
     * input stream for config file reading.
     */
    InputStream in;

    /**
     * Singleton constructor of config class.
     */
    public Config() {
        try{
            in = getClass().getClassLoader().getResourceAsStream("config.properties");
            if(in != null) {
                properties.load(in);
                in.close();
            }
        } catch (IOException ioe) {
            configLogger.error(ioe.getMessage());
        }
    }

    /**
     * Get the config singleton instance.
     *
     * @return the config instance.
     */
    public static Config getInstance() {
        return config;
    }

    /**
     * Gets config properties.
     *
     * @return config properties.
     */
    public Properties getProperties() { return properties; }
}