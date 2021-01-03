package ORM.Base;

import ORM.MetaData;

/**
 *  Every database column gets represented as a field.
 */
public class Field {
    /**
     * This field's class.
     */
    private Class<?> fieldType;
    /**
     * This field's entity
     */
    private Entity entity;
    /**
     * The <code>java.lang.reflect.Field</code> this field is based on.
     */
    private java.lang.reflect.Field field;
    /**
     * This field's column name in the database.
     */
    private String columnName;
    /**
     * Boolean true/false if field has primary key attribute in database.
     */
    private boolean primary;
    /**
     * Boolean true/false if field has auto increment attribute in database.
     */
    private boolean autoIncrement;
    /**
     * Boolean true/false if field has foreign key attribute in database.
     */
    private boolean foreign;
    /**
     * Boolean true/false if field has a m:n relation in database.
     */
    private boolean mn;
    /**
     * Name of foreign table if field is external field and thus references another column in database.
     */
    private String foreignTable;
    /**
     * Name of foreign column if field is external field and thus references another column in database.
     */
    private String foreignColumn;
    /**
     * Boolean true/false if field can be ignored for database entry creation.
     * Example: field is referencing a foreign key relation (1:1, 1:n) but does not need a foreign key entry in this table.
     */
    private boolean ignore;
    /**
     * Boolean true/false if field has nullable key attribute in database.
     */
    private boolean nullable;

    /**
     * Public field constructor to instantiate a new field object.
     *
     * @param entity    The entity that this field belongs to.
     * @param field     The <code>java.lang.reflect.Field</code> that this field object is based upon.
     */
    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
        field.setAccessible(true);
        this.columnName = field.getName();
        this.primary = MetaData.isPrimary(field);
        this.autoIncrement = MetaData.isAutoIncrement(field);
        this.foreign = MetaData.isForeign(field);
        this.fieldType = field.getType();
        this.ignore = MetaData.isIgnore(field);
        this.nullable = MetaData.isNullable(field);
        this.mn = MetaData.isManyToMany(field);
    }

    /**
     * Public getter for this field's entity.
     *
     * @return  this fields entity object.
     */
    public Entity getEntity() { return entity; }
    /**
     * Public setter for this fields entity.
     *
     * @param entity    The entity object that this field will be associated with.
     */
    public void setEntity(Entity entity) { this.entity = entity; }

    /**
     * Public <code>java.lang.reflect.Field</code> getter for this field's reflection field.
     *
     * @return this field's reflection field object.
     */
    public java.lang.reflect.Field getField() { return field; }

    /**
     * Public getter for this field's foreign column name in database.
     *
     * @return This field's foreign column name as String.
     */
    public String getForeignColumn() { return this.foreignColumn; }
    /**
     * Public setter for this field's foreign column.
     *
     * @param fkColumn  Database name of the foreign key column.
     */
    public void setForeignColumn(String fkColumn) { this.foreignColumn = fkColumn; }

    /**
     * Public getter for this field's foreign key table name.
     *
     * @return  Database name of the table the foreign key references as String.
     */
    public String getForeignTable() { return foreignTable; }
    /**
     * Public getter for this field's foreign table in database.
     *
     * @param foreignTable  Database name of the table the foreign key references.
     */
    public void setForeignTable(String foreignTable) { this.foreignTable = foreignTable; }

    /**
     * Public getter for this field's class.
     *
     * @return  This field's class.
     */
    public Class<?> getFieldType() { return this.fieldType; }
    /**
     * Public setter for this field's class.
     *
     * @param fieldType Class this field is be set to.
     */
    public void setFieldType(Class<?> fieldType) { this.fieldType = fieldType; }

    //

    /**
     * Get the field value of this certain field of an object.
     * Alternative is to call for the getter Methods but that would need a little bit more refactoring
     *
     * @param object    The object from which this field's value will be extracted.
     * @return          This field's value from the input object as object.
     * @see <a href=https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection>Stackoverflow source</a>
     */
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

    /**
     * Set this field's value of a particular object to the specified input value.
     *
     * @param object    The object which contains this field that will be set to a new value.
     * @param value     The value that the input object's field value will be set to.
     */
    public void setValue(Object object, Object value) {
        try {
            this.field.set(object,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Public getter for this field's database column name.
     *
     * @return  This field's database column name as String.
     */
    public String getColumnName() { return this.columnName; }

    /**
     * Boolean check if this field is annotated as primary key.
     *
     * @return  True if field is annotated as primary key, else false.
     */
    public boolean isPrimary() { return this.primary; }
    /**
     * Boolean check if this field is annotated as auto incremented.
     *
     * @return  True if field is annotated as auto incremented, else false.
     */
    public boolean isAutoIncrement() { return this.autoIncrement; }
    /**
     * Boolean check if this field is annotated as foreign key.
     *
     * @return  True if field is annotated as foreign key, else false.
     */
    public boolean isForeign() { return this.foreign; }
    /**
     * Boolean check if this field has a m:n relation in the DB.
     *
     * @return true if field is m:n, else false.
     */
    public boolean isManyToMany() { return this.mn; }
    /**
     * Boolean check if this field is annotated as nullable.
     *
     * @return  True if field is annotated as nullable, else false.
     */
    public boolean isNullable() { return this.nullable; }
    /**
     * Boolean check if this field can be ignored for database queries.
     *
     * @return  True if field is annotated as ignore, else false.
     */
    public boolean ignore() { return this.ignore; }
    /**
     * Boolean check if this field is annotated as m:n relation.
     *
     * @return  True if field is annotated as m:n relation, else false.
     */
    public boolean isMtoN () { return this.mn; }
}