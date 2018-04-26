package de.ul.swtp.system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface RoleManager {

    @PreAuthorize("hasAuthority('SYS_ROLE_CREATE') or hasAuthority('ROLE_ADMIN')")
    Role create(Role role);

    @PreAuthorize("hasAuthority('SYS_ROLE_CREATE') or hasAuthority('ROLE_ADMIN')")
    Role create(Role role, List<Authority> authorities);

    @PreAuthorize("hasAuthority('SYS_ROLE_GETROLEBYID') or hasAuthority('ROLE_ADMIN')")
    Role getRoleById(Long roleId);

    @PreAuthorize("hasAuthority('SYS_ROLE_GETROLESBYIDS') or hasAuthority('ROLE_ADMIN')")
    Page<Role> getRolesByIds(List<Long> roleIds, Pageable pageable);

    @PreAuthorize("hasAuthority('SYS_ROLE_GETALL') or hasAuthority('ROLE_ADMIN')")
    Page<Role> getAll(Pageable pageable);

    @PreAuthorize("hasAuthority('SYS_ROLE_FINDUSERSINROLE') or hasAuthority('ROLE_ADMIN')")
    List<User> findUsersInRole(Long roleId);

    @PreAuthorize("hasAuthority('SYS_ROLE_UPDATE') or hasAuthority('ROLE_ADMIN')")
    Role update(Long id, Role role);

    @PreAuthorize("hasAuthority('SYS_ROLE_DELETE') or hasAuthority('ROLE_ADMIN')")
    void delete(Long roleId);

    @PreAuthorize("hasAuthority('SYS_ROLE_ADDUSERTOROLE') or hasAuthority('ROLE_ADMIN')")
    void addUserToRole(Role role, User user);

    @PreAuthorize("hasAuthority('SYS_ROLE_ADDUSERSTOROLE') or hasAuthority('ROLE_ADMIN')")
    void addUsersToRole(Role role, List<User> users);

    @PreAuthorize("hasAuthority('SYS_ROLE_REMOVEUSERFROMROLE') or hasAuthority('ROLE_ADMIN')")
    void removeUserFromRole(Role role, User user);

    @PreAuthorize("hasAuthority('SYS_ROLE_REMOVEUSERSFROMROLE') or hasAuthority('ROLE_ADMIN')")
    void removeUsersFromRole(Role role, List<User> users);

    @PreAuthorize("hasAuthority('SYS_ROLE_REMOVEALLUSERSFROMROLE') or hasAuthority('ROLE_ADMIN')")
    void removeAllUsersFromRole(Role role);

    @PreAuthorize("hasAuthority('SYS_ROLE_ADDAUTHORITYTOROLE') or hasAuthority('ROLE_ADMIN')")
    Role addAuthorityToRole(Long roleId, Long authorityId);

    @PreAuthorize("hasAuthority('SYS_ROLE_ADDAUTHORITIESTOROLE') or hasAuthority('ROLE_ADMIN')")
    Role addAuthoritiesToRole(Long roleId, List<Long> authorityIds);

    @PreAuthorize("hasAuthority('SYS_ROLE_REMOVEAUTHORITYFROMROLE') or hasAuthority('ROLE_ADMIN')")
    void removeAuthorityFromRole(Long roleId, Long authorityId);

    @PreAuthorize("hasAuthority('SYS_ROLE_REMOVEAUTHORITIESFROMROLE') or hasAuthority('ROLE_ADMIN')")
    void removeAuthoritiesFromRole(Long roleId, List<Long> authorityIds);

}
