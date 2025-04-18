package secretstuffs.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DonationEnum {
    PENDING("Pending Approval"),
    ACCEPTED("Accepted by Recipient"),
    REJECTED("Rejected by Recipient");

    private final String description;

    DonationEnum(String description) {
        this.description = description;
    }

    /**
     * Finds a donation status by its name, ignoring case.
     *
     * @param value the donation status name
     * @return the matching DonationEnum
     * @throws IllegalArgumentException if no match is found
     */
    public static DonationEnum fromString(String value) {
        return Arrays.stream(DonationEnum.values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid donation status: " + value));
    }
}