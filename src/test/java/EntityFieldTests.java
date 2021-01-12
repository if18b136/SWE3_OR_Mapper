import Entities.Person;
import Entities.Teacher;
import Entities.Testing;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class EntityFieldTests {

    @Test
    public void getEntityTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
        Entity timmyEntity = Manager.getEntity(timmy);
        Assertions.assertNotNull(timmyEntity);
        Assertions.assertEquals(timmyEntity.getEntityClass(),Person.class);
    }

    @Test
    public void entityFieldTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));     // Person Class Test
        Entity timmyEntity = Manager.getEntity(timmy);
        Assertions.assertNotNull(timmyEntity.getFields());
        Assertions.assertNotNull(timmyEntity.getPrimaryFields());
        Assertions.assertNotNull(timmyEntity.getInternalFields());
        Assertions.assertNotNull(timmyEntity.getExternalFields());
        Assertions.assertNotNull(timmyEntity.getManyFields());
    }

    @Test
    public void fieldSetValueTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        entity.getPrimaryFields()[0].setFieldType(long.class);
        Assertions.assertEquals(Manager.getEntity(test).getPrimaryFields()[0].getFieldType(),long.class);
    }

    @Test
    public void fieldGetEntityTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        for (Field field : entity.getFields()) {
            Assertions.assertEquals(entity,field.getEntity());
        }
    }

    @Test
    public void fieldSetEntityTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Entity entity = Manager.getEntity(test);
        entity.getFields()[0].setEntity(Manager.getEntity(timmy));
        Assertions.assertEquals(Manager.getEntity(timmy),entity.getFields()[0].getEntity());
    }

    @Test
    public void fieldGetForeignColumnTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Teacher teacher = new Teacher(1,2500.60);
        Entity teacherEntity = Manager.getEntity(teacher);
        Assertions.assertEquals(teacherEntity.getExternalFields()[0].getForeignColumn(),Manager.getEntity(timmy).getPrimaryFields()[0].getColumnName());
    }

    @Test
    public void fieldGetForeignTableTest() {
        Person timmy = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Teacher teacher = new Teacher(1,2500.60);
        Entity teacherEntity = Manager.getEntity(teacher);
        Assertions.assertEquals(teacherEntity.getExternalFields()[0].getForeignTable(),Manager.getEntity(timmy).getTableName());
    }

    @Test
    public void fieldIsNullableTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertTrue(entity.getFields()[1].isNullable());
    }

    @Test
    public void fieldPrimaryIsNotNullableTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertFalse(entity.getPrimaryFields()[0].isNullable());
    }

}
