package de.ul.swtp.system;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private static Validator validator;
    private User testUser = new User();

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // A valid test user is created whose attributes are then changed in the tests to render it invalid
    @Before
    public void testSetUp(){
        testUser.setUsername("validUsername@test.de");
        testUser.setPassword("validPassword");
        testUser.setEnabled(true);
        testUser.setAdmin(false);
    }

    //Validation tests
    //TODO check the constraintViolation messages to ensure the expected validations are failing
    //TODO work out how to check multiple messages
    // check multiple messages https://stackoverflow.com/questions/41941404/test-multiple-constraint-violation-return-messages

    @Test
    public void defaultsOK() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void idNull() {
        testUser.setId(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Ignore
    @Test
    public void idNotNull() {
        testUser.setId((long) 1234);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
            "must be null",
            constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void usernameBlank1() {
        testUser.setUsername("");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(2, constraintViolations.size());
    }


    @Test
    public void usernameBlank2() {
        testUser.setUsername("     ");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(2, constraintViolations.size());
        /*
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
        */

    }

    @Test
    public void usernameNull() {
        testUser.setUsername(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void validUsername1() {
        testUser.setUsername("testemailaddress@test.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void validUsername2() {
        testUser.setUsername("testemail+address@test.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void invalidUsername1() {
        testUser.setUsername("test@emailaddress@test.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidUsername2() {
        testUser.setUsername("testemailaddresstest.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidUsername3() {
        testUser.setUsername("testemail@@addresstest.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void usernameTooLongForAnEmailAddress() {
        testUser.setUsername("waaaaaaaaaaaaaaaaaaaaaaaytooooooooooooooooooooooloooooooooooooooooooooong@gmail.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void usernameHasNoTopLevelDomain() {
        testUser.setUsername("testemailaddress@test");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void passwordTooShort() {
        testUser.setPassword("aa");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void passwordTooShortAndEmpty() {
        testUser.setPassword("");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void passwordNull() {
        testUser.setPassword(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void enabledNull() {
        testUser.setEnabled(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void enabledFalse() {
        testUser.setEnabled(false);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void adminNull() {
        testUser.setAdmin(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void adminTrue() {
        testUser.setAdmin(true);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void lastPasswordResetDateInTheFuture() {
        Calendar c = Calendar.getInstance();
        // add one day to the current date
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        testUser.setLastPasswordResetDate(tomorrow);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(testUser);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a past date",
                constraintViolations.iterator().next().getMessage()
        );
    }
}