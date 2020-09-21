package edu.bigdata.training.testservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bigdata.training.testservice.config.ServiceConf;
import edu.bigdata.training.testservice.config.UtilsConf;
import edu.bigdata.training.testservice.controller.model.Person;
import edu.bigdata.training.testservice.model.PersonEntity;
import edu.bigdata.training.testservice.utils.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ServiceConf.class, UtilsConf.class})
public class TestServiceControllerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityUtils entityUtils;

    @Before
    public void init() {
        entityUtils.clearPersonEntitiesCache();
    }

    @Test
    public void get_should_returnPersonEntity_when_personEntityExists() throws Exception {
        PersonEntity personEntity = entityUtils.createAndSavePersonEntity();
        String expectedJson = objectMapper.writeValueAsString(personEntity);

        mvc.perform(get("/person/" + personEntity.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void getall_should_returnPersonEntity_when_personEntityExists() throws Exception {
        PersonEntity personEntity1= entityUtils.createAndSavePersonEntity();
        PersonEntity personEntity2 = entityUtils.createAndSavePersonEntity();
        String expectedJson = objectMapper.writeValueAsString(Arrays.asList(personEntity1, personEntity2));

        mvc.perform(get("/person/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void create_should_add_PersonEntity() throws Exception {
        Person person = new Person("name");
        String argJson = objectMapper.writeValueAsString(person);

        String response = mvc.perform(post("/person/")
                .content(argJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Map<String,String> responseObject = objectMapper.readValue(response, Map.class);
        Assert.assertEquals("name", responseObject.get("name"));

        PersonEntity personEntity= entityUtils.getPersonEntity(responseObject.get("id"));
        Assert.assertEquals("name", personEntity.getName());

    }

    @Test
    public void delete_PersonEntity() throws Exception {

        PersonEntity personEntity1 = entityUtils.createAndSavePersonEntity();
        PersonEntity personEntity2 = entityUtils.createAndSavePersonEntity();
        String expectedJson = objectMapper.writeValueAsString(Arrays.asList(personEntity1));


        mvc.perform(delete("/person/" + personEntity2.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/person/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

    }

    @Test
    public void update_PersonEntity() throws Exception {
        PersonEntity personEntity1= entityUtils.createAndSavePersonEntity();
        personEntity1.setName("name");
        Person person = new Person("name");
        String expectedJson = objectMapper.writeValueAsString(personEntity1);

        String argJson = objectMapper.writeValueAsString(person);

        mvc.perform(put("/person/"+ personEntity1.getId())
                .content(argJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(get("/person/" + personEntity1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

    }
}
