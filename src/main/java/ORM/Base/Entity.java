package ORM.Base;

import ORM.MetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Every database table is being represented as a Entity class object within the framework.
 */
public class Entity {
    /**
     * Class of this entity.
     */
    private Class<?> entityClass;
    /**
     * Table name of this entity in the database.
     */
    private String tableName;
    /**
     * Field array of this entity's fields.
     */
    private Field[] fields;
    /**
     * Field array of this entity's primary key fields.
     */
    private Field[] primaryFields;
    /**
     * Field array of this entity's internal (= non-foreign) fields.
     */
    private Field[] internalFields;
    /**
     * Field array of this entity's foreign fields.
     */
    private Field[] externalFields;
    /**
     * Field array of this entity's m:n fields.
     */
    private Field[] manyFields;
    /**
     * Parent class or object.Class if base class.
     */
    private Class<?> superClass;

    /**
     * calls initEntity to initialize the Entity object
     *
     * @param type Class from which the Entity will be from
     */
    public Entity(Class<?> type) {
        initEntity(type);
    }

    /**
     * calls initEntity to initialize the Entity object
     *
     * @param obj Object from which the Entity will be from
     */
    public Entity(Object obj) {
        initEntity(obj.getClass());
    }

    /**
     * Private entity initialization method.
     * Creates fields for this entity's class fields, classifies and stores them for further use.
     * Sets parent class to either <code>type.getSuperclass()</code> or <code>Object.class</code> if base class.
     *
     * @param type  the custom framework class that an entity will be created from.
     */
    private void initEntity(Class<?> type) {
        String tableName = MetaData.getAnnotationTableName(type);
        this.tableName = tableName == null || tableName.equals("") ? MetaData.buildTableName(type.getSimpleName()) : tableName;
        this.entityClass = type;

        List<Field> fieldsList = new ArrayList<>();
        List<Field> primaryFieldsList = new ArrayList<>();
        List<Field> internalFieldsList = new ArrayList<>();
        List<Field> externalFieldsList = new ArrayList<>();
        List<Field> manyFieldsList = new ArrayList<>();

        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for(java.lang.reflect.Field field : fields) {
            Field ormField = new Field(this, field);
            if(!ormField.ignore()) { // the ignore check handles fields like Teacher.Courses, which is not a entry in t_teacher, but a fillable field in teacher Object
                fieldsList.add(ormField);
                if(ormField.isPrimary() && !primaryFieldsList.contains(ormField)) {
                    primaryFieldsList.add(ormField);
                }
                if(ormField.isMtoN()) {     // m:n fields are not really existing in the db - they have their own table
                    manyFieldsList.add(ormField);
                    ormField.setForeignTable(MetaData.getManyTable(field));
                } else {
                    if(ormField.isForeign()) {
                        ormField.setForeignColumn(MetaData.getForeignColumn(field));    //TODO why is this not in field init?
                        ormField.setForeignTable(MetaData.getForeignTable(field));
                        externalFieldsList.add(ormField);
                    } else {
                        internalFieldsList.add(ormField);
                    }
                }
            }
        }

        this.fields = fieldsList.toArray(new Field[0]);
        this.primaryFields = primaryFieldsList.toArray(new Field[0]);
        this.internalFields = internalFieldsList.toArray(new Field[0]);
        this.externalFields = externalFieldsList.toArray(new Field[0]);
        this.manyFields = manyFieldsList.toArray(new Field[0]);
        // get superclass without need of extra annotating them
        this.superClass = type.getSuperclass();
    }

    /**
     * Public getter for this entity's table name.
     *
     * @return  this entity's table name as String.
     */
    public String getTableName() { return this.tableName; }

    /**
     * Public getter for this entity's fields.
     *
     * @return  this entity's fields as field array.
     */
    public Field[] getFields() { return this.fields; }

    /**
     * Public getter for this entity's class.
     *
     * @return  this entity's class.
     */
    public Class<?> getEntityClass() { return this.entityClass; }

    /**
     * Public getter for this entity's internal fields.
     *
     * @return  this entity's internal fields as field array.
     */
    public Field[] getInternalFields() { return this.internalFields; }

    /**
     * Public getter for this entity's primary key fields.
     *
     * @return  this entity's primary key fields as field array.
     */
    public Field[] getPrimaryFields() { return this.primaryFields; }

    /**
     * Public getter for this entity's foreign key fields.
     *
     * @return  this entity's foreign key fields as field array.
     */
    public Field[] getExternalFields() { return this.externalFields; }

    /**
     * Public getter for this entity's m:n fields.
     *
     * @return  this entity's m:n fields as field array.
     */
    public Field[] getManyFields() { return this.manyFields; }

    /**
     * Public getter for this entity's parent class.
     *
     * @return  this entity's parent class or <code>Object.class if base class</code>.
     */
    public Class<?> getSuperClass() { return this.superClass; }
}
