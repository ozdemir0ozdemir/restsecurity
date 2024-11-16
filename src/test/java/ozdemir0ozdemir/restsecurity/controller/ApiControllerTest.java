package ozdemir0ozdemir.restsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import ozdemir0ozdemir.restsecurity.config.SecurityConfig;

import java.util.Base64;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest({ApiController.class, SecurityConfig.class})
class ApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldReturnApiKeyAuth() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.put("API-Key", List.of("valid-api-key"));
        headers.put("API-Secret", List.of("valid-api-secret"));

        mvc
                .perform(get("/api/data")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsString("ApiKeyAuthenticationToken")));
    }

    @Test
    void shouldReturnBasicAuth() throws Exception {

        String basic = Base64.getEncoder().encodeToString("username:password".getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Basic " + basic));

        mvc
                .perform(get("/api/data")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsString("UsernamePasswordAuthenticationToken")));
    }

    @Test
    void shouldReturnStatusUnauthorized() throws Exception {

        mvc
                .perform(get("/api/data"))
                .andExpect(status().isUnauthorized());
    }



}