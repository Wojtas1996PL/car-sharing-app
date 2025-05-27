package mate.academy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.user.UserRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.model.RoleName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
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
                    new ClassPathResource("database/add-three-users.sql"));
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

    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method updateRole works")
    public void updateRole_CorrectRole_ReturnsUserResponseDto() throws Exception {
        UserResponseDto expectedUserResponseDto = new UserResponseDto();
        expectedUserResponseDto.setId(1L);
        expectedUserResponseDto.setEmail("claude@gmail.com");
        expectedUserResponseDto.setRole(RoleName.ROLE_MANAGER);
        expectedUserResponseDto.setFirstName("Claude");
        expectedUserResponseDto.setLastName("Strife");

        String jsonRequest = objectMapper.writeValueAsString(expectedUserResponseDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/role", 1L)
                        .param("role", "ROLE_MANAGER")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actualUserResponseDto = objectMapper
                .readValue(result.getResponse()
                        .getContentAsString(), UserResponseDto.class);

        assertThat(actualUserResponseDto).isEqualTo(expectedUserResponseDto);
        assertNotNull(actualUserResponseDto);
    }

    @WithUserDetails(value = "bob@gmail.com")
    @Test
    @DisplayName("Verify that method getProfileInfo works")
    public void getProfileInfo_CorrectUser_ReturnsUserResponseDto() throws Exception {
        UserResponseDto expectedUserResponseDto = new UserResponseDto();
        expectedUserResponseDto.setId(2L);
        expectedUserResponseDto.setEmail("bob@gmail.com");
        expectedUserResponseDto.setRole(RoleName.ROLE_CUSTOMER);
        expectedUserResponseDto.setFirstName("Bob");
        expectedUserResponseDto.setLastName("Marley");

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actualUserResponseDto = objectMapper
                .readValue(result.getResponse()
                        .getContentAsString(), UserResponseDto.class);

        assertThat(actualUserResponseDto).isEqualTo(expectedUserResponseDto);
        assertNotNull(actualUserResponseDto);
    }

    @WithUserDetails(value = "mila@gmail.com")
    @Test
    @DisplayName("Verify that method updateProfileInfo works")
    public void updateProfileInfo_CorrectUser_ReturnsUserResponseDto() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("mila@gmail.com");
        userRequestDto.setFirstName("M");
        userRequestDto.setLastName("J");

        UserResponseDto expectedUserResponseDto = new UserResponseDto();
        expectedUserResponseDto.setId(3L);
        expectedUserResponseDto.setEmail("mila@gmail.com");
        expectedUserResponseDto.setRole(RoleName.ROLE_CUSTOMER);
        expectedUserResponseDto.setFirstName("M");
        expectedUserResponseDto.setLastName("J");

        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actualUserResponseDto = objectMapper
                .readValue(result.getResponse()
                        .getContentAsString(), UserResponseDto.class);

        assertThat(actualUserResponseDto).isEqualTo(expectedUserResponseDto);
        assertNotNull(actualUserResponseDto);
    }
}
