package org.example.dto.response.outdoor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelResponse {

    private Integer id;
    private Integer userId;
    private String wheelType;
    private String name;
    private List<WheelItemResponse> items;
}