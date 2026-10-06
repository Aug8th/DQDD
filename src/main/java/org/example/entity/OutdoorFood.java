package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outdoor_foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutdoorFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private OutdoorTheme theme;

    @Column(nullable = false, length = 255)
    private String name;
}