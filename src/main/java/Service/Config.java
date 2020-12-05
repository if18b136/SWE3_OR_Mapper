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
    final Logger configLogger = LogManager.getLogger("Config");
    private Properties properties = new Properties();
    private static Config config = new Config();
    InputStream in;

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

    public static Config getInstance() {
        return config;
    }
    public Properties getProperties() { return properties; }

}