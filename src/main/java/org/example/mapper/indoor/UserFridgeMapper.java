package org.example.mapper.indoor;

import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserFridgeMapper {

    public UserFridge toEntity(UserFridgeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        UserFridge fridge = new UserFridge();
        fridge.setUserId(dto.getUserId());
        fridge.setIngredientName(dto.getIngredientName());
        fridge.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : new BigDecimal("1.00"));
        fridge.setUnit(dto.getUnit() != null ? dto.getUnit() : "gram");
        fridge.setPurchaseDate(dto.getPurchaseDate());
        fridge.setExpiryDate(dto.getExpiryDate());
        fridge.setStorageType(dto.getStorageType() != null ? dto.getStorageType() : UserFridge.StorageType.COOLER);
        fridge.setCategoryTag(dto.getCategoryTag());
        fridge.setNotifiedFlag(false);

        return fridge;
    }

    public UserFridgeResponseDto toResponseDto(UserFridge entity) {
        if (entity == null) {
            return null;
        }

        UserFridgeResponseDto dto = new UserFridgeResponseDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setIngredientName(entity.getIngredientName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnit(entity.getUnit());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setNotifiedFlag(entity.getNotifiedFlag());
        dto.setStorageType(entity.getStorageType());
        dto.setCategoryTag(entity.getCategoryTag());

        return dto;
    }
}