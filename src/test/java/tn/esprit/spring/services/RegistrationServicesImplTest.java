package tn.esprit.spring.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.tdo.RegistrationDTO;

@ExtendWith(MockitoExtension.class)

class RegistrationServicesImplTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRegistrationAndAssignToSkier() {
        // Créer un skieur avec des valeurs valides pour tous les champs
        Skier skier = new Skier(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "CityName", null, null, null);
        Registration registration = new Registration();
        registration.setNumWeek(5);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationService.addRegistrationAndAssignToSkier(registration, 1L);
        assertNotNull(result);
        assertEquals(skier, result.getSkier());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAssignRegistrationToCourse() {
        Registration registration = new Registration();
        Course course = new Course();
        registration.setNumWeek(5);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationService.assignRegistrationToCourse(1L, 1L);
        assertNotNull(result);
        assertEquals(course, result.getCourse());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_AlreadyRegistered() {
        Skier skier = new Skier(1L, "John", "Doe", LocalDate.of(2010, 1, 1), "CityName", null, null, null);
        Course course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        Registration registration = new Registration();
        registration.setNumWeek(5);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(5, 1L, 1L)).thenReturn(1L);

        Registration result = registrationService.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);
        assertNull(result); // Devrait renvoyer null car le skieur est déjà inscrit
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_AgeRestriction() {
        // Initialisation de l'objet Skier avec un âge de 14 ans
        Skier skier = new Skier(1L, "John", "Doe", LocalDate.of(2010, 1, 1), "CityName", null, null, null);

        // Initialisation de l'objet Course avec le type de cours pour les enfants
        Course course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        // Création de l'objet Registration avec les valeurs appropriées
        Registration registration = new Registration();
        registration.setNumWeek(5);

        // Configuration des mocks pour retourner des valeurs valides et non nulles
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier)); // Le skieur est trouvé
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course)); // Le cours est trouvé
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(5, 1L, 1L)).thenReturn(0L); // Pas encore inscrit
        when(registrationRepository.countByCourseAndNumWeek(course, 5)).thenReturn(5L); // 5 inscriptions pour la semaine

        // Mock pour enregistrer l'inscription et retourner un objet non nul
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Appel de la méthode de service
        Registration result = registrationService.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Assertions pour vérifier le résultat
        assertNotNull(result, "L'inscription ne doit pas être nulle"); // Vérifie que l'inscription n'est pas null
        assertEquals(skier, result.getSkier()); // Vérifie que le skieur est bien assigné
        assertEquals(course, result.getCourse()); // Vérifie que le cours est bien assigné

        // Vérifie que l'inscription a été sauvegardée une fois
        verify(registrationRepository, times(1)).save(registration);
    }


    @Test
    void testNumWeeksCourseOfInstructorBySupport() {
        List<Integer> expectedWeeks = Arrays.asList(1, 2, 3);
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(1L, Support.SKI)).thenReturn(expectedWeeks);

        List<Integer> result = registrationService.numWeeksCourseOfInstructorBySupport(1L, Support.SKI);

        // Utiliser assertIterableEquals pour comparer les listes
        assertIterableEquals(expectedWeeks, result);
    }

}