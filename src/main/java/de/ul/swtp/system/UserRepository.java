package de.ul.swtp.system;

import de.ul.swtp.modules.contactmanagement.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByRoles(Role role);

    List<User> findAllByGroups(Group group);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}
