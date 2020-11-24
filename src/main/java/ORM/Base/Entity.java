package ORM.Base;

import ORM.Annotations.Table;
import ORM.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity {
    private Class<?> entityClass;
    private String tableName;
    private Field[] fields;
    private Field[] primaryFields;
    private Field[] internalFields;
    private Field[] externalFields;
    private Class<?> superClass;
    private Class<?>[] subClasses;

    /**<h2>Entity Constructor from class</h2>
     * calls initEntity to initialize the Entity object
     * @param type Class from which the Entity will be from
     */
    public Entity(Class<?> type) {
        initEntity(type);
    }

    /**<h2>Entity Constructor from object</h2>
     * calls initEntity to initialize the Entity object
     * @param obj Object from which the Entity will be from
     */
    public Entity(Object obj) {
        initEntity(obj.getClass());
    }

    private void initEntity(Class<?> type) {
        String tableName = MetaData.getAnnotationTableName(type);
        this.tableName = tableName == null || tableName.equals("") ? MetaData.buildTableName(type.getSimpleName()) : tableName;
        this.entityClass = type;

        List<Field> fieldsList = new ArrayList<>();
        List<Field> primaryFieldsList = new ArrayList<>();
        List<Field> internalFieldsList = new ArrayList<>();
        List<Field> externalFieldsList = new ArrayList<>();

        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for(java.lang.reflect.Field field : fields) {
            Field ormField = new Field(this, field);
            fieldsList.add(ormField);
            //TODO own array for fields that are neither internal nor external?
            if(!ormField.ignore()) { // the ignore check handles fields like Teacher.Courses, which is not a entry in t_teacher, but a fillable field in teacher Object
                if(ormField.isPrimary() && !primaryFieldsList.contains(ormField)) {
                    primaryFieldsList.add(ormField);
                }
                if(ormField.isForeign()) {
                    ormField.setForeignColumn(MetaData.getForeignColumn(field));
                    ormField.setForeignTable(MetaData.getForeignTable(field));
                    externalFieldsList.add(ormField);
                } else {
                    internalFieldsList.add(ormField);
                }
            }
        }

        this.fields = fieldsList.toArray(new Field[0]);
        this.primaryFields = primaryFieldsList.toArray(new Field[0]);
        this.internalFields = internalFieldsList.toArray(new Field[0]);
        this.externalFields = externalFieldsList.toArray(new Field[0]);

        // get superclass without need of extra annotating them
        this.superClass = type.getSuperclass().equals(Object.class) ? null : type.getSuperclass();
    }

    public String getTableName() { return this.tableName; }
    public Field[] getFields() { return this.fields; }
    public Class<?> getEntityClass() { return this.entityClass; }
    public Field[] getInternalFields() { return this.internalFields; }
    public Field[] getPrimaryFields() { return this.primaryFields; }
    public Field[] getExternalFields() { return this.externalFields; }
    public Class<?> getSuperClass() { return this.superClass; }
}
