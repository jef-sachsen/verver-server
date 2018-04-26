package de.ul.swtp.system;

import de.ul.swtp.modules.contactmanagement.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserManager {

    @PreAuthorize("hasAuthority('SYS_USER_GETALL') or hasAuthority('ROLE_ADMIN')")
    Page<User> getAll(Pageable pageable);

    @PreAuthorize("hasAuthority('SYS_USER_GETUSERBYID') or hasAuthority('ROLE_ADMIN')")
    User getUserById(Long id);

    @PreAuthorize("hasAuthority('SYS_USER_GETUSERSBYIDS') or hasAuthority('ROLE_ADMIN')")
    Page<User> getUsersByIds(List<Long> userIds, Pageable pageable);

    @PreAuthorize("hasAuthority('SYS_USER_GETUSERBYUSERNAME') or hasAuthority('ROLE_ADMIN')")
    User getUserByUsername(String username);

    @PreAuthorize("hasAuthority('SYS_USER_GETALLBYROLE') or hasAuthority('ROLE_ADMIN')")
    List<User> getAllByRole(Role role);

    @PreAuthorize("hasAuthority('SYS_USER_GETALLBYGROUP') or hasAuthority('ROLE_ADMIN')")
    List<User> getAllByGroup(Group group);

    @PreAuthorize("hasAuthority('SYS_USER_CREATE') or hasAuthority('ROLE_ADMIN')")
    User create(User user);

    @PreAuthorize("hasAuthority('SYS_USER_UPDATE') or hasAuthority('ROLE_ADMIN')")
    User update(Long id, User user);

    @PreAuthorize("hasAuthority('SYS_USER_UPDATE') or hasAuthority('ROLE_ADMIN')")
    User updateWithoutTouchingAcl(Long id, User user);

    @PreAuthorize("hasAuthority('SYS_USER_DELETE') or hasAuthority('ROLE_ADMIN')")
    void delete(Long id);

    void enableUser(Long id);

    User signUpUser(User user);
}
