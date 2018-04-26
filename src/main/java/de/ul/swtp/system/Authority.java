package de.ul.swtp.system;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "mv_authorities")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Authority.class,
        resolver = AuthorityIdResolver.class)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "authorities")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    //@JsonIdentityReference(alwaysAsId=true)
    private List<Role> roles;

    public Authority() {
    }

    protected boolean canEqual(Object other) {
        return other instanceof Authority;
    }

    @Override
    public String toString() {
        return "Authority(id=" + this.getId() + ", name=" + this.getName() + ")";
    }

}
