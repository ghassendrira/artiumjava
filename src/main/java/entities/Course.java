package entities;

public class Course {
    private int id;
    private String title;
    private String description;
    private String filePath;
    private int categoryId;

    public Course() {}

    public Course(int id, String title, String description, String filePath, int categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.categoryId = categoryId;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}