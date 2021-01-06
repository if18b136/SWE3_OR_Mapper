package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.MtoN;
import ORM.Annotations.Table;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Student test class for ORM framework.
 */
@Table(name = "t_student")
public class Student extends Person{
    @Column(primary = true)
    @ForeignKey(table = "t_person", column = "id")
    private int id;
    /**
     * Database column for m:n entries for courses the student attends.
     */
    @Column
    @MtoN(table = "t_student_course", correspondingClass = Course.class)
//    @ForeignKey(table = "t_student_courses", column = "student_id", foreignColumn = "course_id")
    private List<Course> courses;   // do not init with = new Arraylist() here!

    public Student() {}

    public Student(int id) {
        this.id = id;
    }

    public Student(int id, List<Course> courses) {
        this.id = id;
        this.courses = courses;
    }

    public void addCourse(Course course) {
        if(this.courses == null) {
            this.courses = new ArrayList<>();
            this.courses.add(course);
        } else if (this.courses.isEmpty()) {
            this.courses.add(course);
        } else {
            if (!this.courses.contains(course)) {
                this.courses.add(course);
            }
        }
    }

    public void removeCourse(Course course) {
        if(this.courses != null && !this.courses.isEmpty()) {
            this.courses.remove(course);
        }
    }

    public List<String> getBookedCourses() {
        List<String> list = new ArrayList<>();
        for (Course course : this.courses) {
            list.add(course.getCourseName());
        }
        return list;
    }
}
