package ORM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Parser {

    static final Logger parserLogger = LogManager.getLogger("Parser Logger");

    public static String parseType(String type) {
        try {
            return switch (type) {
                case "int" -> "int";
                case "java.lang.String" -> "varchar(255)";
                case "java.time.LocalDate" -> "date";
                default -> throw new Exception("Exception while parsing type to dB type - type not recognized.");
            };
        } catch (Exception e) {
            parserLogger.error(e);
        }
        return null;    // should not be returned because of default in switch case - maybe refactor to Exception method throw instead of try/catch
    }
}
