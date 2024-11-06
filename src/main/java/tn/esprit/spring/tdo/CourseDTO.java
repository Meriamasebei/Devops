package tn.esprit.spring.tdo;


import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;

import javax.validation.constraints.*;
@Getter
@Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO extends Course {
    @NotNull (message = "Course number cannot be null")
    private Long numCourse;

    @NotNull(message = "Level cannot be null")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 10, message = "Level must be no more than 10")
    private int level;

    @NotNull(message = "Type of course cannot be null")
    private TypeCourse typeCourse;

    @NotNull(message = "Support cannot be null")
    private Support support;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive number")
    private Float price;

    @Min(value = 1, message = "Time slot must be at least 1")
    private int timeSlot;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    private String description;

    @NotBlank(message = "Location cannot be blank")
    private String location;

}