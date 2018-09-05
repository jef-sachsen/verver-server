package de.ul.swtp.modules.contactmanagement;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.ul.swtp.modules.contactmanagement.contactdetails.Address;
import de.ul.swtp.modules.contactmanagement.contactdetails.BankAccount;
import de.ul.swtp.modules.contactmanagement.contactdetails.VoluntaryDetails;
import de.ul.swtp.system.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cm_contacts")
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Contact.class,
        resolver = ContactIdResolver.class)
public class Contact implements Serializable {

    //@Null may well not work here, if the validations are call on update as well as create
    //@Null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // perhaps we need a @NotBlank here? Are emails obligatory?
    @Email(regexp = "^.*@.*\\..*")
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @DateTimeFormat
    private Date dateOfBirth;

    //TODO: write regex (or custom validator) for phone numbers
    //@Pattern(regexp = )
    // Note: phone numbers are currently optional
    @Digits(integer = 16, fraction = 0)
    private String phone;

    //@NotBlank
    @OneToOne
    private Address address;

    @OneToOne
    private BankAccount bankAccount;

    @OneToOne
    private VoluntaryDetails voluntaryDetails;

    @ManyToMany(mappedBy = "contacts", fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Group> groups;

    @OneToOne(mappedBy = "contact"/*, cascade = CascadeType.ALL*/, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    //@JsonIgnore
    private User user;

}
