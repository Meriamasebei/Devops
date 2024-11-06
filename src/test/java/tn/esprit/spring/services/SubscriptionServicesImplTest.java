package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.tdo.SkierDTO;
import tn.esprit.spring.tdo.SubscriptionDTO;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SubscriptionServicesImplTest {
    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionServices;

    private Subscription subscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        subscription.setStartDate(LocalDate.now());
    }
    @Test
    void addSubscription() {
        // Setup
        subscription.setEndDate(subscription.getStartDate().plusMonths(1));
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        // Test
        Subscription result = subscriptionServices.addSubscription(subscription);

        // Assert
        assertNotNull(result);
        assertEquals(subscription.getTypeSub(), result.getTypeSub());
        assertEquals(subscription.getStartDate().plusMonths(1), result.getEndDate());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void updateSubscription() {
        // Setup
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        // Test
        Subscription result = subscriptionServices.updateSubscription(subscription);

        // Assert
        assertNotNull(result);
        assertEquals(subscription.getStartDate(), result.getStartDate());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void retrieveSubscriptionById() {
        // Setup
        Long id = 1L;
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));

        // Test
        Subscription result = subscriptionServices.retrieveSubscriptionById(id);

        // Assert
        assertNotNull(result);
        assertEquals(subscription.getTypeSub(), result.getTypeSub());
        verify(subscriptionRepository, times(1)).findById(id);
    }

    @Test
    void getSubscriptionByType() {
        // Setup
        TypeSubscription type = TypeSubscription.MONTHLY;
        Set<Subscription> mockSubscriptions = Collections.singleton(subscription);  // Use Collections.singleton for Java 8 compatibility
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(type)).thenReturn(mockSubscriptions);

        // Test
        Set<Subscription> result = subscriptionServices.getSubscriptionByType(type);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(subscriptionRepository, times(1)).findByTypeSubOrderByStartDateAsc(type);
    }


    @Test
    void retrieveSubscriptionsByDates() {
        // Setup
        LocalDate date1 = LocalDate.now().minusDays(10);
        LocalDate date2 = LocalDate.now().plusDays(10);
        List<Subscription> mockSubscriptions = Arrays.asList(subscription);  // Use Arrays.asList for Java 8 compatibility
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(date1, date2)).thenReturn(mockSubscriptions);

        // Test
        List<Subscription> result = subscriptionServices.retrieveSubscriptionsByDates(date1, date2);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(subscriptionRepository, times(1)).getSubscriptionsByStartDateBetween(date1, date2);
    }


    @Test
    void retrieveSubscriptions() {
        // Setup
        Long numSub = 1L;
        Subscription subscription = new Subscription();
        subscription.setNumSub(numSub);
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.MONTHLY); // or any other type

        // Now call the method that adds subscription
        subscriptionServices.addSubscription(subscription); // This will set endDate properly

        List<Subscription> mockSubscriptions = new ArrayList<>();
        mockSubscriptions.add(subscription);
        when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(mockSubscriptions);
        Skier skierDTO = new Skier();
        skierDTO.setFirstName("Joe");
        skierDTO.setLastName("DOhn");
        when(skierRepository.findBySubscription(subscription)).thenReturn(skierDTO);

        // Test
        subscriptionServices.retrieveSubscriptions();

        // Verify interactions
        verify(subscriptionRepository, times(1)).findDistinctOrderByEndDateAsc();
        verify(skierRepository, times(1)).findBySubscription(subscription);
    }

    @Test
    void showMonthlyRecurringRevenue() {
        // Setup
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY)).thenReturn(100f);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL)).thenReturn(600f);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL)).thenReturn(1200f);

        // Test
        subscriptionServices.showMonthlyRecurringRevenue();

        // Assert that logs revenue (you may use a log capturing utility for testing log output)
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY);
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL);
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL);
    }
}