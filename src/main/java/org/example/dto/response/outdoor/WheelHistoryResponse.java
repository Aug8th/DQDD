package org.example.dto.response.outdoor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelHistoryResponse {

    private Integer id;
    private Integer wheelId;
    private Integer oldItemId;
    private String oldItemName;
    private Integer newItemId;
    private String newItemName;
    private LocalDateTime createdAt;
}