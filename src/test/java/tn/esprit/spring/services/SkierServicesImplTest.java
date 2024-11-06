package tn.esprit.spring.services;


import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class SkierServicesImplTest {
    @InjectMocks
    private SkierServicesImpl skierServices;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private IPisteRepository pisteRepository;
    @Mock
    private ISkierRepository skierRepository;

    Subscription subscriptionAnnual =  Subscription.
            builder()
            .typeSub(TypeSubscription.ANNUAL)
            .startDate(LocalDate.of(2024, 1, 1))
            .build();

    Subscription subscriptionSemestriel = Subscription.builder()
            .typeSub(TypeSubscription.SEMESTRIEL)
            .startDate(LocalDate.of(2024, 1, 1))
            .build();

    Subscription subscriptionMonthly = Subscription.builder()
            .typeSub(TypeSubscription.MONTHLY)
            .startDate(LocalDate.of(2024, 1, 1))
            .build();

    public List<Skier> skiersList = new ArrayList<Skier>(){
        {

            add(Skier.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .subscription(subscriptionAnnual)
                    .build());

            add( Skier.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .subscription(subscriptionSemestriel)
                    .build());

            add( Skier.builder()
                    .firstName("Mark")
                    .lastName("Smith")
                    .subscription(subscriptionMonthly)
                    .build());
        }
    };

    @Test
    void retrieveAllSkiersTest() {
        // Arrange: Mock the behavior of the repository
        Mockito.when(skierRepository.findAll()).thenReturn(skiersList);

        // Act: Call the method to be tested
        List<Skier> result = skierServices.retrieveAllSkiers();

        // Assert: Verify the results
        assertEquals(3, result.size(), "Should return three skiers");
        assertEquals("John", result.get(0).getFirstName(), "First skier's name should be John");
        assertEquals("Jane", result.get(1).getFirstName(), "Second skier's name should be Jane");
        assertEquals("Mark", result.get(2).getFirstName(), "Third skier's name should be Mark");
    }

    @Test
    void addSkierTest() {
        for (Skier skierDTO : skiersList) {
            // Arrange : Simuler le comportement du repository
            Skier skier = Skier.builder()
                    .firstName(skierDTO.getFirstName())
                    .lastName(skierDTO.getLastName())
                    .subscription(skierDTO.getSubscription())
                    .build();

            Mockito.when(skierRepository.save(Mockito.any(Skier.class))).thenReturn(skier);

            // Act : Appeler la méthode `addSkier`
            Skier result = skierServices.addSkier(skier);

            // Assert : Vérifier que la date de fin a bien été ajustée en fonction du type d'abonnement
            assertNotNull(result);
            switch (skierDTO.getSubscription().getTypeSub()) {
                case ANNUAL:
                    assertEquals(LocalDate.of(2025, 1, 1), skierDTO.getSubscription().getEndDate(),
                            "ANNUAL subscription should set endDate to one year later.");
                    break;
                case SEMESTRIEL:
                    assertEquals(LocalDate.of(2024, 7, 1), skierDTO.getSubscription().getEndDate(),
                            "SEMESTRIEL subscription should set endDate to six months later.");
                    break;
                case MONTHLY:
                    assertEquals(LocalDate.of(2024, 2, 1), skierDTO.getSubscription().getEndDate(),
                            "MONTHLY subscription should set endDate to one month later.");
                    break;

            }

        }
        // Vérifier que la méthode `save` du repository a été appelée
        verify(skierRepository, times(3)).save(any(Skier.class));
    }


    @Test
    void testAddSkierAndAssignToCourse() {
        // Données de test
        Long numCourse = 1L;

        // Créer un SkierDTO
        Skier skier = new Skier();
        skier.setFirstName("Alice");
        skier.setLastName("Wonderland");
        skier.setRegistrations(new HashSet<>()); // Initialiser l'ensemble des inscriptions

        // Créer une inscription
        Registration registration = new Registration();
        registration.setCourse(new Course()); // S'assurer que l'objet Course existe
        skier.getRegistrations().add(registration);

        // Mocks pour les dépôts
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Course course = new Course(); // Créer un cours pour l'assignation
        course.setNumCourse(numCourse); // Assurez-vous que l'ID correspond
        when(courseRepository.getById(numCourse)).thenReturn(course);

        // Exécution de la méthode à tester
        Skier result = skierServices.addSkierAndAssignToCourse(skier, numCourse);

        // Vérifications
        assertNotNull(result, "The result should not be null");
        assertEquals("Alice", result.getFirstName(), "The skier's first name should be 'Alice'");
        assertEquals("Wonderland", result.getLastName(), "The skier's last name should be 'Wonderland'");

        // Vérifier que l'inscription a été mise à jour
        for (Registration r : result.getRegistrations()) {
            assertEquals(result, r.getSkier(), "The skier in the registration should match the saved skier");
            assertEquals(course, r.getCourse(), "The course in the registration should match the course assigned");
        }

        // Vérifier que les dépôts ont été appelés
        verify(skierRepository, times(1)).save(skier);
        verify(courseRepository, times(1)).getById(numCourse);
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }
    @Test
    void testRemoveSkier() {
        // Données de test
        Long numSkier = 1L;

        // Exécution de la méthode à tester
        skierServices.removeSkier(numSkier);

        // Vérification que deleteById a été appelé avec le bon identifiant
        verify(skierRepository, times(1)).deleteById(numSkier);
    }

    @Test
    void testRetrieveSkier() {
        // Données de test
        Long numSkier = 1L;
        Skier expectedSkier = Skier.builder()
                .firstName("John")
                .lastName("Doe")
                .subscription(new Subscription())  // Remplissez avec les détails nécessaires
                .build();

        // Simuler le comportement du repository
        when(skierRepository.findById(numSkier)).thenReturn(Optional.of(expectedSkier));

        // Exécution de la méthode à tester
        Skier actualSkier = skierServices.retrieveSkier(numSkier);

        // Vérification des résultats
        assertNotNull(actualSkier);
        assertEquals(expectedSkier.getFirstName(), actualSkier.getFirstName());
        assertEquals(expectedSkier.getLastName(), actualSkier.getLastName());

        // Vérification que findById a été appelé avec le bon identifiant
        verify(skierRepository, times(1)).findById(numSkier);

        // Testez le cas où le skieur n'existe pas
        when(skierRepository.findById(numSkier)).thenReturn(Optional.empty());
        Skier nonExistentSkier = skierServices.retrieveSkier(numSkier);

        // Vérification que le résultat est nul
        assertNull(nonExistentSkier);
    }


    @Test
    void testRetrieveSkiersBySubscriptionType() {
        // Données de test
        TypeSubscription subscriptionType = TypeSubscription.ANNUAL;


        Skier skier1 = Skier.builder()
                .firstName("John")
                .lastName("Doe")
                .subscription(subscriptionAnnual)
                .build();

        Skier skier2 = Skier.builder()
                .firstName("Jane")
                .lastName("Doe")
                .subscription(subscriptionMonthly)
                .build();

        Arrays.asList(skier1, skier2);

        // Simuler le comportement du repository
        when(skierRepository.findBySubscription_TypeSub(subscriptionType)).thenReturn(Arrays.asList(skier1));

        // Exécution de la méthode à tester
        List<Skier> retrievedSkiers = skierServices.retrieveSkiersBySubscriptionType(subscriptionType);

        // Vérification des résultats
        assertNotNull(retrievedSkiers);
        assertEquals(1, retrievedSkiers.size());
        assertEquals("John", retrievedSkiers.get(0).getFirstName());

        // Testez le cas où aucun skieur n'est trouvé avec le type d'abonnement
        when(skierRepository.findBySubscription_TypeSub(subscriptionType)).thenReturn(Collections.emptyList());
        List<Skier> emptyResult = skierServices.retrieveSkiersBySubscriptionType(subscriptionType);
        assertNotNull(emptyResult);
        assertTrue(emptyResult.isEmpty());
    }




}