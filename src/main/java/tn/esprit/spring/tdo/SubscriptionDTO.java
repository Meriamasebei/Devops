package tn.esprit.spring.tdo;



import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;

@Getter
@Setter
@ToString

public class SubscriptionDTO extends Subscription {

    private Long numSub;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float price;
    private TypeSubscription typeSub;


}