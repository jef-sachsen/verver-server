package de.ul.swtp.system;

import com.fasterxml.jackson.annotation.*;
import de.ul.swtp.modules.contactmanagement.Contact;
import de.ul.swtp.modules.contactmanagement.Group;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "mv_users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = User.class,
        resolver = UserIdResolver.class)
public class User {

    //TODO: check if @JsonIgnore renders the validations ineffective in the actual app
    //FIXME @Null doesn't trigger on id in Postman
    //@Null may well not work here, if the validations are call on update as well as create
    //@Null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //The regex ensures that only emails with top level domains are accepted (note that local email addresses are actually valid but of little use to us)
    @Email(regexp="^.*@.*\\..*")
    @javax.validation.constraints.NotBlank
    private String username;

    @Length(min=4, max=256)
    @NotEmpty
    @JsonIgnore
    @JsonSetter
    private String password;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean admin;

    @Past
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordResetDate;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "mv_user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Role> roles;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "mv_user_group",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Group> groups;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    @JsonIdentityReference(alwaysAsId=true)
    //@JsonIgnore
    private Contact contact;

    @JsonIgnore
    public List<Authority> getAuthorities() {
        //convert groups to authorities
        List<Authority> groupsToAuthorities = new ArrayList<>();
        for (Group group : groups) {
            Authority authority = new Authority();
            authority.setName("CM_GROUP_" + group.getId());
            groupsToAuthorities.add(authority);
        }
        //resolve role authorities
        List<List<Authority>> rolesToListOfAuthorities = roles.stream().map(role -> role.getAuthorities()).collect(Collectors.toList());
        List<Authority> roleAuthorities = rolesToListOfAuthorities.stream().flatMap(List::stream).collect(Collectors.toList());
        //combine the two lists
        List<Authority> returnList = Stream.of(groupsToAuthorities, roleAuthorities).flatMap(Collection::stream).collect(Collectors.toList());

        //Add Admin or User authority
        if (this.getAdmin()) {
            Authority authority = new Authority();
            authority.setName("ROLE_ADMIN");
            authority.setId((long)(returnList.size() + 1));
            returnList.add(authority);
        } else {
            Authority authority = new Authority();
            authority.setName("ROLE_USER");
            authority.setId((long)(returnList.size() + 1));
            returnList.add(authority);
        }
        return returnList;
    }
}
