package de.ul.swtp.modules.contactmanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.Permission;

import java.util.Collection;
import java.util.List;

public interface ContactManager {
    
    //No ACL permission is required because contact is yet to be created.
    @PreAuthorize("hasAuthority('CM_CONTACT_CREATE') or hasAuthority('ROLE_ADMIN')")
    Contact create(Contact contact);

    @PreAuthorize("(hasAuthority('CM_CONTACT_GETCONTACTBYID')  or hasAuthority('ROLE_ADMIN')) and hasPermission(#id, 'de.ul.swtp.modules.contactmanagement.Contact', read)")
    Contact getContactById(Long id);

    @PreAuthorize("(hasAuthority('CM_CONTACT_UPDATE')  or hasAuthority('ROLE_ADMIN')) and hasPermission(#id, 'de.ul.swtp.modules.contactmanagement.Contact', write)")
    Contact update(Long id, Contact contact);

    @PreAuthorize("(hasAuthority('CM_CONTACT_DELETE')  or hasAuthority('ROLE_ADMIN')) and hasPermission(#id, 'de.ul.swtp.modules.contactmanagement.Contact', delete)")
    void delete(Long id);

    @PreAuthorize("(hasAuthority('CM_CONTACT_ADDPERMISSIONTOGROUP')  or hasAuthority('ROLE_ADMIN')) and hasPermission(#id, 'de.ul.swtp.modules.contactmanagement.Contact', write)")
    void addPermissionToGroup(Long contactId, Long groupId, Permission permission);

    @PreAuthorize("(hasAuthority('CM_CONTACT_ADDPERMISSIONTOGROUPS')  or hasAuthority('ROLE_ADMIN')) and hasPermission(#id, 'de.ul.swtp.modules.contactmanagement.Contact', write)")
    void addPermissionToGroups(Long contactId, Collection<Long> groupIds, Permission permission);

    /**
     * Returns a page of contacts.
     * @param pageable
     * @return
     */
    @PreAuthorize("hasAuthority('CM_CONTACT_GETALL') or hasAuthority('ROLE_ADMIN')")
    //@PostFilter("hasPermission(filterObject, read)")
    Page<Contact> getAll(Pageable pageable);

    /**
     * Returns a page of contacts that are assigned to the role specified by the given id.
     * @param pageable
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('CM_CONTACT_GETALL') or hasAuthority('ROLE_ADMIN')")
    //@PostFilter("hasPermission(filterObject, read)")
    Page<Contact> getAll(Pageable pageable, Long id);

    @PreAuthorize("hasAuthority('CM_CONTACT_GETCONTACTSBYIDS') or hasAuthority('ROLE_ADMIN')")
    //@PostFilter("hasPermission(filterObject, read)")
    Page<Contact> getContactsByIds(List<Long> contactIds, Pageable pageable);
}
