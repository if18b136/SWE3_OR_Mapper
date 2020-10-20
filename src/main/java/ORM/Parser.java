package ORM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public final class Parser {

    static final Logger parserLogger = LogManager.getLogger("Parser Logger");

    public static String parseType(AnnotatedType type) {
        try {
            return switch (type.toString()) {
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

    //TODO check if upper one is needed anymore (at end of project)
    public static String parseType(String type, int length) {
        try {
            return switch (type) {
                case "int" -> "int";
                case "java.lang.String" -> "varchar(" + length + ")";
                case "java.time.LocalDate" -> "date";
                default -> throw new Exception("Exception while parsing type to dB type - type not recognized. " + type);
            };
        } catch (Exception e) {
            parserLogger.error(e);
        }
        return null;    // should not be returned because of default in switch case - maybe refactor to Exception method throw instead of try/catch
    }
}
