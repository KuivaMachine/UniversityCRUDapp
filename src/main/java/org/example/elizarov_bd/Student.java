package org.example.elizarov_bd;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor


public class Student {
     int id;
     String lastName;
     String firstName;
     String fath;
    int birthYear;
    int mark_1;
    int mark_2;
    int mark_3;
    int mark_4;
    String teach_name;
    String teach_surname;
    String teach_midname;

    public Student(int mark_4, int mark_3, int mark_2, int mark_1, int birthYear, String fath, String firstName, String lastName, int id) {
        this.mark_4 = mark_4;
        this.mark_3 = mark_3;
        this.mark_2 = mark_2;
        this.mark_1 = mark_1;
        this.birthYear = birthYear;
        this.fath = fath;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }
}
