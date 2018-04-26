package de.ul.swtp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class MvApplicationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        // just test if the application context loads
    }

    @Test
    @WithMockUser(roles = "USER")
    public void UsersEndpointDefined() throws Exception {
        MvcResult response =
                this.mockMvc
                        .perform(get("/users"))
                        .andDo(document("home"))
                        .andReturn();

        Assert.assertNotEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void GroupsEndpointDefined() throws Exception {
        MvcResult response =
                this.mockMvc
                        .perform(get("/groups"))
                        .andDo(document("home"))
                        .andReturn();

        Assert.assertNotEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void ContactsEndpointDefined() throws Exception {
        MvcResult response =
                this.mockMvc
                        .perform(get("/contacts"))
                        .andDo(document("home"))
                        .andReturn();

        Assert.assertNotEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void PermissionsEndpointDefined() throws Exception {
        MvcResult response =
                this.mockMvc
                        .perform(get("/permissions"))
                        .andDo(document("home"))
                        .andReturn();

        Assert.assertNotEquals(response.getResponse().getStatus(), 404);
    }

    @Test
    public void LoginEndpointDefined() throws Exception {
        MvcResult response =
                this.mockMvc
                        .perform(post("/login").content("{ \"username\": \"username\", \"password\": \"password\" }"))
                        .andDo(document("home"))
                        .andReturn();

        Assert.assertNotEquals(response.getResponse().getStatus(), 404);
    }

}
