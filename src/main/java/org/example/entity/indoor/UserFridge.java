package org.example.entity.indoor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.example.entity.common.User;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "user_fridges")
public class UserFridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal quantity = new java.math.BigDecimal("1.00");

    @Column(length = 50)
    private String unit = "gram";

    public enum StorageType { COOLER, FREEZER }

    public void setStorageType(String storageType) { this.storageType = storageType; }

    public void setStorageType(StorageType type) {
        this.storageType = type == null ? null : type.name();
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user == null ? null : user.getId();
    }

    // Alias retained for the teammate's DTO mapper.
    public boolean getNotifiedFlag() { return notifiedFlag; }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "notified_flag")
    private boolean notifiedFlag;

    @Column(name = "storage_type", columnDefinition = "enum('COOLER','FREEZER')")
    private String storageType = "COOLER";

    @Column(name = "category_tag", length = 100)
    private String categoryTag;

}
