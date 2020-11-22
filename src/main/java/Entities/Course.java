package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

@Table(name = "t_course")
public class Course {
    @Column(primary = true)
    private int id;
    @Column(nullable = false, length = 50)
    private String courseName;
    @Column(nullable = false)
    private Double ects;
    @Column(nullable = false)
    @ForeignKey(table = "t_teacher", column = "id")
    private Teacher teacher;

    public Course() {}

    public Course(int id, String courseName, Double ects, Teacher teacher) {
        this.id = id;
        this.courseName = courseName;
        this.ects = ects;
        this.teacher = teacher;
    }

    public Course(String courseName, Double ects, Teacher teacher) {
        this.id = -1;
        this.courseName = courseName;
        this.ects = ects;
        this.teacher = teacher;
    }

    //TODO - Course: add getter and setter if needed
    public String getCourseName() { return this.courseName; }
}
