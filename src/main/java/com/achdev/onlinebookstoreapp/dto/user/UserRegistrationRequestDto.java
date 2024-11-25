package com.achdev.onlinebookstoreapp.dto.user;

import com.achdev.onlinebookstoreapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(
        firstField = "password",
        secondField = "repeatPassword",
        message = "Passwords do not match"
)
public class UserRegistrationRequestDto {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Length(min = 8, max = 35,
            message = "Password must be between 8 and 35 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*+=]).{8,35}$",
            message = "Password must contain at least one digit, one lowercase letter, "
                      + "one uppercase letter, and one special character"
    )
    private String password;

    private String repeatPassword;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private String shippingAddress;
}
