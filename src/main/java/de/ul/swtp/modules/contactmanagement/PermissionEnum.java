package de.ul.swtp.modules.contactmanagement;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.util.Assert;

public enum PermissionEnum {
    READ, WRITE, CREATE, DELETE, ADMINISTRATION;

    public static Permission resolve(PermissionEnum permissionEnum) throws EmptyResultDataAccessException {
        Assert.notNull(permissionEnum, "PermissionEnum must not be null!");
        switch (permissionEnum) {
            case READ:
                return BasePermission.READ;
            case WRITE:
                return BasePermission.WRITE;
            case CREATE:
                return BasePermission.CREATE;
            case DELETE:
                return BasePermission.READ;
            case ADMINISTRATION:
                return BasePermission.ADMINISTRATION;
            default:
                throw new EmptyResultDataAccessException("This Group does not have a Permission! Add that first!", 1);
        }
    }
}
