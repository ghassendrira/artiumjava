package entities;

public class Quiz {
    private int id;
    private int courseId; // Lien avec le cours
    private String title;

    public Quiz() {}

    public Quiz(int id, int courseId, String title) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}