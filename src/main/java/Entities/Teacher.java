package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

import java.util.List;

@Table(name = "t_teacher", superclass = "t_person")
public class Teacher extends Person{
    @Column(primary = true, autoIncrement = true)
    private int id;

    @Column(nullable = false)
    @ForeignKey(table = "t_person", foreignColumn = "id")
    private int personId;

    @Column
    @ForeignKey(table = "t_course", foreignColumn = "id")
    private List<Course> teachingCourses;

    public Teacher() {}

    public Teacher(int id, int personId, List<Course> teachingCourses) {
        this.id = id;
        this.personId = personId;
        this.teachingCourses = teachingCourses;
    }

    public Teacher(int personId, List<Course> teachingCourses) {
        this.personId = personId;
        this.teachingCourses = teachingCourses;
    }

    public Teacher(int personId) {
        this.personId = personId;
    }

    //TODO - Teacher: add getter and setter if needed

    public void addTeachingCourse(Course course) {
        if (!teachingCourses.contains(course)) {
            teachingCourses.add(course);
        }
    }


}
