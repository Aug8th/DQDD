package org.example.entity.outdoor;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.WheelType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_wheels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomWheel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheel_type", nullable = false)
    private WheelType wheelType;

    @Column(length = 255)
    private String name;

    @OneToMany(
            mappedBy = "wheel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WheelItem> items = new ArrayList<>();
}