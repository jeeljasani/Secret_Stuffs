package secretstuffs.application.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import secretstuffs.domain.entities.Donation;
import secretstuffs.domain.enums.DonationEnum;
import secretstuffs.infrastructure.repositories.DonationRepository;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public Donation save(Donation donation) {
        validateDonation(donation);
        checkForDuplicateDonation(donation);
        return donationRepository.save(donation);
    }

    public Donation donate(Long userId, Long itemPostId) {
        Donation donation = donationRepository.findByUser_IdAndItemPost_Id(userId, itemPostId).orElse(null);
        if (donation == null) {
            throw new IllegalArgumentException("Donation not found");
        }
        donation.setStatus(DonationEnum.ACCEPTED);
        return donationRepository.save(donation);
    }

    public List<Donation> getDonationsByUserId(Long userId) {
        return donationRepository.findByUser_Id(userId);
    }

    public List<Donation> getDonationsByItemPostId(Long itemPostId) {
        return donationRepository.findByItemPost_Id(itemPostId);
    }

    private void validateDonation(Donation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation cannot be null");
        }
        if (donation.getUser() == null || donation.getUser().getId() == null) {
            throw new IllegalArgumentException("Donation must have a valid user with an ID");
        }
        if (donation.getItemPost() == null || donation.getItemPost().getId() == null) {
            throw new IllegalArgumentException("Donation must have a valid item post with an ID");
        }
    }

    private void checkForDuplicateDonation(Donation donation) {
        Long userId = donation.getUser().getId();
        Long itemPostId = donation.getItemPost().getId();
        boolean exists = donationRepository.findByUser_IdAndItemPost_Id(userId, itemPostId).isPresent();
        if (exists) {
            throw new IllegalArgumentException("A donation already exists for this user and item post");
        }
    }
}