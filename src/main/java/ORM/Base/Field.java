package ORM.Base;

import ORM.MetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <h2>Field class </h2>
 *  every database column is represented as a field.
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
    private boolean ignore;
    private boolean nullable;

    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
        field.setAccessible(true);
        this.columnName = field.getName();
        this.primary = MetaData.isPrimary(field);
        this.autoIncrement = MetaData.isAutoIncrement(field);
        this.foreign = MetaData.isForeign(field);
        this._fieldType = field.getType();
        this.ignore = MetaData.isIgnore(field);
        this.nullable = MetaData.isNullable(field);
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

    public java.lang.reflect.Field getField() { return field; }

    public void setForeignColumn(String fkColumn) { this.foreignColumn = fkColumn; }
    public String getForeignColumn() { return this.foreignColumn; }

    public void setForeignTable(String foreignTable) { this.foreignTable = foreignTable; }
    public String getForeignTable() { return foreignTable; }

    public Class<?> getFieldType() { return this._fieldType; }
    public void setFieldType(Class<?> fieldType) { this._fieldType = fieldType; }

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

    public void setValue(Object object, Object value) {
        try {
            this.field.set(object,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getColumnName() { return this.columnName; }

    public boolean isPrimary() { return this.primary; }
    public boolean isAutoIncrement() { return this.autoIncrement; }
    public boolean isForeign() { return this.foreign; }
    public boolean isNullable() { return this.nullable; }
    public boolean ignore() { return this.ignore; }

    public boolean isMtoN () {
        return this.foreignTable.isEmpty();
    }
}