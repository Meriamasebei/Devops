package tn.esprit.spring.services;

import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.tdo.CourseDTO;

import java.util.List;

public interface ICourseServices {

    List<Course> retrieveAllCourses();

    Course  addCourse(CourseDTO course);

    Course updateCourse(CourseDTO course);

    Course retrieveCourse(Long numCourse);

    void deleteCourse(Long numCourse);
    List<Course> searchCourses(Integer level, TypeCourse typeCourse, Float minPrice, Float maxPrice);
    List<Course> recommendCourses(TypeCourse typeCourse);

}
