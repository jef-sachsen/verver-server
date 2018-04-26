package de.ul.swtp.system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/users")
public class UserController {
    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<User>> getUsers(Pageable pageable, @RequestParam(name = "ids", required = false) List<Long> ids) {
        if(ids == null){
            Page<User> users = userManager.getAll(pageable);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        Page<User> users = userManager.getUsersByIds(ids, pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody User user) {
        //TODO: validate that id field is empty
        User createdUser = userManager.create(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        //TODO: Should only return {id, username, permissionId[], contactId[]} of the user.
        User user = userManager.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") Long id) {
        User updatedUser = userManager.update(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        User deletedUser = userManager.getUserById(id);
        userManager.delete(id);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);

    }
}
