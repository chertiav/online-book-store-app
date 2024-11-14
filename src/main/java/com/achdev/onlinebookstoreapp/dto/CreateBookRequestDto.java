package com.achdev.onlinebookstoreapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookRequestDto {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Author is mandatory")
    private String author;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
            message = "Invalid ISBN format")
    private String isbn;

    @Digits(integer = 6,
            fraction = 2,
            message = "Price should be a valid decimal number")
    @Min(value = 0, message = "Price must be 0.00 or greater than zero")
    private BigDecimal price;

    private String description;
    private String coverImage;
}
