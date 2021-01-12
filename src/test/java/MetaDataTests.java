import Entities.Course;
import Entities.Student;
import Entities.Teacher;
import Entities.Testing;
import ORM.Base.Entity;
import ORM.Manager;
import ORM.MetaData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MetaDataTests {

    @Test
    public void metadataGetAnnotationTableNameTest() {
        Assertions.assertEquals(MetaData.getAnnotationTableName(Testing.class), "t_Test");
    }

    @Test
    public void metadataAnnotationTableNameSameAsEntityTableNameTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertEquals(entity.getTableName(),MetaData.getAnnotationTableName(Testing.class));
    }

    @Test
    public void metadataIsPrimaryTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertTrue(MetaData.isPrimary(entity.getPrimaryFields()[0].getField()));
    }

    @Test
    public void metadataIsAutoIncrementTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertFalse(MetaData.isAutoIncrement(entity.getFields()[1].getField()));
    }

    @Test
    public void metadataIsNullableTest() {
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        Entity entity = Manager.getEntity(test);
        Assertions.assertFalse(MetaData.isNullable(entity.getPrimaryFields()[0].getField()));
        Assertions.assertTrue(MetaData.isNullable(entity.getFields()[1].getField()));
    }

    @Test
    public void metadataGetForeignColumnTest() {
        Teacher teacher = new Teacher(1,2500.60);
        Entity teacherEntity = Manager.getEntity(teacher);
        Assertions.assertEquals(MetaData.getForeignColumn(teacherEntity.getPrimaryFields()[0].getField()),"id");
    }

    @Test
    public void metadataGetForeignTableTest() {
        Teacher teacher = new Teacher(1,2500.60);
        Entity teacherEntity = Manager.getEntity(teacher);
        Assertions.assertEquals(MetaData.getForeignTable(teacherEntity.getPrimaryFields()[0].getField()),"t_person");
    }

    @Test
    public void metadataGetManyTableTest() {
        Student student = new Student(1);
        Entity entity = Manager.getEntity(student);
        Assertions.assertEquals(MetaData.getManyTable(entity.getManyFields()[0].getField()), "t_student_course");
    }

    @Test
    public void metadataGetManyClassTest() {
        Student student = new Student(1);
        Entity entity = Manager.getEntity(student);
        Assertions.assertEquals(MetaData.getManyClass(entity.getManyFields()[0].getField()), Course.class);
    }

    @Test
    public void metadataBuildTableNameTest() {
        Assertions.assertEquals(MetaData.buildTableName("test"), "t_test");
    }
}
