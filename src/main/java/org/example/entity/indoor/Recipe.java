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
import java.time.LocalDateTime;
import org.example.entity.common.User;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id")
    private Integer categoryId;

    private String name;

    @Column(name = "prep_time")
    private Integer prepTime;

    private Integer servings;

    @Column(columnDefinition = "enum('dễ','trung bình','khó')")
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    @Column(name = "approval_status", columnDefinition = "enum('pending','approved','rejected')")
    private String approvalStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public Integer getServings() {
        return servings;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }
}
