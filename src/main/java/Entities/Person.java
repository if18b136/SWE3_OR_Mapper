package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.Table;
import java.time.LocalDate;

/**
 * Person test class for ORM framework.
 */
@Table(name = "t_person", subclasses = {Student.class,Teacher.class})
public class Person {
    @Column(primary = true/*,autoIncrement = true*/) // TODO - get autoIncrementation done - currently does not work with upsert because of automatic AutoIncrement flag that ALWAYS ignores the entry, even in upsert
    private int id;   // id should not get changed as it is the primary key - but if we want to enable autoIncrement, we need to change it after initialization
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false)
    private LocalDate birthDate;

    public Person(int ID, String firstName, String lastName, LocalDate birthDate) {
        this.id = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.id = -1;   // init with defined value for AI detection
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public Person() {}

    public int getId() { return id; }
    public void setID(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
