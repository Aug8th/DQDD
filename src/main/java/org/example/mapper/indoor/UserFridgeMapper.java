package org.example.mapper.indoor;

import org.example.dto.request.indoor.UserFridgeRequestDto;
import org.example.dto.response.indoor.UserFridgeResponseDto;
import org.example.entity.indoor.UserFridge;
import org.springframework.stereotype.Component;

@Component
public class UserFridgeMapper {

    public UserFridge toEntity(UserFridgeRequestDto dto) {
        if (dto == null) return null;
        UserFridge fridge = new UserFridge();
        fridge.setUserId(dto.getUserId());
        fridge.setIngredientName(dto.getIngredientName());
        fridge.setPurchaseDate(dto.getPurchaseDate());
        fridge.setExpiryDate(dto.getExpiryDate());
        fridge.setNotifiedFlag(dto.getNotifiedFlag() != null ? dto.getNotifiedFlag() : false);
        return fridge;
    }

    public UserFridgeResponseDto toResponseDto(UserFridge entity) {
        if (entity == null) return null;
        UserFridgeResponseDto dto = new UserFridgeResponseDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setIngredientName(entity.getIngredientName());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setNotifiedFlag(entity.getNotifiedFlag());
        return dto;
    }
}