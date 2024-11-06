package tn.esprit.spring.tdo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Skier;

@Getter
@Setter
@ToString
public class RegistrationDTO {
    private Long numRegistration;
    private int numWeek;

    private Skier skier;
    private Course course;

}