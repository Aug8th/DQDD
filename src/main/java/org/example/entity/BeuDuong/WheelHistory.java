package org.example.entity.BeuDuong;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wheel_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "wheel_id", nullable = false)
    private CustomWheel wheel;

    @ManyToOne
    @JoinColumn(name = "old_item_id", nullable = false)
    private WheelItem oldItem;

    @ManyToOne
    @JoinColumn(name = "new_item_id", nullable = false)
    private WheelItem newItem;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}