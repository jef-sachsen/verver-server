package de.ul.swtp.modules.contactmanagement;

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

public class ContactTest {
    private static Validator validator;
    private Contact testContact = new Contact();

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // A valid test contact is created whose attributes are then changed in the tests to render it invalid
    @Before
    public void testSetUp(){
        testContact.setFirstName("validFirstName");
        testContact.setLastName("validLastName");
        testContact.setAddress("Teststr. 12 04123 Leipzig");
    }

    @Test
    public void defaultsOK() {
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void idNull() {
        testContact.setId(null);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    @Ignore
    @Test
    public void idNotNull() {
        testContact.setId((long) 1234);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be null",
                constraintViolations.iterator().next().getMessage()
        );
    }

    //TODO write a more comprehensive set of test for email address verification

    @Test
    public void validEmail1() {
        testContact.setEmail("testemailaddress@test.com");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void validEmail2() {
        testContact.setEmail("testemail+address@test.com");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    //TODO Test blank and null email addresses
    @Test
    public void invalidEmail1() {
        testContact.setEmail("test@emailaddress@test.com");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidEmail2() {
        testContact.setEmail("waaaaaaaaaaaaaaaaaaaaaaaytooooooooooooooooooooooloooooooooooooooooooooong@gmail.com");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidEmail3() {
        testContact.setEmail("testemailaddresstest.com");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidEmail4() {
        testContact.setEmail("test\\emailaddress@test");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }


    @Test
    public void invalidEmail5() {
        testContact.setEmail("testemailaddress@test");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be a well-formed email address",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void firstNameBlank() {
        testContact.setFirstName("    ");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void firstNameNull() {
        testContact.setFirstName(null);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void lastNameBlank() {
        testContact.setLastName("    ");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void lastNameNull() {
        testContact.setFirstName(null);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void validPhoneNr1() {
        testContact.setPhone("123456789");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    // I wouldn't have expected this test to pass with the @Digit annotation
    @Test
    public void validPhoneNr2() {
        testContact.setPhone("+4912345678");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    // It would be good to accept phone numbers in common formats
    @Ignore
    @Test
    public void validPhoneNr3() {
        testContact.setPhone("12 345 6789");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    // It's important to note that at the moment phone numbers are optional, this may not be desirable.
    @Test
    public void nullPhoneValid() {
        testContact.setPhone(null);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void invalidPhoneNr1() {
        // too long
        testContact.setPhone("041234567890123456789");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "numeric value out of bounds (<16 digits>.<0 digits> expected)",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidPhoneNr2() {
        testContact.setPhone("zerofourone");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "numeric value out of bounds (<16 digits>.<0 digits> expected)",
                constraintViolations.iterator().next().getMessage()
        );
    }

    // That this test fails demonstrates that a better solution than @Digits is needed
    @Ignore
    @Test
    public void invalidPhoneNr3() {
        testContact.setPhone("-123456789");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "numeric value out of bounds (<16 digits>.<0 digits> expected)",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void invalidPhoneNr4() {
        testContact.setPhone("++-123456");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "numeric value out of bounds (<16 digits>.<0 digits> expected)",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void blankPhoneInvalid() {
        testContact.setPhone("");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "numeric value out of bounds (<16 digits>.<0 digits> expected)",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void addressNull() {
        testContact.setAddress(null);
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }

    @Test
    public void addressBlank() {
        testContact.setAddress("    ");
        Set<ConstraintViolation<Contact>> constraintViolations = validator.validate(testContact);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must not be blank",
                constraintViolations.iterator().next().getMessage()
        );
    }
}
