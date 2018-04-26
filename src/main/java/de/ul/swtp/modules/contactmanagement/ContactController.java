package de.ul.swtp.modules.contactmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/contacts")
public class ContactController {

    private ContactManager contactManager;

    @Autowired
    public ContactController(ContactManager contactManager) {
        this.contactManager = contactManager;
    }


    /**
     * Endpoint to retrieve a page of users.
     * Takes parameters page={pageId}, size={sizeOfPage} and sort={sortCriteria},{sortDirection}.
     * {sortCriteria} has to be an attribute of the Contact object.
     * {sortDirection} has to either be "asc" or "desc".
     * The results can be sorted by multiple criteria by adding more sort={sortCriteria},{sortDirection} parameters to the URI where the first sort parameter has the highest priority..
     *
     * @param pageable gets created by Spring DATA Rest from the parameters passed in the URI.
     * @return Page of Contacts.
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Contact>> getContacts(Pageable pageable, @RequestParam(name = "group") Optional<Long> id, @RequestParam(name = "ids", required = false) List<Long> ids) {
        if (id.isPresent()) {
            Page<Contact> contacts = this.contactManager.getAll(pageable, id.get());
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } else if (ids != null) {
            Page<Contact> contacts = this.contactManager.getContactsByIds(ids, pageable);
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        }
        Page<Contact> contacts = this.contactManager.getAll(pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);

    }

    /**
     * Endpoint to create a new contact.
     *
     * @param contact
     * @return
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        //TODO: validate that id field is empty
        Contact createdContact = contactManager.create(contact);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve the user with the specified id.
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Contact> getContact(@PathVariable("id") Long id) {
        Contact contact = contactManager.getContactById(id);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    /**
     * Endpoint to update the user with the id specified in the URI.
     * The id inside of the passed contact object will be ignored.
     *
     * @param contact New contact information.
     * @param id      User to be updated.
     * @return
     */
    @PutMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Contact> updateContact(@RequestBody Contact contact, @PathVariable("id") Long id) {
        Contact updatedContact = contactManager.update(id, contact);
        return new ResponseEntity<>(updatedContact, HttpStatus.OK);
    }


    /**
     * Endpoint to delete the user with the specified id.
     *
     * @param id Id of the user to be deleted.
     * @return Deleted user object.
     */
    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity/*<Contact>*/ deleteContact(@PathVariable("id") Long id) {
        //TODO: Should return deletedContact, but as this one is changed by delete, a 500 error is generated.
        //Contact deletedContact = contactManager.getContactById(id);
        contactManager.delete(id);
        return new ResponseEntity<>(/*deletedContact, */HttpStatus.OK);
    }
}
