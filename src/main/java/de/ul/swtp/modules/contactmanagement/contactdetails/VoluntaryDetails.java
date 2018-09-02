package de.ul.swtp.modules.contactmanagement.contactdetails;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cm_voluntarydetails")
@Data
public class VoluntaryDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profession;

}
