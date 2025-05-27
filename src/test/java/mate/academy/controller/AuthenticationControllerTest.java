package mate.academy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.dto.user.UserLoginResponseDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserRegistrationResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    @Autowired
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext webApplicationContext)
            throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-random-user.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-all-database-tables.sql"));
        }
    }

    @Test
    @DisplayName("Verify that method register works")
    public void register_CorrectUserRegistrationRequestDto_ReturnsUserRegistrationResponseDto()
            throws Exception {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setEmail("bob@gmail.com");
        userRegistrationRequestDto.setFirstName("Bob");
        userRegistrationRequestDto.setLastName("Marley");
        userRegistrationRequestDto.setPassword("123456789");
        userRegistrationRequestDto.setRepeatPassword("123456789");

        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);

        MvcResult result = mockMvc
                .perform(post("/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UserRegistrationResponseDto userRegistrationResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(),
                        UserRegistrationResponseDto.class);

        assertThat(userRegistrationResponseDto.getEmail())
                .isEqualTo(userRegistrationRequestDto.getEmail());
        assertNotNull(userRegistrationResponseDto);
    }

    @Test
    @DisplayName("Verify that method login works")
    public void login_CorrectUserLoginRequestDto_ReturnsLoginResponseDto() throws Exception {
        UserLoginRequestDto userLoginRequestDto =
                new UserLoginRequestDto("claude@gmail.com", "123456789");

        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);

        MvcResult result = mockMvc.perform(post("/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto userLoginResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);

        assertNotNull(userLoginResponseDto.token());
        assertFalse(userLoginResponseDto.token().isEmpty());
    }
}
