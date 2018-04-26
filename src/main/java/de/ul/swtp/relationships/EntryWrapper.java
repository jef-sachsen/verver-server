package de.ul.swtp.relationships;

import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

@Data
public class EntryWrapper {

    private List<ImmutablePair<Long, Long>> entries;
}

