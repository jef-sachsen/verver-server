package de.ul.swtp.relationships;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public interface RelationshipManager {
    /*TODO: 1st implement pair/matrix
    2nd implement updating owning side to update jointables
    3rd implement both sides to update jointables
    4th figure out how to handle ACL with this manager.
    */

    /**
     * Updates (overwrites) the current Jointable.
     * @param entries List of Tuples: List[Long roleId, Long authorityId]
     */
    void updateJoinRoleAuthority(List<ImmutablePair<Long, Long>> entries);

    /**
     * Updates (overwrites) the current Jointable.
     * Pair can probably be changed to ImmutablePair
     * @param entries List of Tuples: List[Long userId, Long roleId]
     */
    void updateJoinUserRole(List<ImmutablePair<Long, Long>> entries);
    void updateJoinUserGroup();
    void updateJoinGroupContact();

    //void updateRolesOfUser(User user);
    //TODO: Jackson parse id to object, impl update methods

}
