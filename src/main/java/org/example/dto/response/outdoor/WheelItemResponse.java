package org.example.dto.response.outdoor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelItemResponse {

    private Integer id;
    private Integer wheelId;
    private String itemName;
    private Boolean isExcluded;
}