package de.ul.swtp.modules.contactmanagement;

import de.ul.swtp.system.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface GroupManager {

    @PreAuthorize("hasAuthority('CM_GROUP_CREATE') or hasAuthority('ROLE_ADMIN')")
    Group create(Group group);

    @PreAuthorize("hasAuthority('CM_GROUP_GETGROUPBYID') or hasAuthority('ROLE_ADMIN')")
    Group getGroupById(Long id);

    @PreAuthorize("hasAuthority('CM_GROUP_GETGROUPSBYIDS') or hasAuthority('ROLE_ADMIN')")
    Page<Group> getGroupsByIds(List<Long> groupIds, Pageable pageable);

    @PreAuthorize("hasAuthority('CM_GROUP_GETALL') or hasAuthority('ROLE_ADMIN')")
    Page<Group> getAll(Pageable pageable);

    @PreAuthorize("hasAuthority('CM_GROUP_GETALLBYCONTACTS') or hasAuthority('ROLE_ADMIN')")
    List<Group> getAllByContacts(Contact contact);

    @PreAuthorize("hasAuthority('CM_GROUP_FINDUSERSINGROUP') or hasAuthority('ROLE_ADMIN')")
    List<User> findUsersInGroup(Long id);

    @PreAuthorize("hasAuthority('CM_GROUP_UPDATE') or hasAuthority('ROLE_ADMIN')")
    Group update(Long id, Group group);

    @PreAuthorize("hasAuthority('CM_GROUP_UPDATE') or hasAuthority('ROLE_ADMIN')")
    Group updateWithoutTouchingAcl(Long id, Group group);

    @PreAuthorize("hasAuthority('CM_GROUP_DELETE') or hasAuthority('ROLE_ADMIN')")
    void delete(Long id);

    @PreAuthorize("hasAuthority('CM_GROUP_ADDUSERTOGROUP') or hasAuthority('ROLE_ADMIN')")
    void addUserToGroup(Long groupId, Long userId);

    @PreAuthorize("hasAuthority('CM_GROUP_ADDUSERSTOGROUP') or hasAuthority('ROLE_ADMIN')")
    void addUsersToGroup(Long groupId, List<Long> userIds);

    @PreAuthorize("hasAuthority('CM_GROUP_REMOVEUSERFROMGROUP') or hasAuthority('ROLE_ADMIN')")
    void removeUserFromGroup(Long groupId, Long userId);

    @PreAuthorize("hasAuthority('CM_GROUP_REMOVEUSERSFROMGROUP') or hasAuthority('ROLE_ADMIN')")
    void removeUsersFromGroup(Long groupId, List<Long> userIds);

    @PreAuthorize("hasAuthority('CM_GROUP_ADDAUTHORITYTOGROUP') or hasAuthority('ROLE_ADMIN')")
    void addAuthorityToGroup(Long groupId, Long authorityId);

    @PreAuthorize("hasAuthority('CM_GROUP_ADDAUTHORITIESTOGROUP') or hasAuthority('ROLE_ADMIN')")
    void addAuthoritiesToGroup(Long groupId, List<Long> authorityIds);

    @PreAuthorize("hasAuthority('CM_GROUP_REMOVEAUTHORITYFROMGROUP') or hasAuthority('ROLE_ADMIN')")
    void removeAuthorityFromGroup(Long groupId, Long authorityId);

    @PreAuthorize("hasAuthority('CM_GROUP_REMOVEAUTHORITIESFROMGROUP') or hasAuthority('ROLE_ADMIN')")
    void removeAuthoritiesFromGroup(Long groupId, List<Long> authorityIds);
}
