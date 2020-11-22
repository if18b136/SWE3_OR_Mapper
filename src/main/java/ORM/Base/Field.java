package ORM.Base;

import ORM.Manager;
import ORM.MetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

/**<h1>Class </h1>
 *
 */
public class Field {
    static final Logger fieldLogger = LogManager.getLogger("Field Logger");

    private Class _fieldType;
    private Entity entity;
    private java.lang.reflect.Field field;
    private String columnName;
    private boolean primary;
    private boolean autoIncrement;
    private boolean foreign;
    private String foreignTable;
    private String foreignColumn;

    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
        field.setAccessible(true);
        this.columnName = field.getName();
        this.primary = MetaData.isPrimary(field);
        this.autoIncrement = MetaData.isAutoIncrement(field);
        this.foreign = MetaData.isForeign(field);
        this._fieldType = field.getType();
//        if(field.getType().equals(List.class)) {
//            System.out.println("----- " + this.columnName + " -----");
//            System.out.println(field.getType());
//            System.out.println(field.getAnnotatedType().getType());
//            System.out.println(field.getDeclaringClass());
//            System.out.println("--------------------");
//        }
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

    public java.lang.reflect.Field getField() { return field; }

    public void setForeignColumn(String fkColumn) { this.foreignColumn = fkColumn; }
    public String getForeignColumn() { return this.foreignColumn; }

    public void setForeignTable(String foreignTable) { this.foreignTable = foreignTable; }
    public String getForeignTable() { return foreignTable; }

    public Class getFieldType() { return this._fieldType; }
    public void setFieldType(Class fieldType) { this._fieldType = fieldType; }

    // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
    // Alternative is to call for the getter Methods but that would need a little bit more refactoring
    public Object getValue(Object object) {
        try {
            Object value = this.field.get(object);
            if(value != null) {
                return value;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getColumnName() { return this.columnName; }

    public boolean isPrimary() { return this.primary; }
    public boolean isAutoIncrement() { return this.autoIncrement; }
    public boolean isForeign() { return this.foreign; }

    public boolean isMtoN () {
        return this.foreignTable.isEmpty();
    }

    //TODO add sqlTypeParser Methods
}