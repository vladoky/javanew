package edu.bigdata.training.testservice.service;

import edu.bigdata.training.testservice.controller.model.Person;
import edu.bigdata.training.testservice.dao.TestServiceRepository;
import edu.bigdata.training.testservice.model.PersonEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestBusinessLogicServiceTest.TestBusinessLogicServiceTestConfiguration.class})
public class TestBusinessLogicServiceTest {

    @Autowired
    private TestBusinessLogicService testBusinessLogicService;

    @Autowired
    private TestServiceRepository testServiceRepository;

    @Test
    public void testCreate() {
        //create
        Person person = new Person("test");

        PersonEntity personEntity = testBusinessLogicService.processCreate(person);

        Assert.assertEquals(person.getName(), personEntity.getName());
        Mockito.verify(testServiceRepository, Mockito.times(1)).save(personEntity);
    }

    @Test
    public void testGetAll() {
        //getAll
        List<PersonEntity> personEntityList = testBusinessLogicService.processGetAll();

        Assert.assertEquals("name1", personEntityList.get(0).getName());
        Assert.assertEquals("name2", personEntityList.get(1).getName());
        Mockito.verify(testServiceRepository, Mockito.times(1)).getAll();

    }

    @Test
    public void testGet() {
        //get
        PersonEntity personEntityList = testBusinessLogicService.processGet(UUID.randomUUID().toString());

        Assert.assertEquals("name", personEntityList.getName());
        Mockito.verify(testServiceRepository, Mockito.times(1)).get(any());
    }

    @Test
    public void testUpdate() {
        //update
        Person person = new Person("test");
        PersonEntity personEntity = testBusinessLogicService.processUpdate(UUID.randomUUID().toString(), person);
        Assert.assertEquals("name", personEntity.getName());
        Mockito.verify(testServiceRepository, Mockito.times(1)).update(any());
    }

    @Test
    public void testDelete() {
        //delete
        testBusinessLogicService.processDelete(UUID.randomUUID().toString());
        Mockito.verify(testServiceRepository, Mockito.times(1)).delete(any());
    }



    @Configuration
    static class TestBusinessLogicServiceTestConfiguration {

        @Bean
        TestServiceRepository testServiceRepository() {
            TestServiceRepository testServiceRepository = mock(TestServiceRepository.class);
            when(testServiceRepository.get(any()))
                    .thenReturn(new PersonEntity("name"));
            when(testServiceRepository.getAll())
                    .thenReturn(Arrays.asList(new PersonEntity("name1"),new PersonEntity("name2")));
            when(testServiceRepository.update(any()))
                    .thenReturn(new PersonEntity("name"));
            return testServiceRepository;
        }

        @Bean
        TestBusinessLogicService testBusinessLogicService(TestServiceRepository testServiceRepository){
            return new TestBusinessLogicService(testServiceRepository);
        }
    }

}