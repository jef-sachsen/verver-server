package de.ul.swtp.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/permissions")
public class AuthorityController {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityController(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Authority>> getAuthorities(Pageable pageable, @RequestParam(name = "ids", required = false) List<Long> ids) {
        if(ids == null){
            Page<Authority> authorities = this.authorityRepository.findAll(pageable);
            return new ResponseEntity<>(authorities, HttpStatus.OK);
        }
        Page<Authority> authorities = this.authorityRepository.findAllByIdIn(ids, pageable);
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Authority> getAuthority(@PathVariable(name = "id") Long id) {
        Authority authority = this.authorityRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No Authority with id %s exists!", id), 1));
        return new ResponseEntity<>(authority, HttpStatus.OK);
    }
}
