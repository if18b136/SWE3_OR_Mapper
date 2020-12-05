package ORM;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.time.LocalDate;


/**
 * <h2>static MetaData class</h2>
 * class for all kinds of metadata extraction, handling, manipulation, etc... .
 */
public final class MetaData {
    static final Logger metaDataLogger = LogManager.getLogger("MetaData");

    public static String getAnnotationTableName(Class<?> c) {
        try{
            Table annotation = c.getAnnotation(Table.class);
            if(annotation != null) {
                return annotation.name();
            } else {
                throw new IllegalArgumentException("The chosen class has no Table Annotation.");
            }
        } catch (IllegalArgumentException iae) {
            metaDataLogger.error(iae);
        }
        return null;    // not reachable - method either returns String or throws an Exception
    }

    public static boolean isPrimary(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).primary()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAutoIncrement(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).autoIncrement()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isForeign(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof ForeignKey) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIgnore(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).ignore()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNullable(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof Column && ((Column) annotation).nullable()) {
                return true;
            }
        }
        return false;
    }

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

    /**
     * <h4>Method buildTableName</h4>
     * Little helper function to get normed table names.
     * @param name Simple name of class without @Table Annotation or empty tableName
     * @return String construct of "t_" + class name in lower case.
     */
    public static String buildTableName(String name) {
        return "t_" + name.toLowerCase();
    }


    /**
     * <h4>Method toColumnType</h4>
     * traverses custom Classes for foreign key types and converts LocalDate to SQL.Date.
     * @param field the field as argument for entity access.
     * @param object the field as object for value returning.
     * @return Either a new object with a "simple" type, a SQL.Date object or the original object if it was simple enough.
     */
    public static Object toColumnType(ORM.Base.Field field, Object object) {
        if (field.isForeign() && !field.isPrimary() && object != null) {  //if it is primary, it can not be a custom class - TODO check that
            System.out.println("toColumnType - " + field.getColumnName() + " : " + object.toString());
            ORM.Base.Field foreignField = Manager.getEntity(object).getPrimaryFields()[0];  //TODO why access only the first pk?
            return MetaData.toColumnType(foreignField, foreignField.getValue(object));      //recursive call to get to the root and access a non-custom Object (eg. int instead of Teacher)
        }
        if (field.getFieldType().equals(LocalDate.class)) {     // the date class used in the framework
            return field.getValue(object);
        }
        // TODO add boolean conversion if needed

        return object;
    }

    public static Object toFieldType(ORM.Base.Field field, Object object) {
        // TODO add other field type conversions
        if (field.getFieldType().equals(LocalDate.class)) {
            return LocalDate.parse(object.toString());
        }
        return object;
    }
}
