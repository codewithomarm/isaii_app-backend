package com.isaiiapp.backend.order.v1.status.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateStatusRequest {

    @NotBlank(message = "Name should not be blank")
    @Size(min = 1, max = 20, message = "Name should be between 1 and 20 characters")
    private String name;

    @NotBlank(message = "Description should not be blank")
    @Size(min = 1, max = 100, message = "Description should be between 1 and 100 characters")
    private String description;
}
