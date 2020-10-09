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


}
