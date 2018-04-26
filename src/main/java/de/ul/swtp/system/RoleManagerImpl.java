package de.ul.swtp.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager expects magically validated objects atm.
 * Validated means 1.) formally correct and 2.) integritally (e.g. DB Checks) correct.
 */
@Component
public class RoleManagerImpl implements RoleManager {
    private final RoleRepository roleRepository;
    private final UserManager userManager;

    @Autowired
    public RoleManagerImpl(RoleRepository roleRepository, UserManager userManager) {
        this.roleRepository = roleRepository;
        this.userManager = userManager;
    }

    @Override
    public Role create(Role role) {
        role.setId(null);
        Role createdRole = roleRepository.save(role);
        if (createdRole.getUsers() != null) addRoleToUsers(createdRole);
        return createdRole;
    }

    @Override
    public Role create(Role role, List<Authority> authorities) {
        //TODO: validate that id field is empty
        role.setId(null);
        Role createdRole = roleRepository.save(role);
        List<Long> authorityIds = authorities.stream().map(Authority::getId).collect(Collectors.toList());
        return addAuthoritiesToRole(createdRole.getId(), authorityIds);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No Role with id %s exists!", id), 1));
    }

    @Override
    public Page<Role> getRolesByIds(List<Long> roleIds, Pageable pageable) {
        //Does not throw not found, when there is a role in the list that is not in the db.
        return roleRepository.findAllByIdIn(roleIds, pageable);
    }

    @Override
    public Role update(Long id, Role role) {
        Role roleToBeUpdated = getRoleById(id);

        //JPA
        //remove this role from all users that have it at the moment.
        removeRoleFromAllUsers(roleToBeUpdated);

        //id of given role is ignored.
        if (role.getName() != null) roleToBeUpdated.setName(role.getName());
        if (role.getAuthorities() != null) roleToBeUpdated.setAuthorities(role.getAuthorities());
        if (role.getUsers() != null) roleToBeUpdated.setUsers(role.getUsers());
        System.out.println(roleToBeUpdated.toString());
        Role updatedRole = roleRepository.save(roleToBeUpdated);

        //JPA
        //add this role to all users that are in the updated role
        addRoleToUsers(updatedRole);


        return updatedRole;
    }

    private void removeRoleFromAllUsers(Role role) {
        List<User> usersThatAlreadyHaveThisRole = userManager.getAllByRole(role);
        for (User user : usersThatAlreadyHaveThisRole) {
            user.getRoles().remove(role);
            userManager.update(user.getId(), user);
        }
    }

    private void addRoleToUsers(Role role) {
        for (User user : role.getUsers()) {
            user.getRoles().add(role);
            userManager.update(user.getId(), user);
        }
    }

    @Override
    public void delete(Long roleId) {
        removeRoleFromAllUsers(getRoleById(roleId));
        roleRepository.deleteById(roleId);
    }

    @Override
    public Page<Role> getAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }


    @Override
    public List<User> findUsersInRole(Long id) {
        return getRoleById(id).getUsers();
    }


    @Override
    public void addUserToRole(Role role, User user) {
        List<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        //userManager.update(user.getId(), user);
    }

    @Override
    public void addUsersToRole(Role role, List<User> users) {
        for (User user : users) {
            addUserToRole(role, user);
        }
    }

    @Override
    public void removeUserFromRole(Role role, User user) {
        List<Role> roles = user.getRoles();
        //if (roles.contains(role)) {
        roles.remove(role);
        //}
        user.setRoles(roles);
        //userManager.update(user.getId(), user);
    }

    @Override
    public void removeUsersFromRole(Role role, List<User> users) {
        for (User user : users) {
            removeUserFromRole(role, user);
        }
    }

    @Override
    public void removeAllUsersFromRole(Role role) {
        for (User user : role.getUsers()) {
            removeUserFromRole(role, user);
        }
    }

    @Override
    public Role addAuthorityToRole(Long roleId, Long authorityId) {
        return new Role();
    }

    @Override
    public Role addAuthoritiesToRole(Long roleId, List<Long> authorityIds) {
        Role role = getRoleById(roleId);
        for (Long authorityId : authorityIds) {
            role = addAuthorityToRole(roleId, authorityId);
        }
        return role;
    }

    @Override
    public void removeAuthorityFromRole(Long roleId, Long authorityId) {

    }

    @Override
    public void removeAuthoritiesFromRole(Long roleId, List<Long> authorityIds) {
        for (Long authorityId : authorityIds) {
            removeAuthorityFromRole(roleId, authorityId);
        }
    }
}
