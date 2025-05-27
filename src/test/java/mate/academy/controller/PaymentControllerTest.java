package mate.academy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.stripe.Stripe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.payment.PaymentRequestDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.model.PaymentStatus;
import mate.academy.model.PaymentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    @Autowired
    protected static MockMvc mockMvc;

    @RegisterExtension
    private static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8080))
            .build();

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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-three-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-four-rentals.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-three-payments.sql"));
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
    @DisplayName("Verify that method getPaymentsFromUser works")
    public void getPayments_CorrectPayments_ReturnsPaymentResponseDtoList() throws Exception {
        PaymentResponseDto paymentResponseDto1 = new PaymentResponseDto();
        paymentResponseDto1.setId(1L);
        paymentResponseDto1.setRentalId(1L);
        paymentResponseDto1.setStatus(PaymentStatus.PAID);
        paymentResponseDto1.setType(PaymentType.PAYMENT);
        paymentResponseDto1.setSessionId("sessionId1");
        paymentResponseDto1.setSessionUrl("http://session.com");

        PaymentResponseDto paymentResponseDto2 = new PaymentResponseDto();
        paymentResponseDto2.setId(2L);
        paymentResponseDto2.setRentalId(2L);
        paymentResponseDto2.setStatus(PaymentStatus.PAID);
        paymentResponseDto2.setType(PaymentType.PAYMENT);
        paymentResponseDto2.setSessionId("sessionId2");
        paymentResponseDto2.setSessionUrl("http://session.com");

        List<PaymentResponseDto> expectedPaymentsResponseDto = List
                .of(paymentResponseDto1, paymentResponseDto2);

        MvcResult result = mockMvc.perform(get("/payments")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDto> actualPaymentsResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsByteArray(),
                        new TypeReference<>() {
                        });

        assertThat(actualPaymentsResponseDto).isEqualTo(expectedPaymentsResponseDto);
        assertNotNull(actualPaymentsResponseDto);
    }

    @WithMockUser(username = "bob@gmail.com", roles = "CUSTOMER")
    @Test
    @DisplayName("Verify that method createPayment works")
    public void createPayment_CorrectPayment_ReturnsPaymentResponseDto() throws Exception {
        Stripe.overrideApiBase("http://localhost:8080");
        Stripe.apiKey = "sk_test_mocked_key";

        wireMockExtension.stubFor(WireMock.post("/v1/checkout/sessions")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"session_123\", \"status\": \"open\", \"url\": "
                                + "\"http://localhost:8080/payments/success\"}")));

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRentalId(3L);
        paymentRequestDto.setType(PaymentType.PAYMENT);

        PaymentResponseDto expectedPaymentResponseDto = new PaymentResponseDto();
        expectedPaymentResponseDto.setRentalId(3L);
        expectedPaymentResponseDto.setType(PaymentType.PAYMENT);

        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);

        MvcResult result = mockMvc
                .perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actualPaymentResponseDto = objectMapper
                .readValue(result.getResponse()
                                .getContentAsString(),
                        PaymentResponseDto.class);

        expectedPaymentResponseDto.setId(actualPaymentResponseDto.getId());
        expectedPaymentResponseDto.setStatus(actualPaymentResponseDto.getStatus());
        expectedPaymentResponseDto.setSessionUrl(actualPaymentResponseDto.getSessionUrl());
        expectedPaymentResponseDto.setMoneyToPay(actualPaymentResponseDto.getMoneyToPay());
        expectedPaymentResponseDto.setSessionId(actualPaymentResponseDto.getSessionId());

        assertThat(actualPaymentResponseDto).isEqualTo(expectedPaymentResponseDto);
        assertNotNull(actualPaymentResponseDto);
    }
}
