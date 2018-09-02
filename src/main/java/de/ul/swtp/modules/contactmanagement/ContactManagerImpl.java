package de.ul.swtp.modules.contactmanagement;

import de.ul.swtp.security.acl.CustomJdbcMutableAclService;
import de.ul.swtp.system.User;
import de.ul.swtp.system.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@Transactional
public class ContactManagerImpl implements ContactManager {

    private final ContactRepository contactRepository;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final CustomJdbcMutableAclService customJdbcMutableAclService;

    @Autowired
    public ContactManagerImpl(ContactRepository contactRepository, GroupManager groupManager, UserManager userManager, CustomJdbcMutableAclService customJdbcMutableAclService) {
        this.contactRepository = contactRepository;
        this.groupManager = groupManager;
        this.userManager = userManager;
        this.customJdbcMutableAclService = customJdbcMutableAclService;
    }

    @Override
    public Contact create(Contact contact) {
        contact.setId(null);
        Contact createdContact = contactRepository.save(contact);
        if (createdContact.getGroups() != null) {
            addContactToGroups(createdContact);
            for (Group group : createdContact.getGroups()) {
                customJdbcMutableAclService.addAceToAcl(createdContact.getId(), new GrantedAuthoritySid("CM_GROUP_" + group.getId()), PermissionEnum.resolve(group.getPermissionEnum()), Contact.class);
            }
        }
        addPermission(createdContact.getId(), new GrantedAuthoritySid("ROLE_ADMIN"), BasePermission.ADMINISTRATION);
        addPermission(createdContact.getId(), new PrincipalSid(getUsername()), BasePermission.ADMINISTRATION);

        //JPA and ACL between Contact and User
        if (contact.getUser() != null) addContactToUser(createdContact);

        return createdContact;
    }

    /**
     * Returns a contact object.
     * Throws EmptyResultDataAccessException when Contact does not exist.
     *
     * @param id
     * @return Contact object.
     */
    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No Contact with id %s exists!", id), 1));
    }

    @Override
    public Contact update(Long id, Contact contact) {
        Contact contactToBeUpdated = getContactById(id);

        //Remove this contact from all groups that have it at the moment.
        removeContactFromAllGroups(contactToBeUpdated);

        //Delete all ACEs in this contacts ACL that are specified in contactToBeUpdated.getGroups();
        //These are all ACEs in the list. Can be made way more efficient.
        for (Group group : contactToBeUpdated.getGroups()) {
            customJdbcMutableAclService.deleteAceFromAcl(id, new GrantedAuthoritySid("CM_GROUP_" + group.getId()), PermissionEnum.resolve(group.getPermissionEnum()), Contact.class);
        }

        if (contact.getEmail() != null) contactToBeUpdated.setEmail(contact.getEmail());
        if (contact.getFirstName() != null) contactToBeUpdated.setFirstName(contact.getFirstName());
        if (contact.getLastName() != null) contactToBeUpdated.setLastName(contact.getLastName());
        if (contact.getPhone() != null) contactToBeUpdated.setPhone(contact.getPhone());
        if (contact.getAddress() != null) contactToBeUpdated.setAddress(contact.getAddress());
        if (contact.getBankAccount() != null) contactToBeUpdated.setBankAccount(contact.getBankAccount());
        if (contact.getGroups() != null) contactToBeUpdated.setGroups(contact.getGroups());
        //Only do something when contact.getUser() != null
        //Delete relation when user.getContact().getId() is 0.
        /*if (contact.getUser() != null) {
            //if sent user has a contact
            if (contact.getUser().getId().equals(0L) && contactToBeUpdated.getUser() != null) {
                //if sent contacts id = 0 and oldUser has a contact, delete relation
                removeContactFromUser(contactToBeUpdated);
                contactToBeUpdated.setUser(null);
            } else if (contactToBeUpdated.getUser() == null || !contact.getUser().getId().equals(contactToBeUpdated.getUser().getId())) {
                //if id != null and id is different than before, overwrite relation
                if (contactToBeUpdated.getUser() != null) {
                    System.out.println("Access deleteAce from Contact");
                    removeContactFromUser(contactToBeUpdated);
                    //customJdbcMutableAclService.deleteAceFromAcl(contactToBeUpdated.getId(), new PrincipalSid(contactToBeUpdated.getUser().getUsername()), BasePermission.ADMINISTRATION, Contact.class);
                }
                contactToBeUpdated.setUser(contact.getUser());
                addContactToUser(contactToBeUpdated);
            }
        }*/

        Contact updatedContact = contactRepository.save(contactToBeUpdated);

        //add this Contact to all groups that are in the updated contact
        addContactToGroups(updatedContact);

        //Add ACEs that are specified in updatedContact.getGroups()
        for (Group group : updatedContact.getGroups()) {
            System.out.println("Access addAce from Contact");
            customJdbcMutableAclService.addAceToAcl(id, new GrantedAuthoritySid("CM_GROUP_" + group.getId()), PermissionEnum.resolve(group.getPermissionEnum()), Contact.class);
        }

        return updatedContact;
    }

    private void removeContactFromUser(Contact contact) {
        User user = userManager.getUserById(contact.getUser().getId());
        Contact dummyContact = new Contact();
        dummyContact.setEmail("max@mustercontact.com");
        dummyContact.setId(0L);
        user.setContact(dummyContact);
        //calling update here should not be a problem because Contact is null.
        userManager.updateWithoutTouchingAcl(user.getId(), user);
        //remove ACL entry if existent
        //System.out.println("Access deleteAce from Contact 2");
        //customJdbcMutableAclService.deleteAceFromAcl(contact.getId(), new PrincipalSid(contact.getUser().getUsername()), BasePermission.ADMINISTRATION, Contact.class);
    }

    private void addContactToUser(Contact contact) {
        User user = userManager.getUserById(contact.getUser().getId());
        user.setContact(contact);
        //Here update should not touch ACL please
        userManager.updateWithoutTouchingAcl(user.getId(), user);
        //set ACL entry
        //System.out.println("Access addAce from Contact 2");
        //customJdbcMutableAclService.addAceToAcl(contact.getId(), new PrincipalSid(contact.getUser().getUsername()), BasePermission.ADMINISTRATION, Contact.class);
    }

    private void removeContactFromAllGroups(Contact contact) {
        List<Group> groupsThatAlreadyHaveThisRole = groupManager.getAllByContacts(contact);
        for (Group group : groupsThatAlreadyHaveThisRole) {
            group.getContacts().remove(contact);
            groupManager.updateWithoutTouchingAcl(group.getId(), group);
        }
    }

    private void addContactToGroups(Contact contact) {
        for (Group group : contact.getGroups()) {
            group.getContacts().add(contact);
            groupManager.updateWithoutTouchingAcl(group.getId(), group);
        }
    }


    @Override
    public void delete(Long id) {
        ObjectIdentity oi = new ObjectIdentityImpl(Contact.class, id);
        customJdbcMutableAclService.deleteAcl(oi, false);
        Contact contact = getContactById(id);
        removeContactFromAllGroups(contact);
        //Delete this contact from its corresponding user if relation exists + remove ACE
        if (contact.getUser() != null) removeContactFromUser(contact);
        contactRepository.deleteById(id);
    }

    @Override
    public Page<Contact> getAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    @Override
    public Page<Contact> getAll(Pageable pageable, Long id) {
        Group group = groupManager.getGroupById(id);
        return contactRepository.findAllByGroups(group, pageable);
    }

    @Override
    public Page<Contact> getContactsByIds(List<Long> contactIds, Pageable pageable) {
        return contactRepository.findAllByIdIn(contactIds, pageable);
    }

    @Override
    public void addPermissionToGroups(Long contactId, Collection<Long> groupIds, Permission permission) {
        //TODO: add functionality.
    }

    @Override
    public void addPermissionToGroup(Long contactId, Long groupId, Permission permission) {
        GrantedAuthoritySid groupSid = new GrantedAuthoritySid("CM_GROUP_" + groupId);
        addPermission(contactId, groupSid, permission);
    }

    private void addPermission(Long contactId, Sid recipient, Permission permission) {
        MutableAcl acl;
        ObjectIdentity oid = new ObjectIdentityImpl(Contact.class, contactId);

        try {
            acl = (MutableAcl) customJdbcMutableAclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = customJdbcMutableAclService.createAcl(oid);
        }

        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        customJdbcMutableAclService.updateAcl(acl);

    }

    protected String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return auth.getPrincipal().toString();
        }
    }
}
