package de.ul.swtp.relationships;

import de.ul.swtp.modules.contactmanagement.ContactManager;
import de.ul.swtp.modules.contactmanagement.GroupManager;
import de.ul.swtp.system.Role;
import de.ul.swtp.system.RoleManager;
import de.ul.swtp.system.User;
import de.ul.swtp.system.UserManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RelationshipManagerImpl implements RelationshipManager {

    private final RoleManager roleManager;
    private final UserManager userManager;
    private final GroupManager groupManager;
    private final ContactManager contactManager;

    @Autowired
    public RelationshipManagerImpl(RoleManager roleManager, UserManager userManager, GroupManager groupManager, ContactManager contactManager) {
        this.roleManager = roleManager;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.contactManager = contactManager;
    }

    @Override
    public void updateJoinRoleAuthority(List<ImmutablePair<Long, Long>> entries) {

    }

    @Override
    public void updateJoinUserRole(List<ImmutablePair<Long, Long>> entries) {
        //UserId, RoleId
        //This is brute force updating inside of the user and group objects (delete all and save new).
        //get lists of users and roles
        Set<User> users = entries.stream().map(entry -> userManager.getUserById(entry.getLeft())).collect(Collectors.toSet());
        Set<Role> roles = entries.stream().map(entry -> roleManager.getRoleById(entry.getRight())).collect(Collectors.toSet());
        //empty the lists
        for (User user : users) {
            user.setRoles(new ArrayList<Role>());
        }
        for (Role role : roles) {
            role.setUsers(new ArrayList<User>());
        }
        //Build list of users with correct roleIds and vice versa
        for (ImmutablePair<Long, Long> entry : entries) {
            for (User user : users) {
                if (user.getId().equals(entry.getLeft())){
                    List<Role> r = user.getRoles();
                    r.add(roleManager.getRoleById(entry.getRight()));
                    user.setRoles(r);
                }
            }
            for (Role role : roles) {
                if (role.getId().equals(entry.getRight())){
                    List<User> u = role.getUsers();
                    u.add(userManager.getUserById(entry.getLeft()));
                    role.setUsers(u);
                }
            }
        }

        //update all users and roles
        for (User user:users){
            userManager.update(user.getId(), user);
        }
        for (Role role: roles){
            roleManager.update(role.getId(), role);
        }
        //for each pair:
        //get User and Group, update user and Group, save.
    }

    @Override
    public void updateJoinUserGroup() {

    }

    @Override
    public void updateJoinGroupContact() {

    }
}
