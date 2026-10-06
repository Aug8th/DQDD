package org.example.dto.response.indoor;

import lombok.Data;
import java.util.List;

@Data
public class UserFridgeListResponseDto {
    private List<UserFridgeResponseDto> items;
    private String notification;
}