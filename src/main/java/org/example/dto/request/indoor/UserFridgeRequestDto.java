package org.example.dto.request.indoor;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserFridgeRequestDto {
    private Integer userId;
    private String ingredientName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private Boolean notifiedFlag;
}