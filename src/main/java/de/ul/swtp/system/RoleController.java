package de.ul.swtp.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/roles")
public class RoleController {

    private RoleManager roleManager;

    @Autowired
    public RoleController(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Role>> getRoles(Pageable pageable, @RequestParam(name = "ids", required = false) List<Long> ids) {
        if(ids == null){
            Page<Role> roles = roleManager.getAll(pageable);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
        Page<Role> roles = roleManager.getRolesByIds(ids, pageable);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleManager.create(role);
        return new ResponseEntity<>(createdRole, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Role> getRole(@PathVariable("id") Long id) {
        Role role = roleManager.getRoleById(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Role> updateRole(@RequestBody Role role, @PathVariable("id") Long id) {
        Role updatedRole = roleManager.update(id, role);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Role> deleteRole(@PathVariable("id") Long id) {
        Role deletedRole = roleManager.getRoleById(id);
        roleManager.delete(id);
        return new ResponseEntity<>(deletedRole, HttpStatus.OK);
    }
}
