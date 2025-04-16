package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Posts {
    private int id;
    private final StringProperty title;
    private final StringProperty content;
    private final StringProperty image; // New field for image path

    // Constructor with ID and image
    public Posts(int id, String title, String content, String image) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.image = new SimpleStringProperty(image);
    }

    // Constructor without ID
    public Posts(String title, String content, String image) {
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.image = new SimpleStringProperty(image);
    }

    // Empty constructor
    public Posts() {
        this.title = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.image = new SimpleStringProperty();
    }

    @Override
    public String toString() {
        return "Posts{" +
                "id=" + id +
                ", title='" + title.get() + '\'' +
                ", content='" + content.get() + '\'' +
                ", image='" + image.get() + '\'' +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
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

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public StringProperty imageProperty() {
        return image;
    }
}