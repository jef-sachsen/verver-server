package de.ul.swtp.modules.contactmanagement.contactdetails;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cm_voluntarydetails")
@Data
public class VoluntaryDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profession;

}
