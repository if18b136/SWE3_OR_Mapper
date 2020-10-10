package ORM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Parser {

    static final Logger parserLogger = LogManager.getLogger("Parser Logger");

    public static String parseType(String type) {
        try {
            switch (type) {
                case "int":
                    return "int";
                case "java.lang.String":
                    return "varchar(255)";
                case "java.time.LocalDate":
                    return "date";
                default:
                    throw new Exception("Exception while parsing type to dB type - type not recognized.");
            }
        } catch (Exception e) {
            parserLogger.error(e);
        }
        return null;    // should not be returned because of default in switch case - maybe refactor to Exception method throw instead of try/catch
    }
}
