package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

import java.util.List;

@Table(name = "t_teacher")
public class Teacher extends Person{
    @Column(primary = true)
    @ForeignKey(table = "t_person", column = "id")
    private int id;

    @Column(ignore = true)
    private List<Course> teachingCourses;

    public Teacher() {}
    public Teacher(int id) { this.id = id; }
    public Teacher(int id, List<Course> teachingCourses) {
        this.id = id;
        this.teachingCourses = teachingCourses;
    }


    //TODO - Teacher: add getter and setter if needed

    public void addTeachingCourse(Course course) {
        if (!teachingCourses.contains(course)) {
            teachingCourses.add(course);
        }
    }

    public List<Course> getTeachingCourses() { return this.teachingCourses; }
}
