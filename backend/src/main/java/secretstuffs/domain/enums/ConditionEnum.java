package secretstuffs.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ConditionEnum {
    NEW("Brand New"),
    LIKE_NEW("Like New"),
    GOOD("Good Condition"),
    FAIR("Fair Condition"),
    POOR("Poor Condition"),
    DAMAGED("Damaged");

    private final String description;

    ConditionEnum(String description) {
        this.description = description;
    }

    /**
     * Validates if a given string is a valid condition.
     *
     * @param value the condition name
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String value) {
        return Arrays.stream(ConditionEnum.values())
                .anyMatch(condition -> condition.name().equalsIgnoreCase(value));
    }
}