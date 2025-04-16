package services;

import entities.Course;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseService {

    private Connection cnx;

    public CourseService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Course course) throws SQLException {
        String sql = "INSERT INTO course (title, description, filePath, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, course.getTitle());
            pst.setString(2, course.getDescription());
            pst.setString(3, course.getFilePath());
            pst.setInt(4, course.getCategoryId());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                course.setId(rs.getInt(1));
            }
            System.out.println("Nouvel enregistrement ajouté avec ID : " + course.getId());
        }
    }

    public void modifier(Course course) throws SQLException {
        System.out.println("Tentative de modification de l'enregistrement avec ID : " + course.getId());
        String sql = "UPDATE course SET title = ?, description = ?, filePath = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, course.getTitle());
            pst.setString(2, course.getDescription());
            pst.setString(3, course.getFilePath());
            pst.setInt(4, course.getCategoryId());
            pst.setInt(5, course.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Aucun enregistrement trouvé avec l'ID : " + course.getId());
            }
            System.out.println("Enregistrement avec ID " + course.getId() + " mis à jour avec succès.");
        }
    }

    public void supprimer(Course course) throws SQLException {
        String sql = "DELETE FROM course WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, course.getId());
            pst.executeUpdate();
        }
    }

    public List<Course> recuperer() throws SQLException {
        List<Course> courseList = new ArrayList<>();
        String sql = "SELECT * FROM course";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setTitle(rs.getString("title"));
                course.setDescription(rs.getString("description"));
                course.setFilePath(rs.getString("filePath"));
                course.setCategoryId(rs.getInt("category_id"));
                courseList.add(course);
            }
        }
        return courseList;
    }
}