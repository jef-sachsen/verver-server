package de.ul.swtp.relationships;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/rel")
public class RelationshipController {
    private final RelationshipManager relationshipManager;
    private ObjectMapper mapper = new ObjectMapper();

    public RelationshipController(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity updateJoinUserRole(/*@RequestBody EntryWrapper entryWrapper*/) {
        /*System.out.println(json);
        ObjectNode node = mapper.readValue(json, ObjectNode.class);

        if (node.get("log") != null) {
            String log = node.get("log").textValue();
            // do something with log
            node.remove("log"); // !important
        }

        List<ImmutablePair<Long, Long>> entries = mapper.convertValue(node, ImmutablePair.class);*/
        /*List<ImmutablePair<Long, Long>> entriesAsPairs = new ArrayList<>();
        for(List<Long> entry: entries){
            entriesAsPairs.add(new ImmutablePair(entry.get(0), entry.get(1)));
        }*/
                //entries.stream().map(pairList -> new ImmutablePair(pairList.get(0), pairList.get(1))).collect(Collectors.toList());


        EntryWrapper entryWrapper = new EntryWrapper();
        List<ImmutablePair<Long, Long>> list = new ArrayList<>();
        //list.add(new ImmutablePair<Long, Long>(1L, 1L));
        list.add(new ImmutablePair<Long, Long>(1L, 2L));
        entryWrapper.setEntries(list);
        List<ImmutablePair<Long, Long>> entries = entryWrapper.getEntries();
        relationshipManager.updateJoinUserRole(entries);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<EntryWrapper> getSampleList() {
        EntryWrapper entryWrapper = new EntryWrapper();
        List<ImmutablePair<Long, Long>> list = new ArrayList<>();
        list.add(new ImmutablePair<Long, Long>(1L, 2L));
        list.add(new ImmutablePair<Long, Long>(3L, 4L));
        entryWrapper.setEntries(list);
        return new ResponseEntity<>(entryWrapper, HttpStatus.OK);
    }

    @GetMapping(value = "/lol")
    @ResponseBody
    public ResponseEntity<ImmutablePair<Long, Long>> getSamplePair() {
        return new ResponseEntity<ImmutablePair<Long, Long>>(new ImmutablePair<Long, Long>(1L, 2L), HttpStatus.OK);
    }
}
