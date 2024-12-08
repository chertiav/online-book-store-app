package com.achdev.onlinebookstoreapp.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCategoryRequestDto {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
}
