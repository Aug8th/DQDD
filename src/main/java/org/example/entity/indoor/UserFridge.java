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

@Entity
@Table(name = "user_fridges")
public class UserFridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "notified_flag")
    private boolean notifiedFlag;

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setNotifiedFlag(boolean notifiedFlag) {
        this.notifiedFlag = notifiedFlag;
    }
}
