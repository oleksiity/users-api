package com.oleksiity.usersapi.controller.payload;

import com.oleksiity.usersapi.validation.AgeLimit;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUpdateUserPayload(@NotBlank(message = "{users-api.users.errors.email_empty}")
                                      @NotNull(message = "{users-api.users.errors.first_name_not_valid}")
                                      @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE,
                                              message = "{users-api.users.errors.email_not_valid_format}")
                                      String email,
                                      @NotBlank(message = "{users-api.users.errors.first_name_not_valid}")
                                      String firstName,
                                      @NotBlank(message = "{users-api.users.errors.last_name_not_valid}")
                                      String lastName,
                                      @NotNull(message = "{users-api.users.errors.birth_date_empty}")
                                      @AgeLimit(message = "{users-api.users.errors.birth_date_less_then_18}")
                                      LocalDate birthDate,
                                      String address,
                                      @Size(min = 10, max = 10, message = "{users-api.users.errors.phone_not_valid}")
                                      @Pattern(regexp = "^[0-9]*$", message = "")
                                      String phoneNumber) {
}