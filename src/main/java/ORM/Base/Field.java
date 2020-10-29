package ORM.Base;

import ORM.MetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;

public class Field {
    static final Logger fieldLogger = LogManager.getLogger("Field Logger");

    private Entity entity;
    private java.lang.reflect.Field field;
    private String columnName;
    private boolean primary;
    private boolean autoIncrement;


    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
        field.setAccessible(true);
        this.columnName = field.getName();
        this.primary = MetaData.isPrimary(field);
        this.autoIncrement = MetaData.isAutoIncrement(field);
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

    public AnnotatedType getFieldType () { return field.getAnnotatedType(); } // erased method field fieldType because it can be retrieved from reflection field.

    public Object getValue() {
        try {
            return field.get(this.entity.getObject());
        } catch (IllegalAccessException iae) {
            fieldLogger.error(iae);
        }
       return null; // won't ever be reached
    }

    public String getColumnName() { return this.columnName; }

    public boolean isPrimary() { return this.primary; }
    public boolean isAutoIncrement() { return this.autoIncrement; }
}