package tn.esprit.spring.entities;
import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
@Builder
public class Course implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numCourse;

	@NotNull(message = "Level cannot be null")
	@Min(value = 1, message = "Level must be at least 1")
	@Max(value = 10, message = "Level must be no more than 10")
	int level;

	@NotNull(message = "Type of course cannot be null")
	@Enumerated(EnumType.STRING)
	TypeCourse typeCourse;

	@NotNull(message = "Support cannot be null")
	@Enumerated(EnumType.STRING)
	Support support;

	@NotNull(message = "Price cannot be null")
	@Positive(message = "Price must be a positive number")
	Float price;
	@Min(value = 1, message = "Time slot must be at least 1")
	int timeSlot;

	@NotBlank(message = "Description cannot be blank")
	@Size(max = 255, message = "Description cannot be longer than 255 characters")
	String description;

	@NotBlank(message = "Location cannot be blank")
	String location;

	@JsonIgnore
	@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
	private Set<Registration> registrations;


}