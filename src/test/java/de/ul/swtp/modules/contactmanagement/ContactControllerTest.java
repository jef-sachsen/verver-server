package de.ul.swtp.modules.contactmanagement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ContactControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ContactRepository contactRepository;

    @Before
    public void setUp() throws Exception {
        //create sample contacts
        for(int i = 1; i < 10; ++i){
            Contact contact = new Contact();

            contact.setId(new Long(i));
            contact.setEmail("max"+i+"@mustercontact.com");
            contact.setFirstName("Max");
            contact.setLastName("Musterkontakt");
            contact.setPhone("017612"+i+"678");
            contact.setAddress("Musterkontaktstr. "+i);
            contact.setBankDetails("Musterbank " + i);

            contactRepository.save(contact);
        }
    }

    @Test
    public void contextLoads() {
        // just test if the application context loads
    }

    @Test
    public void shouldGetUnauthorizedWithoutRole() throws Exception{

        this.mockMvc.perform(get("/contacts"))
                .andExpect(status().isUnauthorized()).andDo(document("home"));
    }

    @Test
    @WithMockUser(authorities = "CM_CONTACT_GETALL")
    public void getPersonsSuccessfullyWithUserRole() throws Exception{

        this.mockMvc.perform(get("/contacts"))
                .andExpect(status().isOk()).andDo(document("home"));
    }


    public void createContact(String email, String firstName, String lastName, String phone, String address, String bankDetails) throws Exception{
        String validTestContact = "{\n" +
                "    \"email\": \""+email+"\",\n" +
                "    \"firstName\": \""+firstName+"\",\n" +
                "    \"lastName\": \""+lastName+"\",\n" +
                "    \"phone\": \""+phone+"\",\n" +
                "    \"address\": \""+address+"\",\n" +
                "    \"bankDetails\": \""+bankDetails+"\",\n" +
                "    \"groups\": null\n" +
                "}";
        //add user
        this.mockMvc
                .perform(
                        post("/contacts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validTestContact))
                .andDo(document("home"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = {"CM_CONTACT_CREATE","CM_CONTACT_GETALL"})
    public void validContactCreateTest() throws Exception{


        //get old list of user
        MvcResult response =
                this.mockMvc
                        .perform(get("/contacts"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONObject JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        JSONArray contacts = JSONResponse.getJSONArray("content");
        int oldLength = contacts.length();

        //create new User
        createContact("max@mustercontact.com","Max","Musterkontakt","017612345678","Musterkontaktstr. 123","Musterbank");

        //get new list of user
        response =
                this.mockMvc
                        .perform(get("/contacts"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        contacts = JSONResponse.getJSONArray("content");
        int newLength = contacts.length();

        //assert that there should be one more user now
        Assert.assertThat(newLength - oldLength, equalTo(1));

    }

    /**
     * calls API to get all user and returns one random user
     *
     * @return JSONObject of contact
     * @throws Exception when API response has unexpected behaviour
     */
    public JSONObject getRandomContact() throws Exception {
        //get list of user
        MvcResult response =
                this.mockMvc
                        .perform(get("/contacts"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONObject JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        JSONArray users = JSONResponse.getJSONArray("content");

        //pick random user
        Random rand = new Random();
        return users.getJSONObject(rand.nextInt(users.length() - 1));
    }


    /**
     * Picks random contact and then does API request for that contact, compares values
     *
     * @throw Exception on failure
     */
    /*@Test
    @WithMockUser(authorities = {"CM_CONTACT_GETCONTACTBYID","CM_CONTACT_GETALL"})
    public void validGetUserByIdTest() throws Exception {

        JSONObject contact = getRandomContact();
        String id = contact.getString("id");

        MvcResult response =
                this.mockMvc
                        .perform(get("/contacts/" + id))
                        .andDo(document("home"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONObject responseContact = new JSONObject(response.getResponse().getContentAsString());


        //checks every field that should be implemented according to yml
        String[] fields = {
                "id", "email", "firstName", "lastName", "phone", "address", "bankDetails"
        };
        for (int i = 0; i < fields.length; i++) {

            Assert.assertThat(responseContact.get(fields[i]), equalTo(contact.get(fields[i])));
        }
    }*/
}
