package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.MtoN;
import ORM.Annotations.Table;

import java.util.List;

/**
 * Custom course class for ORM framework testing.
 */
@Table(name = "t_course")
public class Course {
    /**
     * Database column "id".
     */
    @Column(primary = true)
    private int id;
    /**
     * Database column "courseName".
     */
    @Column(nullable = false, length = 50)
    private String courseName;
    /**
     * Database column "ects".
     */
    @Column(nullable = false)
    private Double ects;
    /**
     * Database column foreign key "teacher_id".
     */
    @Column(nullable = false)
    @ForeignKey(table = "t_teacher", column = "id")
    private Teacher teacher;

//    /**
//     * Database column for m:n entries for students attending the course.
//     */
//    @Column
//    @MtoN(table = "t_student_course", correspondingClass = Student.class)
//    private List<Student> students; // do not init with = new Arraylist() here!

    /**
     * Empty constructor for course class.
     */
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
