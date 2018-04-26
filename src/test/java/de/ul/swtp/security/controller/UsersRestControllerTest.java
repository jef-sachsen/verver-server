package de.ul.swtp.security.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class UsersRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*@MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;
    */

    @Test
    public void shouldGetUnauthorizedWithoutAuthority() throws Exception {

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized()).andDo(document("home"));
    }

    @Test
    @WithMockUser(authorities = "SYS_USER_GETALL")
    public void getPersonsSuccessfullyWithAuthority() throws Exception {

        /*User user = new User();
        user.setUsername("username");
        user.setEnabled(Boolean.TRUE);
        user.setIsAdmin(Boolean.FALSE);
        user.setLastPasswordResetDate(new Date(System.currentTimeMillis() + 1000 * 1000));

        JwtUser jwtUser = JwtUserFactory.create(user);

        when(jwtTokenUtil.getUsernameFromToken(any())).thenReturn(user.getUsername());

        when(jwtUserDetailsService.loadUserByUsername(eq(user.getUsername()))).thenReturn(jwtUser);

        mockMvc.perform(get("/users").header("Authorization", "Bearer nsodunsodiuv"))
                .andExpect(status().is2xxSuccessful());*/

        mockMvc.perform(get("/users")).andExpect(status().isOk()).andDo(document("home"));

    }


    /**
     * issue # 180
     * when posting user with password but without the admin attribute, the integrity of the db is lost und the /users endpoint gives a permanent 500 error
     * this is the test that should pass after fixing this issue
     * @throws Exception if API does not give an error for the second call
     */
    @Test
    @Ignore
    @WithMockUser(authorities = {"SYS_USER_GETALL","SYS_USER_CREATE"})
    public void invalidJSONCreatesPermanent500Test() throws Exception{
        String invalidTestUser =
                "{\n" +
                        "\t\t\t\"username\": \"maria@swt.de\",\n" +
                        "\t\t\t\"password\": \"password\",\n" +
                        "            \"enabled\": true\n" +
                        "}";

        MvcResult result = this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTestUser))
                .andReturn();


        Assert.assertNotEquals(result.getResponse().getStatus(),500);

        result = this.mockMvc
                .perform(get("/users"))
                .andReturn();

        Assert.assertNotEquals(result.getResponse().getStatus(),500);

    }

    /**
     * tries to create a user twice
     * @throws Exception if API does not give an error for the second call
     */
    @Test
    @WithMockUser(authorities = "SYS_USER_CREATE")
     public void invalidDuplicateUserCreateTest() throws Exception{
        String validTestUser = getValidTestUserJSON("paul");

        //add one user
        this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTestUser))
                .andDo(document("home"))
                .andExpect(status().isCreated());

         //add second user with same information supplied
        this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTestUser))
                .andDo(document("home"))
                .andExpect(status().is4xxClientError());
     }


    /**
     * calls API to get all user and returns one random user
     *
     * @return JSONObject of user
     * @throws Exception when API response has unexpected behaviour
     */
    public JSONObject getRandomUser() throws Exception {
        //get list of user
        MvcResult response =
                this.mockMvc
                        .perform(get("/users"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONObject JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        JSONArray users = JSONResponse.getJSONArray("content");

        //pick random user
        Random rand = new Random();
        return users.getJSONObject(rand.nextInt(users.length() - 1));
    }

    /**
     *
     */
    public String getValidTestUserJSON(String name){
        return  "{\n" +
                        "\t\t\t\"username\": \""+ name +"@swt.de\",\n" +
                        "\t\t\t\"password\": \"password\",\n" +
                        "            \"enabled\": true,\n" +
                        "            \"admin\": false\n" +
                        "}";
    }

    /**
     * Picks random user and deletes it, checks whether user request afterwards gives 404
     *
     * @throws Exception on failure
     */
    @Test
    @WithMockUser(authorities = {"SYS_USER_GETALL","SYS_USER_DELETE","SYS_USER_GETUSERBYID"})
    public void validUserDeleteTest() throws Exception {
        JSONObject user = getRandomUser();
        String id = user.getString("id");
        this.mockMvc
            .perform(
                delete("/users/" + id)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("home"))
                .andExpect(status().isOk());
        this.mockMvc
            .perform(
                get("/users/" + id)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("home"))
                .andExpect(status().is(404));
    }

    /**
     * tests whether single user can be created and whether number of user has increased by one as a
     * result
     *
     * @throws Exception on failure
     */
      @Test
      @WithMockUser(authorities = {"SYS_USER_CREATE","SYS_USER_GETALL"})
      public void validUserCreateTest() throws Exception {

        String validTestUser = getValidTestUserJSON("anna");
        //get old list of user
        MvcResult response =
            this.mockMvc
                .perform(get("/users"))
                    .andExpect(status().isOk())
                    .andReturn();

        JSONObject JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        int oldLength = JSONResponse.getInt("totalElements");

        //add user
        this.mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validTestUser))
                .andDo(document("home"))
                .andExpect(status().isCreated());

        //get new list of user
        response =
            this.mockMvc
                .perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        JSONResponse = new JSONObject(response.getResponse().getContentAsString());
        int newLength = JSONResponse.getInt("totalElements");

        //assert that there should be one more user now
        Assert.assertThat(newLength - oldLength, equalTo(1));
      }

    /**
     * Picks random user and then does API request for that user, compares values
     *
     * @throw Exception on failure
     */
    @Test
    @WithMockUser(authorities = {"SYS_USER_GETUSERBYID","SYS_USER_GETALL"})
    public void validGetUserByIdTest() throws Exception {

        JSONObject user = getRandomUser();
        String id = user.getString("id");

        MvcResult response =
                this.mockMvc
                        .perform(get("/users/" + id))
                        .andDo(document("home"))
                        .andExpect(status().isOk())
                        .andReturn();

        JSONObject responseUser = new JSONObject(response.getResponse().getContentAsString());


        //checks every field that should be implemented according to yml
        String[] fields = {
                "id", "username", "enabled", "admin", /*"groups", "authorities"*/
        };
        for (int i = 0; i < fields.length; i++) {

            Assert.assertThat(responseUser.get(fields[i]), equalTo(user.get(fields[i])));
        }
    }

    /**
     * Picks random user and updates fields randomly within their definitions and then requests user to see whether fields are as expected
     * @throws Exception on failure
     */
    @Test
    @WithMockUser(authorities = {"SYS_USER_UPDATE","SYS_USER_GETALL", "SYS_USER_GETUSERBYID"})
    public void validUserUpdateTest() throws Exception{

        JSONObject user = getRandomUser();
        String id = user.getString("id");

        Random rand = new Random();

        String[] fields = {"username", "enabled", "admin", "password"};
        String[] values = {"Arya+"+(new Integer(rand.nextInt(10000))).toString()+"@winterfell.com","true","true","2018-04-13T22:00:00.000+0000","LannisterSuck"};

        for(int i = 0; i < fields.length; i++){
            if(user.has(fields[i])){
                user.put(fields[i],values[i]);
            }
        }

        user.putOpt("roles",new JSONArray());
        user.putOpt("groups",new JSONArray());


        System.out.println("Attempt to update user "+id);
        System.out.println(user.toString());

        mockMvc.perform(
                put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andDo(document("home"))
                .andExpect(status().isOk());


        MvcResult response =
                this.mockMvc
                        .perform(get("/users/"+id))
                        .andDo(document("home"))
                        .andExpect(status().isOk())
                        .andReturn();


        JSONObject responseUser = new JSONObject(response.getResponse().getContentAsString());

        //checks every field that should be implemented according to yml
        for(int i = 0; i < fields.length; i++) {
            if(fields[i] != "password")
                Assert.assertThat(responseUser.get(fields[i]).toString(), equalTo(user.get(fields[i])));
        }

        System.out.println(response.getResponse().getContentAsString());
    }
}
