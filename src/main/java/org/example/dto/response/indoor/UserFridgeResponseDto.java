package org.example.dto.response.indoor;

import lombok.Data;
import org.example.entity.indoor.UserFridge;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserFridgeResponseDto {
    private Integer id;
    private Integer userId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private Boolean notifiedFlag;
    private UserFridge.StorageType storageType;
    private String categoryTag;
}