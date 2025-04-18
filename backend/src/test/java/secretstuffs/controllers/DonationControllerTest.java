package secretstuffs.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import secretstuffs.application.services.DonationService;
import secretstuffs.application.services.ItemPostService;
import secretstuffs.application.services.UserService;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.entities.Donation;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.entities.User;
import org.modelmapper.ModelMapper;
import secretstuffs.domain.models.requests.donation.DonateRequestDTO;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

class DonationControllerTest {

    @Mock
    private DonationService donationService;

    @Mock
    private UserService userService;

    @Mock
    private ItemPostService itemPostService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DonationController donationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    @Test
    void createDonation_ReturnCreatedDonation_WhenRequestIsValid() throws Exception {
        // Arrange
        String requestBody = "{\"userId\":1,\"itemPostId\":1}";
        String expectedMessage = "Donation created successfully";

        DonateRequestDTO dto = new DonateRequestDTO();
        dto.setUserId(1L);
        dto.setItemPostId(1L);

        User user = new User();
        user.setId(1L);

        ItemPost itemPost = new ItemPost();
        itemPost.setId(1L);

        Donation donation = new Donation();
        donation.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(itemPostService.getItemById(1L)).thenReturn(itemPost);
        when(modelMapper.map(any(DonateRequestDTO.class), eq(Donation.class))).thenReturn(donation);
        when(donationService.save(any(Donation.class))).thenReturn(donation);

        // Act
        MockHttpServletRequestBuilder requestBuilder = post("/api/donations/");
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        requestBuilder.content(requestBody);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isCreated());
        response.andExpect(jsonPath("$.data.id").value(1L));
        response.andExpect(jsonPath("$.message").value(expectedMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void createDonation_ReturnNotFound_WhenUserOrItemPostDoesNotExist() throws Exception {
        // Arrange
        String requestBody = "{\"userId\":1,\"itemPostId\":1}";
        String errorMessage = "User or item post not found";

        when(userService.getUserById(1L)).thenThrow(new BusinessException("USER_NOT_FOUND", errorMessage, HttpStatus.NOT_FOUND));

        // Act
        MockHttpServletRequestBuilder requestBuilder = post("/api/donations/");
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        requestBuilder.content(requestBody);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isNotFound());
        response.andExpect(jsonPath("$.message").value(errorMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getDonation_ReturnDonation_WhenIdIsValid() throws Exception {
        // Arrange
        Long donationId = 1L;
        String expectedMessage = "Donate";

        Donation donation = new Donation();
        donation.setId(donationId);

        when(donationService.getDonationById(donationId)).thenReturn(Optional.of(donation));

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/donations/{id}", donationId);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data.id").value(donationId));
        response.andExpect(jsonPath("$.message").value(expectedMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getDonation_ReturnNotFound_WhenIdIsInvalid() throws Exception {
        // Arrange
        Long invalidId = -1L;
        String errorMessage = "Donation not found";

        when(donationService.getDonationById(invalidId)).thenReturn(Optional.empty());

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/donations/{id}", invalidId);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isNotFound());
        response.andExpect(jsonPath("$.message").value(errorMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getDonationsByUserId_ReturnDonationsForUser() throws Exception {
        // Arrange
        Long userId = 1L;
        String expectedMessage = "Donations fetched successfully";

        List<Donation> donations = List.of(new Donation(), new Donation());
        when(donationService.getDonationsByUserId(userId)).thenReturn(donations);

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/donations/user/{userId}", userId);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data").isArray());
        response.andExpect(jsonPath("$.message").value(expectedMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getDonationsByItemPostId_ReturnDonationsForItemPost() throws Exception {
        // Arrange
        Long itemPostId = 1L;
        String expectedMessage = "Donations fetched successfully";

        List<Donation> donations = List.of(new Donation(), new Donation());
        when(donationService.getDonationsByItemPostId(itemPostId)).thenReturn(donations);

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/donations/item/{itemPostId}", itemPostId);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data").isArray());
        response.andExpect(jsonPath("$.message").value(expectedMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void donate_UpdateDonation_WhenRequestIsValid() throws Exception {
        // Arrange
        String requestBody = "{\"userId\":1,\"itemPostId\":1}";
        String expectedMessage = "Donation updated successfully";

        Donation donation = new Donation();
        donation.setId(1L);

        when(donationService.donate(1L, 1L)).thenReturn(donation);

        // Act
        MockHttpServletRequestBuilder requestBuilder = post("/api/donations/donate");
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        requestBuilder.content(requestBody);

        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data.id").value(1L));
        response.andExpect(jsonPath("$.message").value(expectedMessage));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }
}