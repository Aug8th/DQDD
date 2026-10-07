package org.example.entity.outdoor;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wheel_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "wheel_id", nullable = false)
    @JsonIgnore
    private CustomWheel wheel;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "is_excluded")
    private Boolean isExcluded = false;
}