package de.ul.swtp.modules.contactmanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByContacts(Contact contact);

    Page<Group> findAllByIdIn(List<Long> ids, Pageable pageable);
}
