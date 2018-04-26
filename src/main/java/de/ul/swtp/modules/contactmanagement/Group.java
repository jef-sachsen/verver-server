package de.ul.swtp.modules.contactmanagement;

import com.fasterxml.jackson.annotation.*;
import de.ul.swtp.system.User;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Data
@Entity
@Table(name = "mv_cm_groups")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Group.class,
        resolver = GroupIdResolver.class)
public class Group implements Serializable {

    public Group() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @JsonIgnore
    private Long aclSidId;

    @NotNull
    @JsonProperty("permission")
    private PermissionEnum permissionEnum;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "mv_group_contact",
            joinColumns = {@JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "CONTACT_ID", referencedColumnName = "ID")})
    @JsonIdentityReference(alwaysAsId=true)
    private List<Contact> contacts;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "mv_group_contact_responsible",
            joinColumns = {@JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "CONTACT_ID", referencedColumnName = "ID")})
    @JsonIdentityReference(alwaysAsId=true)
    private List<Contact> responsibles;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId=true)
    private List<User> users;

    public String toString() {
        return "Group(id=" + this.getId() + ", name=" + this.getName() + ", aclSidId=" + this.getAclSidId() + ", permissionEnum=" + this.getPermissionEnum() + ", contacts=" + this.getContacts() + ", users=" + this.getUsers() + ")";
    }
}
