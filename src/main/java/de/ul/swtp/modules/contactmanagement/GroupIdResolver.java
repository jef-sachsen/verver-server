package de.ul.swtp.modules.contactmanagement;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@Scope("prototype")
public class GroupIdResolver extends SimpleObjectIdResolver {
    private final GroupManager groupManager;

    @Autowired
    public GroupIdResolver(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public void bindItem(IdKey id, Object pojo) {
        super.bindItem(id, pojo);
    }

    @Override
    public Object resolveId(IdKey id) {
        Object resolved = super.resolveId(id);
        if (resolved == null) {
            resolved = _tryToLoadFromSource(id);
            bindItem(id, resolved);
        }

        return resolved;
    }

    private Object _tryToLoadFromSource(IdKey idKey) {
        requireNonNull(idKey.scope, "Global scope not supported.");
        Long id = (Long) idKey.key;
        return groupManager.getGroupById(id);
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new GroupIdResolver(groupManager);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == GroupIdResolver.class;
    }
}

