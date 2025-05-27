package mate.academy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.car.CarDto;
import mate.academy.model.CarType;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
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
                    new ClassPathResource("database/add-three-cars.sql"));
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

    @WithMockUser(username = "bob", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method addNewCar works")
    @Sql(scripts = "classpath:database/delete-car-audi.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addNewCar_CorrectCarDto_ReturnsCarDto() throws Exception {
        CarDto expectedCarDto = new CarDto();
        expectedCarDto.setBrand("Audi");
        expectedCarDto.setModel("Q5");
        expectedCarDto.setType(CarType.SUV);
        expectedCarDto.setDailyFee(BigDecimal.valueOf(159.99));
        expectedCarDto.setInventory(10);

        String jsonRequest = objectMapper.writeValueAsString(expectedCarDto);

        MvcResult result = mockMvc
                .perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CarDto actualCarDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(),
                        CarDto.class);

        expectedCarDto.setId(actualCarDto.getId());

        assertThat(actualCarDto).isEqualTo(expectedCarDto);
        assertNotNull(actualCarDto);
    }

    @Test
    @DisplayName("Verify that method getAllCars works")
    public void getAllCars_CorrectCars_ReturnsCarDtoList() throws Exception {
        CarDto carDto1 = new CarDto();
        carDto1.setId(1L);
        carDto1.setBrand("Mercedes-Benz");
        carDto1.setModel("CLS");
        carDto1.setType(CarType.UNIVERSAL);
        carDto1.setInventory(20);
        carDto1.setDailyFee(BigDecimal.valueOf(300));

        CarDto carDto2 = new CarDto();
        carDto2.setId(2L);
        carDto2.setBrand("Mercedes-Benz");
        carDto2.setModel("GLS");
        carDto2.setType(CarType.SUV);
        carDto2.setInventory(25);
        carDto2.setDailyFee(BigDecimal.valueOf(270));

        CarDto carDto3 = new CarDto();
        carDto3.setId(3L);
        carDto3.setBrand("BMW");
        carDto3.setModel("X5");
        carDto3.setType(CarType.SUV);
        carDto3.setInventory(30);
        carDto3.setDailyFee(BigDecimal.valueOf(200));

        List<CarDto> expectedCars = List.of(carDto1, carDto2, carDto3);

        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarDto> actualCars = objectMapper
                .readValue(result.getResponse()
                                .getContentAsByteArray(),
                        new TypeReference<>() {
                        });

        assertThat(actualCars).isEqualTo(expectedCars);
        assertNotNull(actualCars);
    }

    @Test
    @DisplayName("Verify that method getCarInfo works")
    public void getCarInfo_CorrectCarId_ReturnsCarDto() throws Exception {
        CarDto expectedCarDto = new CarDto();
        expectedCarDto.setId(1L);
        expectedCarDto.setBrand("Mercedes-Benz");
        expectedCarDto.setModel("CLS");
        expectedCarDto.setType(CarType.UNIVERSAL);
        expectedCarDto.setInventory(20);
        expectedCarDto.setDailyFee(BigDecimal.valueOf(300));

        MvcResult result = mockMvc.perform(get("/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actualCarDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(), CarDto.class);

        assertThat(actualCarDto).isEqualTo(expectedCarDto);
        assertNotNull(actualCarDto);
    }

    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    @Test
    @DisplayName("Verify that method updateCar works")
    @Sql(scripts = "classpath:database/add-car-audi.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-car-audi.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCar_CorrectCar_ReturnsCarDto() throws Exception {
        CarDto expectedCarDto = new CarDto();
        expectedCarDto.setId(4L);
        expectedCarDto.setBrand("Mercedes-Benz");
        expectedCarDto.setModel("CLE");
        expectedCarDto.setType(CarType.SEDAN);
        expectedCarDto.setInventory(20);
        expectedCarDto.setDailyFee(BigDecimal.valueOf(300));

        String jsonRequest = objectMapper.writeValueAsString(expectedCarDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/cars/{id}", 4L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actualCarDto = objectMapper
                .readValue(result.getResponse()
                        .getContentAsString(), CarDto.class);

        assertThat(actualCarDto).isEqualTo(expectedCarDto);
        assertNotNull(actualCarDto);
    }

    @Test
    @DisplayName("Verify that method deleteCar works with manager")
    @WithMockUser(username = "mila@gmail.com", roles = "MANAGER")
    public void deleteCar_CorrectCarId_ReturnsNothingAndStatusIsOk() throws Exception {
        mockMvc.perform(delete("/cars/{id}", 2L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify that method deleteCar does not work wih customer")
    @WithMockUser(username = "bob@gmail.com", roles = "CUSTOMER")
    public void deleteCar_CorrectCarId_ReturnsNothingAndStatusForbidden() throws Exception {
        mockMvc.perform(delete("/cars/{id}", 2L))
                .andExpect(status().isForbidden());
    }
}
