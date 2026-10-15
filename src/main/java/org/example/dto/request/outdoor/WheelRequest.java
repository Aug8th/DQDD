package org.example.dto.request.outdoor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.WheelType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelRequest {
    private Integer userId;
    private WheelType wheelType;
    private String name;
}