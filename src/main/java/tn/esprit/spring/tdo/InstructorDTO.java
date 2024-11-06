package tn.esprit.spring.tdo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.entities.Instructor;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class InstructorDTO extends Instructor {
    String firstName;
    String lastName;
    LocalDate dateOfHire;
}