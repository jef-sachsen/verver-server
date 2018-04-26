package de.ul.swtp.system;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@Scope("prototype")
public class RoleIdResolver extends SimpleObjectIdResolver {
    private final RoleManager roleManager;

    @Autowired
    public RoleIdResolver(RoleManager roleManager) {
        this.roleManager = roleManager;
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
        return roleManager.getRoleById(id);
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new RoleIdResolver(roleManager);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == RoleIdResolver.class;
    }
}
