package mate.academy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;
import mate.academy.model.RoleName;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    @Autowired
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

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
                    new ClassPathResource("database/add-three-cars.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-three-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-four-rentals.sql"));
        }
    }

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("bob@gmail.com");
        user.setPassword("123456789");
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setDeleted(false);
        user.setRole(RoleName.ROLE_CUSTOMER);
        userRepository.save(user);
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

    @WithUserDetails(value = "bob@gmail.com")
    @Test
    @DisplayName("Verify that method addRental works")
    public void addRental_CorrectRentalRequestDto_ReturnsRentalResponseDto() throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.setCarId(1L);
        rentalRequestDto.setRentalDate(LocalDate.of(2025, 7, 5));
        rentalRequestDto.setReturnDate(LocalDate.of(2025, 8, 10));

        RentalResponseDto expectedRentalResponseDto = new RentalResponseDto();
        expectedRentalResponseDto.setRentalDate(LocalDate.of(2025, 7, 5));
        expectedRentalResponseDto.setReturnDate(LocalDate.of(2025, 8, 10));
        expectedRentalResponseDto.setCarId(1L);
        expectedRentalResponseDto.setActive(true);

        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        MvcResult result = mockMvc
                .perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDto actualRentalResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(),
                        RentalResponseDto.class);

        expectedRentalResponseDto.setId(actualRentalResponseDto.getId());
        expectedRentalResponseDto.setUserId(actualRentalResponseDto.getUserId());

        assertThat(actualRentalResponseDto).isEqualTo(expectedRentalResponseDto);
        assertNotNull(actualRentalResponseDto);
    }

    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method getRentalsFromUser works")
    public void getRentals_CorrectRentals_ReturnsRentalResponseDtoList() throws Exception {
        RentalResponseDto expectedRentalResponseDto1 = new RentalResponseDto();
        expectedRentalResponseDto1.setId(1L);
        expectedRentalResponseDto1.setUserId(1L);
        expectedRentalResponseDto1.setRentalDate(LocalDate.of(2025, 5, 7));
        expectedRentalResponseDto1.setReturnDate(LocalDate.of(2025, 5, 10));
        expectedRentalResponseDto1.setActualReturnDate(LocalDate.of(2025, 5, 9));
        expectedRentalResponseDto1.setCarId(1L);
        expectedRentalResponseDto1.setActive(false);

        RentalResponseDto expectedRentalResponseDto2 = new RentalResponseDto();
        expectedRentalResponseDto2.setId(4L);
        expectedRentalResponseDto2.setUserId(1L);
        expectedRentalResponseDto2.setRentalDate(LocalDate.of(2025, 5, 8));
        expectedRentalResponseDto2.setReturnDate(LocalDate.of(2025, 5, 10));
        expectedRentalResponseDto2.setActualReturnDate(LocalDate.of(2025, 5, 9));
        expectedRentalResponseDto2.setCarId(1L);
        expectedRentalResponseDto2.setActive(false);

        List<RentalResponseDto> expectedRentalsResponseDto = List
                .of(expectedRentalResponseDto1, expectedRentalResponseDto2);

        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("userId", "1")
                        .param("isActive", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalResponseDto> actualRentalsResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsByteArray(),
                        new TypeReference<>() {
                        });

        assertThat(actualRentalsResponseDto).isEqualTo(expectedRentalsResponseDto);
        assertNotNull(actualRentalsResponseDto);
    }

    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method getRentalInfo works")
    public void getRentalInfo_CorrectRentalId_ReturnsRentalResponseDto() throws Exception {
        RentalResponseDto expectedRentalResponseDto = new RentalResponseDto();
        expectedRentalResponseDto.setId(1L);
        expectedRentalResponseDto.setUserId(1L);
        expectedRentalResponseDto.setRentalDate(LocalDate.of(2025, 5, 7));
        expectedRentalResponseDto.setReturnDate(LocalDate.of(2025, 5, 10));
        expectedRentalResponseDto.setActualReturnDate(LocalDate.of(2025, 5, 9));
        expectedRentalResponseDto.setCarId(1L);
        expectedRentalResponseDto.setActive(false);

        MvcResult result = mockMvc.perform(get("/rentals/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actualRentalResponseDto = objectMapper
                .readValue(result.getResponse()
                        .getContentAsString(), RentalResponseDto.class);

        assertThat(actualRentalResponseDto).isEqualTo(expectedRentalResponseDto);
        assertNotNull(actualRentalResponseDto);
    }

    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method setActualReturnDate works")
    public void setActualReturnDate_CorrectDate_ReturnsRentalResponseDto() throws Exception {
        RentalResponseDto expectedRentalResponseDto = new RentalResponseDto();
        expectedRentalResponseDto.setId(2L);
        expectedRentalResponseDto.setUserId(1L);
        expectedRentalResponseDto.setRentalDate(LocalDate.of(2025, 5, 6));
        expectedRentalResponseDto.setReturnDate(LocalDate.of(2025, 5, 15));
        expectedRentalResponseDto.setActualReturnDate(LocalDate.of(2025, 5, 14));
        expectedRentalResponseDto.setCarId(2L);
        expectedRentalResponseDto.setActive(false);

        MvcResult result = mockMvc
                .perform(post("/rentals/return")
                        .param("id", "2")
                        .param("returnDate", "2025-05-14")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDto actualRentalResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(),
                        RentalResponseDto.class);

        assertThat(actualRentalResponseDto).isEqualTo(expectedRentalResponseDto);
        assertNotNull(actualRentalResponseDto);
    }
}
