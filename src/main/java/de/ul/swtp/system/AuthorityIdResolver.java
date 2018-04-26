package de.ul.swtp.system;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@Scope("prototype")
public class AuthorityIdResolver extends SimpleObjectIdResolver {
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityIdResolver(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
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
        return authorityRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No Authority with id %s exists!", id), 1));
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new AuthorityIdResolver(authorityRepository);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == AuthorityIdResolver.class;
    }
}
