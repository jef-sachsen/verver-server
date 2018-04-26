package de.ul.swtp.modules.contactmanagement;

import de.ul.swtp.modules.contactmanagement.Group;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GroupTest {
    private static Validator validator;
    private Group testGroup = new Group();

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // A valid test group is created whose attributes are then changed in the tests to render it invalid
    @Before
    public void testSetUp(){
        testGroup.setName("validGroupName");
        testGroup.setPermissionEnum(PermissionEnum.DELETE);
    }

    @Test
    public void defaultsOK() {
        Set<ConstraintViolation<Group>> constraintViolations = validator.validate(testGroup);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nameNull() {
        testGroup.setName(null);
        Set<ConstraintViolation<Group>> constraintViolations = validator.validate(testGroup);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void nameBlank() {
        testGroup.setName("  ");
        Set<ConstraintViolation<Group>> constraintViolations = validator.validate(testGroup);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void permissionsEnumNull() {
        testGroup.setPermissionEnum(null);
        Set<ConstraintViolation<Group>> constraintViolations = validator.validate(testGroup);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be null",
                constraintViolations.iterator().next().getMessage()
        );
    }
}
