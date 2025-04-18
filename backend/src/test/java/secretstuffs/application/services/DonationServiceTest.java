package secretstuffs.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import secretstuffs.domain.entities.Donation;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.enums.DonationEnum;
import secretstuffs.infrastructure.repositories.DonationRepository;

class DonationServiceTest {

   @Mock
   private DonationRepository donationRepository;

   @InjectMocks
   private DonationService donationService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void getDonationById_ShouldReturnDonation_WhenExists() {
      Long id = 1L;
      Donation donation = new Donation();
      donation.setId(id);

      when(donationRepository.findById(id)).thenReturn(Optional.of(donation));

      Optional<Donation> result = donationService.getDonationById(id);

      assertTrue(result.isPresent());
      assertEquals(donation, result.get());
   }

   @Test
   void getDonationById_ShouldReturnEmpty_WhenDoesNotExist() {
      Long id = 1L;

      when(donationRepository.findById(id)).thenReturn(Optional.empty());

      Optional<Donation> result = donationService.getDonationById(id);

      assertTrue(result.isEmpty());
   }

   @Test
   void save_ShouldSaveDonation_WhenValid() {
      Donation donation = createValidDonation();

      when(donationRepository.save(donation)).thenReturn(donation);

      Donation result = donationService.save(donation);

      assertEquals(donation, result);
      verify(donationRepository, times(1)).save(donation);
   }

   @Test
   void save_ShouldThrowException_WhenDonationIsNull() {
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         donationService.save(null);
      });

      assertEquals("Donation cannot be null", exception.getMessage());
   }

   @Test
   void save_ShouldThrowException_WhenUserIsNull() {
      Donation donation = new Donation();
      donation.setItemPost(new ItemPost());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         donationService.save(donation);
      });

      assertEquals("Donation must have a valid user with an ID", exception.getMessage());
   }

   @Test
   void save_ShouldThrowException_WhenItemPostIsNull() {
      Donation donation = new Donation();
      donation.setUser(new User());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         donationService.save(donation);
      });

      assertEquals("Donation must have a valid user with an ID", exception.getMessage());
   }

   @Test
   void save_ShouldThrowException_WhenDuplicateDonationExists() {
      Donation donation = createValidDonation();

      when(donationRepository.findByUser_IdAndItemPost_Id(
              donation.getUser().getId(), donation.getItemPost().getId()))
              .thenReturn(Optional.of(donation));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         donationService.save(donation);
      });

      assertEquals("A donation already exists for this user and item post", exception.getMessage());
   }

   @Test
   void donate_ShouldUpdateDonationStatus_WhenExists() {
      Long userId = 1L;
      Long itemPostId = 1L;

      Donation donation = createValidDonation();
      donation.setStatus(DonationEnum.PENDING);

      when(donationRepository.findByUser_IdAndItemPost_Id(userId, itemPostId))
              .thenReturn(Optional.of(donation));
      when(donationRepository.save(donation)).thenReturn(donation);

      Donation result = donationService.donate(userId, itemPostId);

      assertEquals(DonationEnum.ACCEPTED, result.getStatus());
      verify(donationRepository, times(1)).save(donation);
   }

   @Test
   void donate_ShouldThrowException_WhenDonationDoesNotExist() {
      Long userId = 1L;
      Long itemPostId = 1L;

      when(donationRepository.findByUser_IdAndItemPost_Id(userId, itemPostId))
              .thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         donationService.donate(userId, itemPostId);
      });

      assertEquals("Donation not found", exception.getMessage());
   }

   @Test
   void getDonationsByUserId_ShouldReturnDonations_WhenExists() {
      Long userId = 1L;
      List<Donation> donations = new ArrayList<>();

      when(donationRepository.findByUser_Id(userId)).thenReturn(donations);

      List<Donation> result = donationService.getDonationsByUserId(userId);

      assertEquals(donations, result);
   }

   @Test
   void getDonationsByUserId_ShouldReturnEmptyList_WhenNoDonationsExist() {
      Long userId = 1L;

      when(donationRepository.findByUser_Id(userId)).thenReturn(new ArrayList<>());

      List<Donation> result = donationService.getDonationsByUserId(userId);

      assertTrue(result.isEmpty());
   }

   @Test
   void getDonationsByItemPostId_ShouldReturnDonations_WhenExists() {
      Long itemPostId = 1L;
      List<Donation> donations = new ArrayList<>();

      when(donationRepository.findByItemPost_Id(itemPostId)).thenReturn(donations);

      List<Donation> result = donationService.getDonationsByItemPostId(itemPostId);

      assertEquals(donations, result);
   }

   @Test
   void getDonationsByItemPostId_ShouldReturnEmptyList_WhenNoDonationsExist() {
      Long itemPostId = 1L;

      when(donationRepository.findByItemPost_Id(itemPostId)).thenReturn(new ArrayList<>());

      List<Donation> result = donationService.getDonationsByItemPostId(itemPostId);

      assertTrue(result.isEmpty());
   }

   private Donation createValidDonation() {
      Donation donation = new Donation();
      User user = new User();
      user.setId(1L);
      donation.setUser(user);

      ItemPost itemPost = new ItemPost();
      itemPost.setId(1L);
      donation.setItemPost(itemPost);

      donation.setCreatedAt(LocalDateTime.now());
      donation.setStatus(DonationEnum.PENDING);
      return donation;
   }
}