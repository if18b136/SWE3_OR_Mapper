package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.Table;
import java.time.LocalDate;

@Table(name = "t_person")
public class Person {
    @Column(primary = true,autoIncrement = true)
    private final int ID;   // id should not get changed as it is the primary key
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false)
    private LocalDate birthDate;

    public Person(int ID, String firstName, String lastName, LocalDate birthDate) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public int getID() { return ID; }
    // public void setID(int ID) { this.ID = ID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
