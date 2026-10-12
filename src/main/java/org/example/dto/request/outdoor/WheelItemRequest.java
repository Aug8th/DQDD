package org.example.dto.request.outdoor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelItemRequest {
    private String itemName;
    private Boolean isExcluded;
}