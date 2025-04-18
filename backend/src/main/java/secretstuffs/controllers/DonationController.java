package secretstuffs.controllers;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import secretstuffs.application.services.DonationService;
import secretstuffs.application.services.ItemPostService;
import secretstuffs.application.services.UserService;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.entities.Donation;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.models.requests.donation.DonateRequestDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserService userService;
    private final ItemPostService itemPostService;
    private final Logger log = LoggerFactory.getLogger(DonationController.class);
    private final ModelMapper modelMapper;

    public DonationController(DonationService donationService, UserService userService, ItemPostService itemPostService, ModelMapper modelMapper) {
        this.donationService = donationService;
        this.userService = userService;
        this.itemPostService = itemPostService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Donation>> getDonation(@PathVariable("id") Long id) {
        log.info("Getting donation with id: {}", id);
        Optional<Donation> donation = donationService.getDonationById(id);
        if (donation.isPresent()) {
            return buildResponse("Donate", HttpStatus.OK, donation.get());
        }
        return buildResponse("Donation not found", HttpStatus.NOT_FOUND, null);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponseDTO<Donation>> createDonation(@RequestBody DonateRequestDTO dto) {
        log.info("Creating donation: {}", dto.getUserId() + " " + dto.getItemPostId());

        // Check if user and item post exist
        try {
            User user = userService.getUserById(dto.getUserId());
            log.info("User: {}", user);
            ItemPost itemPost = itemPostService.getItemById(dto.getItemPostId());
            log.info("Item post: {}", itemPost);
        }
        catch (BusinessException e) {
            return buildResponse("User or item post not found", e.getStatusCode(), null);
        }

        Donation donation = modelMapper.map(dto, Donation.class);
        Donation createdDonation = donationService.save(donation);
        return buildResponse("Donation created successfully", HttpStatus.CREATED, createdDonation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<Donation>>> getDonationsByUserId(@PathVariable("userId") Long userId) {
        log.info("Getting donations for user with id: {}", userId);
        List<Donation> donations = donationService.getDonationsByUserId(userId);
        return buildResponse("Donations fetched successfully", HttpStatus.OK, donations);
    }

    @GetMapping("/item/{itemPostId}")
    public ResponseEntity<ApiResponseDTO<List<Donation>>> getDonationsByItemPostId(@PathVariable("itemPostId") Long itemPostId) {
        log.info("Getting donations for item post with id: {}", itemPostId);
        List<Donation> donations = donationService.getDonationsByItemPostId(itemPostId);
        return buildResponse("Donations fetched successfully", HttpStatus.OK, donations);
    }

    @PostMapping("/donate")
    public ResponseEntity<ApiResponseDTO<Donation>> donate(@RequestBody DonateRequestDTO dto) {
        log.info("Donating to item post with id: {}", dto.getItemPostId());
        Donation donation = donationService.donate(dto.getUserId(), dto.getItemPostId());
        return buildResponse("Donation updated successfully", HttpStatus.OK, donation);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(String message, HttpStatus status, T data) {
        ApiResponseDTO<T> apiResponse = new ApiResponseDTO<>(message, status.value(), data);
        return ResponseEntity.status(status).body(apiResponse);
    }
}