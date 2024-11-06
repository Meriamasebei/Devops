package tn.esprit.spring.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;

import tn.esprit.spring.entities.CourseMapper;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.tdo.CourseDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CourseServicesImplTest {
    @InjectMocks
    private CourseServicesImpl courseServices;
    @Mock
    private ICourseRepository courseRepository;
    @InjectMocks
    private final CourseMapper courseMapper = new CourseMapper();

    @Test
    void testToEntity() {
        CourseDTO courseDTO = new CourseDTO(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100.0F, 2, "Sample Course", "Paris");

        Course course = courseMapper.toEntity(courseDTO);

        assertEquals(1L, course.getNumCourse());
        assertEquals(1, course.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, course.getTypeCourse());
        assertEquals(Support.SKI, course.getSupport());
        assertEquals(100.0F, course.getPrice());
        assertEquals(2, course.getTimeSlot());
        assertEquals("Sample Course", course.getDescription());
        assertEquals("Paris", course.getLocation());
    }
    @Test
    void testToDTO() {
        Course course = Course.builder()
                .numCourse(1L)
                .level(1)
                .typeCourse(TypeCourse.COLLECTIVE_CHILDREN)
                .support(Support.SKI)
                .price(100.0F)
                .timeSlot(2)
                .description("Sample Course")
                .location("Paris")
                .build();

        CourseDTO courseDTO = courseMapper.toDTO(course);

        assertEquals(1L, courseDTO.getNumCourse());
        assertEquals(1, courseDTO.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, courseDTO.getTypeCourse());
        assertEquals(Support.SKI, courseDTO.getSupport());
        assertEquals(100.0F, courseDTO.getPrice());
        assertEquals(2, courseDTO.getTimeSlot());
        assertEquals("Sample Course", courseDTO.getDescription());
        assertEquals("Paris", courseDTO.getLocation());
    }



    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void retrieveAllCourses() {
        Course course1 = new Course();
        Course course2 = new Course();
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1,course2));
        // Act
        List<Course> courses = courseServices.retrieveAllCourses();

        // Assert
        assertEquals(2, courses.size());
        verify(courseRepository, times(1)).findAll();
    }


    @Test
    void addCourseTest() {
        // Arrange
        CourseDTO course = new CourseDTO();
        course.setDescription("Java Basics");
        course.setTypeCourse(TypeCourse.INDIVIDUAL);
        course.setSupport(Support.SKI);
        course.setPrice(10.0f);
        course.setTimeSlot(1);
        course.setLevel(1);

        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course savedCourse = courseServices.addCourse(course);

        assertNotNull(savedCourse);
        assertEquals("Java Basics", savedCourse.getDescription());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse() {
        CourseDTO course = new CourseDTO();
        when(courseRepository.save(course)).thenReturn(course);
        Course updatedCourse = courseServices.updateCourse(course);
        assertNotNull(updatedCourse);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void retrieveCourse() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Course retrievedCourse = courseServices.retrieveCourse(courseId);
        assertNotNull(retrievedCourse);
        verify(courseRepository, times(1)).findById(courseId);
    }
    @Test
    void deleteCourseTest() {
        // Arrange
        Long courseId = 1L;
        doNothing().when(courseRepository).deleteById(courseId);
        // Act
        courseServices.deleteCourse(courseId);
        // Assert
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void searchCourses() {
        // Arrange
        Integer level = 2;
        TypeCourse typeCourse = TypeCourse.COLLECTIVE_CHILDREN;
        Float minPrice = 100.0f;
        Float maxPrice = 500.0f;
        String description = "test";

        // Utilisation du Builder pour créer des cours
        List<Course> mockCourses = Arrays.asList(
                Course.builder()
                        .level(level)
                        .description(description)
                        .support(Support.SKI)
                        .price(10.0f)
                        .timeSlot(1)
                        .typeCourse(typeCourse)
                        .price(200.0f)
                        .build(),
                Course.builder()
                        .level(5)
                        .description("test2")
                        .support(Support.SKI)
                        .price(10.0f)
                        .timeSlot(1)
                        .typeCourse(typeCourse)
                        .price(300.0f)
                        .build()
        );

        when(courseRepository.findAllByCriteria(level, typeCourse, minPrice, maxPrice))
                .thenReturn(mockCourses);

        // Act
        List<Course> courses = courseServices.searchCourses(level, typeCourse, minPrice, maxPrice);

        // Assert
        assertEquals(2, courses.size());
        assertTrue(courses.stream().allMatch(course -> course.getPrice() >= minPrice && course.getPrice() <= maxPrice));
        verify(courseRepository, times(1)).findAllByCriteria(level, typeCourse, minPrice, maxPrice);

    }

    @Test
    void recommendCourses() {
        // Arrange
        TypeCourse typeCourse = TypeCourse.COLLECTIVE_CHILDREN;

        Course course1 = Course.builder()
                .level(2)
                .support(Support.SKI)
                .typeCourse(typeCourse)
                .description("Cours A")
                .price(10.0f)
                .timeSlot(2)
                .build();

        Course course2 = Course.builder()
                .level(3)
                .support(Support.SKI)
                .typeCourse(typeCourse)
                .description("Cours B")
                .price(10.0f)
                .timeSlot(2)
                .build();
        when(courseRepository.findByTypeCourse(typeCourse)).thenReturn(Arrays.asList(course1, course2));

        // Act
        List<Course> recommendedCourses = courseServices.recommendCourses(typeCourse);

        // Assert
        assertEquals(2, recommendedCourses.size(), "Il devrait y avoir 2 cours recommandés");
        assertEquals("Cours A", recommendedCourses.get(0).getDescription());
        assertEquals("Cours B", recommendedCourses.get(1).getDescription());
        verify(courseRepository, times(1)).findByTypeCourse(typeCourse);
    }


}