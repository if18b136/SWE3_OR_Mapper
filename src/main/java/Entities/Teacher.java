package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom teacher class for ORM framework testing.
 */
@Table(name = "t_teacher")
public class Teacher extends Person{
    @Column(primary = true)
    @ForeignKey(table = "t_person", column = "id")
    private int id;

    @Column
    private double salary;

    @Column(ignore = true)
    private List<Course> teachingCourses;

    public Teacher() {}
    public Teacher(int id, double salary) {
        this.id = id;
        this.salary = salary;
    }
    public Teacher(int id, double salary, List<Course> teachingCourses) {
        this.id = id;
        this.salary = salary;
        this.teachingCourses = teachingCourses;
    }


    //TODO - Teacher: add getter and setter if needed

    public void addTeachingCourse(Course course) {
        if (!teachingCourses.contains(course)) {
            teachingCourses.add(course);
        }
    }

    public List<Course> getTeachingCourses() { return this.teachingCourses; }

    public double getSalary() { return this.salary; }
    public void setSalary(Double salary) { this.salary = salary; }
}
