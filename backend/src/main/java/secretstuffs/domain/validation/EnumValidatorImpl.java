package secretstuffs.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private String[] enumValues;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        enumValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // Allow null values (if not annotated with @NotBlank)
        }
        for (String enumValue : enumValues) {
            if (enumValue.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
