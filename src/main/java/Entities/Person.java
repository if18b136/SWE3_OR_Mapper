package Entities;

import java.time.LocalDate;

public class Person {
    private int ID;
    private String firstName;
    private String lastName;
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
