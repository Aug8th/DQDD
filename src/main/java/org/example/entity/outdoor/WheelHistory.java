package org.example.entity.outdoor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wheel_history")
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

    public WheelHistory() {
    }

    public WheelHistory(
            Integer id,
            CustomWheel wheel,
            WheelItem oldItem,
            WheelItem newItem,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.wheel = wheel;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomWheel getWheel() {
        return wheel;
    }

    public void setWheel(CustomWheel wheel) {
        this.wheel = wheel;
    }

    public WheelItem getOldItem() {
        return oldItem;
    }

    public void setOldItem(WheelItem oldItem) {
        this.oldItem = oldItem;
    }

    public WheelItem getNewItem() {
        return newItem;
    }

    public void setNewItem(WheelItem newItem) {
        this.newItem = newItem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}