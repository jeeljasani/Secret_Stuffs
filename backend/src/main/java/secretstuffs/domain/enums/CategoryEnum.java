package secretstuffs.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CategoryEnum {
    FURNITURE("Furniture"),
    ELECTRONICS("Electronics"),
    BOOKS("Books"),
    VEHICLE("Vehicle"),
    CLOTHING("Clothing"),
    TOYS("Toys"),
    SPORTS_EQUIPMENT("Sports Equipment"),
    HOME_APPLIANCES("Home Appliances"),
    HEALTHCARE("Healthcare"),
    SCHOOL_SUPPLIES("School Supplies"),
    FOOD("Food"),
    BABY_PRODUCTS("Baby Products"),
    PET_SUPPLIES("Pet Supplies"),
    ART_SUPPLIES("Art Supplies"),
    TOOLS("Tools"),
    MUSICAL_INSTRUMENTS("Musical Instruments"),
    MISC("Miscellaneous");

    private final String label;

    CategoryEnum(String label) {
        this.label = label;
    }

    /**
     * Finds a category by its name, ignoring case.
     *
     * @param value the category name
     * @return the matching CategoryEnum
     * @throws IllegalArgumentException if no match is found
     */
    public static CategoryEnum fromString(String value) {
        return Arrays.stream(CategoryEnum.values())
                .filter(category -> category.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + value));
    }
}