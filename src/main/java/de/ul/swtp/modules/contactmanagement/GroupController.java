package de.ul.swtp.modules.contactmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/groups")
public class GroupController {

    private final GroupManager groupManager;

    @Autowired
    public GroupController(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Group>> getGroups(Pageable pageable, @RequestParam(name = "ids", required = false) List<Long> ids) {
        if(ids == null){
            Page<Group> groups = groupManager.getAll(pageable);
            return new ResponseEntity<>(groups, HttpStatus.OK);
        }
        Page<Group> groups = groupManager.getGroupsByIds(ids, pageable);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        //TODO: validate that id field is empty
        Group createdGroup = groupManager.create(group);
        return new ResponseEntity<>(createdGroup, HttpStatus.OK);
    }


    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Group> getGroup(@PathVariable("id") Long id) {
        //TODO: Should only return {name, id, contactId[], userId[]} of the group.
        Group group = groupManager.getGroupById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Group> updateGroup(@RequestBody Group group, @PathVariable("id") Long id) {
        Group createdGroup = groupManager.update(id, group);
        return new ResponseEntity<>(createdGroup, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Group> deleteGroup(@PathVariable("id") Long id) {
        Group deletedGroup = groupManager.getGroupById(id);
        groupManager.delete(id);
        return new ResponseEntity<>(deletedGroup, HttpStatus.OK);
    }
}
