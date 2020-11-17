package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

import java.util.List;

@Table(name = "t_student", superclass = "t_person")
public class Student extends Person{
    @Column(primary = true, autoIncrement = true)
    private int id;

    @Column(nullable = false)
    @ForeignKey(table = "t_person", foreignColumn = "id")
    private int personId;

    @Column
    @ForeignKey(table = "t_student_courses", column = "student_id", foreignColumn = "course_id")
    private List<Course> courses;

    public Student() {}

    public Student(int id, int personId, List<Course> courses) {
        this.id = id;
        this.personId = personId;
        this.courses = courses;
    }

    public Student(int personId, List<Course> courses) {
        this.personId = personId;
        this.courses = courses;
    }

    public void addCourse(Course course) {
        if (!this.courses.contains(course)) {
            this.courses.add(course);
        }
    }
}
