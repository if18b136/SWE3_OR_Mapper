package ORM;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.MtoN;
import ORM.Annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.time.LocalDate;


/**
 * Class for all kinds of metadata extraction, handling, manipulation, etc... .
 */
public final class MetaData {
    /**
     * Metadata logger
     */
    static final Logger metaDataLogger = LogManager.getLogger("MetaData");

    /**
     * Retrieves the annotated database table name of a class, if set.
     * If no table name annotation set, throws an exception.
     *
     * @param type  Class that will be searched for a table name annotation.
     * @return  Either the table name as String or throws an exception (null theoretically not reachable).
     */
    public static String getAnnotationTableName(Class<?> type) {
        try{
            Table annotation = type.getAnnotation(Table.class);
            if(annotation != null) {
                return annotation.name();
            } else {
                throw new IllegalArgumentException("The chosen class has no Table Annotation.");
            }
        } catch (IllegalArgumentException iae) {
            metaDataLogger.error(iae);
            iae.printStackTrace();
        }
        return null;    // not reachable - method either returns String or throws an Exception
    }

    /**
     * Checks a reflection field for the custom primary key annotation.
     *
     * @param field Java reflection field.
     * @return      True or false, depending on if annotation is set.
     */
    public static boolean isPrimary(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).primary()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a reflection field for the custom auto increment annotation.
     *
     * @param field Java reflection field.
     * @return      True or false, depending on if annotation is set.
     */
    public static boolean isAutoIncrement(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).autoIncrement()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a reflection field for the custom foreign key annotation.
     *
     * @param field Java reflection field.
     * @return      True or false, depending on if annotation is set.
     */
    public static boolean isForeign(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof ForeignKey) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a reflection field for the custom foreign key annotation and if the foreignColumn attribute is set.
     * if yes it is a m:n relationship in the DB.
     * @param field     Java reflection field.
     * @return          True if m:n, else false.
     */
    public static boolean isManyToMany(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof MtoN) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a reflection field for the custom ignore annotation.
     *
     * @param field Java reflection field.
     * @return      True or false, depending on if annotation is set.
     */
    public static boolean isIgnore(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).ignore()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a reflection field for the custom nullable annotation.
     *
     * @param field Java reflection field.
     * @return      True or false, depending on if annotation is set.
     */
    public static boolean isNullable(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).nullable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the annotated database foreign column name of a class, if set.
     * If no column name annotation set, throws an exception.
     *
     * @param field Java reflection field.
     * @return      Annotated foreign column name as String.
     */
    public static String getForeignColumn(Field field) {
        try{
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof ForeignKey) {
                    return ((ForeignKey) annotation).column();
                }
            }
            throw new Exception("no Foreign Key Annotation set for Field: " + field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the annotated database foreign table name of a class, if set.
     * If no table name annotation set, throws an exception.
     *
     * @param field Java reflection field.
     * @return      Annotated foreign table name as String.
     */
    public static String getForeignTable(Field field) {
        try{
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof ForeignKey) {
                    return ((ForeignKey) annotation).table();
                }
            }
            throw new Exception("no Foreign Key Annotation set for Field: " + field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getManyTable(Field field) {
        try{
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof MtoN) {
                    return ((MtoN) annotation).table();
                }
            }
            throw new Exception("no m:n Annotation set for Field: " + field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getManyClass(Field field) {
        try{
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof MtoN) {
                    return ((MtoN) annotation).correspondingClass();
                }
            }
            throw new Exception("no m:n Annotation set for Field: " + field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Little helper function to get normed table names.
     *
     * @param name Simple name of class without @Table Annotation or empty tableName
     * @return String construct of "t_" + class name in lower case.
     */
    public static String buildTableName(String name) {
        return "t_" + name.toLowerCase();
    }


    /**
     * traverses custom Classes for foreign key types and converts LocalDate to SQL.Date.
     *
     * @param field the field as argument for entity access.
     * @param object the field as object for value returning.
     * @return Either a new object with a "simple" type, a SQL.Date object or the original object if it was simple enough.
     */
    public static Object toColumnType(ORM.Base.Field field, Object object) {
        if (field.isForeign() && !field.isPrimary() && object != null) {  //if it is primary, it can not be a custom class - TODO check that
            ORM.Base.Field foreignField = Manager.getEntity(object).getPrimaryFields()[0];  //TODO why access only the first pk?
            return MetaData.toColumnType(foreignField, foreignField.getValue(object));      //recursive call to get to the root and access a non-custom Object (eg. int instead of Teacher)
        }
        if (field.getFieldType().equals(LocalDate.class)) {     // the date class used in the framework
            return field.getValue(object);
        }
        // TODO add boolean conversion if needed

        return object;
    }

    /**
     * Converts <code>java.sql.Date</code> to <code>java.time.LocalDate</code> format.
     *
     * @param field     Field that will be checked if local field needs conversion.
     * @param object    Value as object.
     * @return  Converted or original object.
     */
    public static Object toFieldType(ORM.Base.Field field, Object object) {
        // TODO add other field type conversions
        if (field.getFieldType().equals(LocalDate.class)) {
            return LocalDate.parse(object.toString());
        }
        return object;
    }

    //TODO retire after refactoring - bad way to convert because every single var needs an own conversion and special case handling for extras.

    /**
     * Type parser for different java types to Database types.
     * Used in order to convert different java values types into strings of mysql db-conform types.
     * Currently only supports a small amount of types and would need to be expanded.
     *
     * @param type      Java type as string.
     * @param length    Optional length if set in custom class.
     * @return  Database var type as String.
     */
    public static String parseType(String type, int length) {
        try {
            return switch (type) {
                case "int" -> "int";
                case "java.lang.String" -> "varchar(" + length + ")";
                case "java.lang.Double", "double" -> "double";
                case "java.time.LocalDate" -> "date";
                default -> throw new Exception("Exception while parsing type to dB type - type not recognized. " + type);
            };
        } catch (Exception e) {
            metaDataLogger.error(e);
        }
        return null;    // should not be returned because of default in switch case - maybe refactor to Exception method throw instead of try/catch
    }
}
