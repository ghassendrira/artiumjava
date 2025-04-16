package services;

import entities.Quiz;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService {
    private Connection cnx = MyDatabase.getInstance().getCnx();

    public void ajouter(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quiz (course_id, title) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, quiz.getCourseId());
            pst.setString(2, quiz.getTitle());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                quiz.setId(rs.getInt(1));
            }
        }
    }

    public List<Quiz> recupererParCours(int courseId) throws SQLException {
        List<Quiz> quizList = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE course_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, courseId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz(rs.getInt("id"), rs.getInt("course_id"), rs.getString("title"));
                quizList.add(quiz);
            }
        }
        return quizList;
    }
}