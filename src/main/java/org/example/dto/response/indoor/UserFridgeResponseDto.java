package org.example.dto.response.indoor;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserFridgeResponseDto {
    private Integer id;
    private Integer userId;
    private String ingredientName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private Boolean notifiedFlag;
}