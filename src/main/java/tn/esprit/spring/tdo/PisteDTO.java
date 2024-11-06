package tn.esprit.spring.tdo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.entities.Skier;

import java.util.Set;

@Getter
@Setter
public class PisteDTO  {

    private Long numPiste;
    private String namePiste;
    private Color color;
    private int length;
    private int slope;
    private Set<Skier> skiers;
}