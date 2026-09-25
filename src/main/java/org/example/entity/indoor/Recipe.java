package org.example.entity.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "prep_time")
    private Integer prepTime;
    private Integer servings;

    @Column(length = 50)
    private String difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}