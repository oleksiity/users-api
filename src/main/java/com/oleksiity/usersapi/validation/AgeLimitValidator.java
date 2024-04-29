package com.oleksiity.usersapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public class AgeLimitValidator implements ConstraintValidator<AgeLimit, LocalDate> {

    @Value("${validation.minAge:18}")
    private int minimumAge;

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        LocalDate today = LocalDate.now();
        LocalDate minimumAgeYearsAgo = today.minusYears(this.minimumAge);
        return !minimumAgeYearsAgo.isBefore(localDate);
    }
}
