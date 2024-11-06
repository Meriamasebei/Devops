package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;

import java.util.List;

public interface ICourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE (:level IS NULL OR c.level = :level) " +
            "AND (:typeCourse IS NULL OR c.typeCourse = :typeCourse) " +
            "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR c.price <= :maxPrice) ")
    List<Course> findAllByCriteria(@Param("level") Integer level,
                                   @Param("typeCourse") TypeCourse typeCourse,
                                   @Param("minPrice") Float minPrice,
                                   @Param("maxPrice") Float maxPrice );

    List<Course> findByTypeCourse(TypeCourse typeCourse);

}
