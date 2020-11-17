package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

@Table(name = "t_course")
public class Course {
    @Column(primary = true,autoIncrement = true)
    private int id;
    @Column(nullable = false, length = 50)
    private String courseName;
    @Column(nullable = false)
    private Float ects;
    @Column(nullable = false)
    @ForeignKey(table = "t_teacher", foreignColumn = "id")
    private Teacher teacher;

    public Course() {}

    public Course(int id, String courseName, Float ects, Teacher teacher) {
        this.id = id;
        this.courseName = courseName;
        this.ects = ects;
        this.teacher = teacher;
    }

    public Course(String courseName, Float ects, Teacher teacher) {
        this.id = -1;
        this.courseName = courseName;
        this.ects = ects;
        this.teacher = teacher;
    }

    //TODO - Course: add getter and setter if needed
}