package ru.template.example.documents.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.template.example.configuration.JacksonConfiguration;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.service.DocumentServiceImpl;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
public class DocumentControllerTest {
    private static final String BASE_PATH = "/documents";

    private final ObjectMapper mapper = new JacksonConfiguration().objectMapper();
    private MockMvc mockMvc;
    @MockBean
    private DocumentServiceImpl service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void successWhenSaveTest() throws Exception {
        var organization = randomAlphabetic(100);

        when(service.save(any())).thenReturn(any());

        var cityDto = new DocumentDto();
        cityDto.setId(5L);
        cityDto.setOrganization(organization);
        mockMvc.perform(postAction(BASE_PATH, cityDto)).andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).save(cityDto);
    }

    @Test
    public void errorWhenSaveTest() throws Exception {
        //TOO long name
        var organization = randomAlphabetic(1000);

        when(service.save(any())).thenThrow(new IllegalStateException("Это слишком!"));

        var cityDto = new DocumentDto();
        cityDto.setId(5L);
        cityDto.setOrganization(organization);
        mockMvc.perform(postAction(BASE_PATH, cityDto)).andExpect(status().is5xxServerError());
    }

    @Test
    public void getTest() {
        //TODO написать тест
    }

    @Test
    public void sendTest() {
        //TODO написать тест
    }

    @Test
    public void deleteTest() {
        //TODO написать тест
    }

    @Test
    public void deleteAllTest() {
        //TODO написать тест
    }

    private MockHttpServletRequestBuilder postAction(String uri, Object dto) throws JsonProcessingException {
        return post(uri)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
    }
}
