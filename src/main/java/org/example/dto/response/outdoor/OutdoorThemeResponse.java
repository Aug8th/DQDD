package org.example.dto.response.outdoor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.entity.outdoor.OutdoorTheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutdoorThemeResponse {

    private Integer id;
    private String name;

    public static OutdoorThemeResponse fromEntity(OutdoorTheme theme) {
        return new OutdoorThemeResponse(
                theme.getId(),
                theme.getName()
        );
    }
}