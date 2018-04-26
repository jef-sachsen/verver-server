package de.ul.swtp.system;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RoleTest {

    private static Validator validator;
    private Role testRole = new Role();

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void testSetUp(){
        testRole.setName("validName");
    }

    @Test
    public void idNull() {
        testRole.setId(null);
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(testRole);
        assertEquals(0, constraintViolations.size());
    }

    @Ignore
    @Test
    public void idNotNull() {
        testRole.setId((long) 1234);
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(testRole);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be null",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void nameOK(){
        testRole.setName("   testName");
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(testRole);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nameNull(){
        testRole.setName(null);
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(testRole);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void nameBlank(){
        testRole.setName("     ");
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(testRole);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );

    }

}
