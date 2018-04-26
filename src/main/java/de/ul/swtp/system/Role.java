package de.ul.swtp.system;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@Entity
@Table(name = "mv_roles")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Role.class,
        resolver = RoleIdResolver.class)
public class Role {

    //@Null may well not work here, if the validations are call on update as well as create
    //@Null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "mv_role_authority",
        joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")},
        inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("permissions")
    private List<Authority> authorities;

    @ManyToMany(mappedBy = "roles")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIdentityReference(alwaysAsId=true)
    private List<User> users;

    public Role() {
    }

    protected boolean canEqual(Object other) {
        return other instanceof Role;
    }

    public String toString() {
        return "Role(id=" + this.getId() + ", name=" + this.getName() + ", authorities=" + this.getAuthorities() + ")";
    }

}
