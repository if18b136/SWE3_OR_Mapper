package ORM.Base;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public class Field {
    private Entity entity;
    private java.lang.reflect.Field field;

    public Field(Entity entity, java.lang.reflect.Field field) {
        this.entity = entity;
        this.field = field;
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

    public AnnotatedType getFieldType () { return field.getAnnotatedType(); } // erased method field fieldType because it can be retrieved from reflection field.
}