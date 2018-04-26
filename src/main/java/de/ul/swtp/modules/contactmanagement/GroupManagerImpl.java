package de.ul.swtp.modules.contactmanagement;

import de.ul.swtp.security.acl.CustomJdbcMutableAclService;
import de.ul.swtp.system.User;
import de.ul.swtp.system.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupManagerImpl implements GroupManager {

    private final GroupRepository groupRepository;
    private final UserManager userManager;
    private final CustomJdbcMutableAclService customJdbcMutableAclService;

    @Autowired
    public GroupManagerImpl(GroupRepository groupRepository, UserManager userManager, CustomJdbcMutableAclService customJdbcMutableAclService) {
        this.groupRepository = groupRepository;
        this.userManager = userManager;
        this.customJdbcMutableAclService = customJdbcMutableAclService;
    }

    @Override
    public Group create(Group group) {
        Group createdGroup = groupRepository.save(group);
        if (createdGroup.getUsers() != null) addGroupToUsers(createdGroup);
        Long generatedId = createdGroup.getId();
        Long groupAclSidId = customJdbcMutableAclService.createGrantedAuthoritySidForGroup(generatedId);
        createdGroup.setAclSidId(groupAclSidId);

        if (createdGroup.getContacts() != null) {
            for (Contact contact : createdGroup.getContacts()) {
                customJdbcMutableAclService.addAceToAcl(contact.getId(), new GrantedAuthoritySid("CM_GROUP_" + createdGroup.getId()), PermissionEnum.resolve(createdGroup.getPermissionEnum()), Contact.class);
            }
        }
        return groupRepository.save(createdGroup);
    }

    @Override
    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No Group with id %s exists!", id), 1));
    }

    @Override
    public Page<Group> getGroupsByIds(List<Long> groupIds, Pageable pageable) {
        return groupRepository.findAllByIdIn(groupIds, pageable);
    }

    @Override
    public Group update(Long id, Group group) {
        Group groupToBeUpdated = getGroupById(id);

        //JPA
        //remove this group from all users that have it at the moment.
        removeGroupFromAllUsers(groupToBeUpdated);

        //ACL
        //delete this groups ACE from all contacts.
        for (Contact contact : groupToBeUpdated.getContacts()) {
            customJdbcMutableAclService.deleteAceFromAcl(contact.getId(), new GrantedAuthoritySid("CM_GROUP_" + id), PermissionEnum.resolve(groupToBeUpdated.getPermissionEnum()), Contact.class);
        }

        //Main update, this also handles JPA for group <-> contact.
        if (group.getName() != null) groupToBeUpdated.setName(group.getName());
        if (group.getUsers() != null) groupToBeUpdated.setUsers(group.getUsers());
        if (group.getContacts() != null) groupToBeUpdated.setContacts(group.getContacts());
        if (group.getResponsibles() != null) groupToBeUpdated.setResponsibles(group.getResponsibles());
        if (group.getPermissionEnum() != null) groupToBeUpdated.setPermissionEnum(group.getPermissionEnum());

        Group updatedGroup = groupRepository.save(groupToBeUpdated);

        //JPA
        //add this group to all users that are in the updated group
        addGroupToUsers(updatedGroup);

        //ACL
        //add ACEs back to the contacts in the contact list.
        for (Contact contact : updatedGroup.getContacts()) {
            customJdbcMutableAclService.addAceToAcl(contact.getId(), new GrantedAuthoritySid("CM_GROUP_" + updatedGroup.getId()), PermissionEnum.resolve(updatedGroup.getPermissionEnum()), Contact.class);
        }

        return updatedGroup;
    }

    @Override
    public Group updateWithoutTouchingAcl(Long id, Group group) {
        Group groupToBeUpdated = getGroupById(id);

        //JPA
        //remove this group from all users that have it at the moment.
        removeGroupFromAllUsers(groupToBeUpdated);

        //Main update, this also handles JPA for group <-> contact.
        if (group.getName() != null) groupToBeUpdated.setName(group.getName());
        if (group.getUsers() != null) groupToBeUpdated.setUsers(group.getUsers());
        if (group.getContacts() != null) groupToBeUpdated.setContacts(group.getContacts());
        if (group.getPermissionEnum() != null) groupToBeUpdated.setPermissionEnum(group.getPermissionEnum());

        Group updatedGroup = groupRepository.save(groupToBeUpdated);

        //JPA
        //add this group to all users that are in the updated group
        addGroupToUsers(updatedGroup);

        return updatedGroup;
    }

    private void removeGroupFromAllUsers(Group group) {
        List<User> usersThatAlreadyHaveThisGroup = group.getUsers();
        for (User user : usersThatAlreadyHaveThisGroup) {
            user.getGroups().remove(group);
            userManager.update(user.getId(), user);
        }
    }

    private void addGroupToUsers(Group group) {
        for (User user : group.getUsers()) {
            if (group.getUsers().contains(user) && !user.getGroups().contains(group)) {
                user.getGroups().add(group);
                userManager.update(user.getId(), user);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Long groupAclSidId = getGroupById(id).getAclSidId();
        customJdbcMutableAclService.deleteAclSidByAclSidId(groupAclSidId);
        removeGroupFromAllUsers(getGroupById(id));
        groupRepository.deleteById(id);
    }

    @Override
    public Page<Group> getAll(Pageable pageable) {
        return groupRepository.findAll(pageable);
    }

    @Override
    public List<Group> getAllByContacts(Contact contact) {
        return groupRepository.findAllByContacts(contact);
    }

    @Override
    public List<User> findUsersInGroup(Long id) {
        return null;
    }

    @Override
    public void addUserToGroup(Long userId, Long groupId) {

    }

    @Override
    public void removeUserFromGroup(Long userId, Long groupId) {

    }

    @Override
    public void addUsersToGroup(Long groupId, List<Long> userIds) {

    }

    @Override
    public void removeUsersFromGroup(Long groupId, List<Long> userIds) {

    }

    @Override
    public void addAuthorityToGroup(Long groupId, Long authorityId) {

    }

    @Override
    public void addAuthoritiesToGroup(Long groupId, List<Long> authorityIds) {

    }

    @Override
    public void removeAuthorityFromGroup(Long groupId, Long authorityId) {

    }

    @Override
    public void removeAuthoritiesFromGroup(Long groupId, List<Long> authorityIds) {

    }
}
