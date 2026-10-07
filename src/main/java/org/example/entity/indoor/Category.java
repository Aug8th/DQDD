package org.example.entity.indoor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Category(String name) {
        this.name = name;
    }

    @jakarta.persistence.OneToMany(mappedBy = "category", fetch = jakarta.persistence.FetchType.LAZY)
    private java.util.List<Recipe> recipes = new java.util.ArrayList<>();

}
