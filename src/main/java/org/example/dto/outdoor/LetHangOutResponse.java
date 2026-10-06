package org.example.dto.outdoor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LetHangOutResponse {

    private Integer foodId;
    private String foodName;
    private Integer locationId;
    private String locationName;
    private String address;
    private Float ggMapsRating;
}