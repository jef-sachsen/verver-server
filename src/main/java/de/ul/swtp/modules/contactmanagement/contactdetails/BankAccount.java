package de.ul.swtp.modules.contactmanagement.contactdetails;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cm_bankdetails")
@Data
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountOwner;
    private String iban;

}
