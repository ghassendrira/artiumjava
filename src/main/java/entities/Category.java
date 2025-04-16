package entities;

public class Category {
    private int id;
    private String name;
    private int parentId; // 0 si c'est une catégorie principale

    public Category() {}

    public Category(int id, String name, int parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }

    @Override
    public String toString() {
        return name;
    }
}