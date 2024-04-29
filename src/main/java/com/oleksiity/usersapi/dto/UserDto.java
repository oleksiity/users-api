package com.oleksiity.usersapi.dto;

import com.oleksiity.usersapi.validation.AgeLimit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{users-api.users.errors.email_not_valid_format}")
    private String email;

    private String firstName;

    private String lastName;

    @AgeLimit(message = "{users-api.users.errors.birth_date_less_then_18}")
    private LocalDate birthDate;

    private String address;

    @Size(min = 10, max = 10, message = "{users-api.users.errors.phone_not_valid}")
    @Pattern(regexp = "^[0-9]*$", message = "{users-api.users.errors.phone_not_valid}")
    private String phoneNumber;
}
