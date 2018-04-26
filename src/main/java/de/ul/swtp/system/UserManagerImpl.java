package de.ul.swtp.system;

import de.ul.swtp.modules.contactmanagement.Contact;
import de.ul.swtp.modules.contactmanagement.Group;
import de.ul.swtp.security.acl.CustomJdbcMutableAclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomJdbcMutableAclService customJdbcMutableAclService;

    @Autowired
    public UserManagerImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomJdbcMutableAclService customJdbcMutableAclService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customJdbcMutableAclService = customJdbcMutableAclService;
    }

    @Override
    public User create(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        //Create ACE if contact field is not null
        /*if (createdUser.getContact() != null) {
            customJdbcMutableAclService.addAceToAcl(createdUser.getContact().getId(), new PrincipalSid(createdUser.getUsername()), BasePermission.ADMINISTRATION, Contact.class);
        }*/
        return createdUser;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EmptyResultDataAccessException(String.format("No User with id %s exists!", id), 1));
    }

    @Override
    public Page<User> getUsersByIds(List<Long> userIds, Pageable pageable) {
        return userRepository.findAllByIdIn(userIds, pageable);
    }

    @Override
    public User update(Long id, User user) {
        User oldUser = getUserById(id);

        if (user.getUsername() != null) oldUser.setUsername(user.getUsername());
        if (user.getEnabled() != null) oldUser.setEnabled(user.getEnabled());
        if (user.getAdmin() != null) oldUser.setAdmin(user.getAdmin());
        if (user.getRoles() != null) oldUser.setRoles(user.getRoles());
        if (user.getGroups() != null) oldUser.setGroups(user.getGroups());
        //Only do something when user.getContact() != null
        //Delete relation when user.getContact() is 0.
        /*if (user.getContact() != null) {
            //if sent user has a contact
            if (user.getContact().getId().equals(0L) && oldUser.getContact() != null) {
                //if sent contacts id = 0 and oldUser has a contact, delete relation
                System.out.println("Access deleteAce from User 1");
                customJdbcMutableAclService.deleteAceFromAcl(oldUser.getContact().getId(), new PrincipalSid(oldUser.getUsername()), BasePermission.ADMINISTRATION, Contact.class);
                oldUser.setContact(null);
            } else if (oldUser.getId() == null || !user.getContact().getId().equals(oldUser.getContact().getId())) {
                //if id != null and id is different than before, overwrite relation
                System.out.println("Access deleteAce from User");
                //maybe check here if oldUser has a Contact
                customJdbcMutableAclService.deleteAceFromAcl(oldUser.getContact().getId(), new PrincipalSid(oldUser.getUsername()), BasePermission.ADMINISTRATION, Contact.class);
                oldUser.setContact(user.getContact());
                System.out.println("Access addAce from User");
                customJdbcMutableAclService.addAceToAcl(oldUser.getContact().getId(), new PrincipalSid(oldUser.getUsername()), BasePermission.ADMINISTRATION, Contact.class);
            }
        }*/

        return userRepository.save(oldUser);
    }

    @Override
    public User updateWithoutTouchingAcl(Long id, User user) {
        User oldUser = getUserById(id);

        if (user.getUsername() != null) oldUser.setUsername(user.getUsername());
        if (user.getEnabled() != null) oldUser.setEnabled(user.getEnabled());
        if (user.getAdmin() != null) oldUser.setAdmin(user.getAdmin());
        if (user.getRoles() != null) oldUser.setRoles(user.getRoles());
        if (user.getGroups() != null) oldUser.setGroups(user.getGroups());
        /*if (user.getContact() != null) {
            if (user.getContact().getId().equals(0L) && oldUser.getContact() != null) {
                oldUser.setContact(null);
            } else if (!user.getContact().getId().equals(oldUser.getContact().getId())) {
                oldUser.setContact(user.getContact());
            }
        }*/
        return userRepository.save(oldUser);
    }

    @Override
    public void delete(Long id) {
        User userToBeDeleted = getUserById(id);
        //customJdbcMutableAclService.deleteAceFromAcl(userToBeDeleted.getContact().getId(), new PrincipalSid(userToBeDeleted.getUsername()), BasePermission.ADMINISTRATION, Contact.class);
        userRepository.deleteById(id);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> getAllByRole(Role role) {
        return userRepository.findAllByRoles(role);
    }

    @Override
    public List<User> getAllByGroup(Group group) {
        return userRepository.findAllByGroups(group);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void enableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(Boolean.TRUE);
        userRepository.save(user);
    }

    public User signUpUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void resetPassword(String username) {
        // TODO
    }

    public void updatePassword(String password) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
        user.setPassword(passwordEncoder.encode(password));
        user.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }
}
