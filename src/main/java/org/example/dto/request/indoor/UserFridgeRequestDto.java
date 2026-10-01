package org.example.dto.request.indoor;

import lombok.Data;
import org.example.entity.indoor.UserFridge;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserFridgeRequestDto {
    private Integer userId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private UserFridge.StorageType storageType;
    private String categoryTag;
}