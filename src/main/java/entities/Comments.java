package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDateTime;

public class Comments {
    private int id;
    private final StringProperty content;
    private int postId;
    private LocalDateTime createdAt;

    // Constructor with all fields
    public Comments(int id, String content, int postId, LocalDateTime createdAt) {
        this.id = id;
        this.content = new SimpleStringProperty(content);
        this.postId = postId;
        this.createdAt = createdAt;
    }

    // Constructor without ID
    public Comments(String content, int postId) {
        this.content = new SimpleStringProperty(content);
        this.postId = postId;
    }

    // Empty constructor
    public Comments() {
        this.content = new SimpleStringProperty();
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", content='" + content.get() + '\'' +
                ", postId=" + postId +
                ", createdAt=" + createdAt +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}