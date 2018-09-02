package de.ul.swtp.security.acl;

import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;


public class CustomJdbcMutableAclService extends JdbcMutableAclService {


    public CustomJdbcMutableAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    public void deleteAclSidByAclSidId(Long id) {
        String deleteAclSidByAclSidId = "DELETE FROM acl_sid WHERE id = ?";
        jdbcTemplate.update(deleteAclSidByAclSidId, id);
    }

    public Long createGrantedAuthoritySidForGroup(Long id) {
        String groupName = "CM_GROUP_" + id;
        System.out.println("Group name: " + groupName);
        String createGrantedAuthoritySidForGroup = "INSERT INTO acl_sid (principal, sid) VALUES (FALSE, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(createGrantedAuthoritySidForGroup, Types.VARCHAR);
        factory.setReturnGeneratedKeys(true);
        jdbcTemplate.update(factory.newPreparedStatementCreator(new Object[]{groupName}), keyHolder);
        System.out.println("keyHolder: " + keyHolder.getKey());
        return (Long) keyHolder.getKey();
    }

    public void addAceToAcl(Long id, Sid recipient, Permission permission, Class clazz) {
        MutableAcl acl;
        ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);

        try {
            acl = (MutableAcl) readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = createAcl(oid);
        }

        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        System.out.println("Added ACE: " + acl.getEntries().get(acl.getEntries().size() - 1));
        updateAcl(acl);
    }

    public void deleteAceFromAcl(Long id, Sid sid, Permission permission, Class clazz) {
        ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
        MutableAcl acl = (MutableAcl) readAclById(oid);
        List<AccessControlEntry> aces = acl.getEntries();
        System.out.println(aces.size()-1);
        for (int i = aces.size()-1; i >= 0; i--) {
            System.out.println(aces);
            if (aces.get(i).getSid().equals(sid)
                    && aces.get(i).getPermission().equals(permission)) {
                System.out.println("Deleting ACE: " + aces.get(i));
                acl.deleteAce(i);

            }
        }
        updateAcl(acl);
    }

}
