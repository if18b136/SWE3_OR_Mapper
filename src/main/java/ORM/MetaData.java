package ORM;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MetaData {
    static final Logger metaDataLogger = LogManager.getLogger("MetaData");

    //TODO - Question: is it smart to declare a class for fieldData key+value?
    public static class fieldData {
        public fieldData(AnnotatedType type, String name, String value) {this.type=type; this.name=name; this.value=value;}
        public fieldData(AnnotatedType type, String name) {this.type=type; this.name=name;}
        public AnnotatedType type;
        public String name;
        public String value;
    }

    // get Fields
    // create String for each field
    // add sql syntax for annotations if needed
    // return Strings
    public static List<String> getAnnotationColumnData(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        List<String> data = new ArrayList<>();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Column) {
                    StringBuilder sql = new StringBuilder();
                    String name = field.getName();
                    String type = Parser.parseType(field.getType().getName(),((Column) annotation).length());
                    String primary = ((Column) annotation).primary() ? "PRIMARY KEY " : "";
                    String autoInc = ((Column) annotation).autoIncrement() ? "AUTO_INCREMENT " : "";
                    String unique = ((Column) annotation).unique() ? "UNIQUE " : "";
                    String nullable = ((Column) annotation).nullable() && ((Column) annotation).primary() ? "" : "NOT NULL";
                    sql.append(name).append(" ").append(type).append(" ")
                            .append(primary).append(autoInc)
                            .append(unique).append(nullable);
                    if (!field.equals(fields[fields.length - 1])) {
                        sql.append(",");
                    }
                    data.add(sql.toString());
                }
            }
        }
        return data;
    }

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


    public static List<fieldData> classMetaData (Class<?> c) {
        Field[] fieldArray = c.getDeclaredFields(); //get all fields as single Array
        List<fieldData> fieldList = new ArrayList<>();           // list from helper class
        for(Field field : fieldArray) {
            field.setAccessible(true);
            fieldList.add(new fieldData(field.getAnnotatedType(), field.getName()));
        }
        return fieldList;
    }

    public static List<fieldData> objectMetaData (Object obj) throws IllegalAccessException {
        Field[] fieldArray = obj.getClass().getDeclaredFields(); //get all fields as single Array
        List<fieldData> fieldList = new ArrayList<>();           // list from helper class
        for(Field field : fieldArray) {
            field.setAccessible(true);
            fieldList.add(new fieldData(field.getAnnotatedType(), field.getName(), field.get(obj).toString()));
        }
        return fieldList;
    }
}
