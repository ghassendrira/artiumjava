package services;

import entities.Question;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {
    // Connexion à la base de données via le singleton MyDatabase
    private Connection cnx = MyDatabase.getInstance().getCnx();

    /**
     * Ajoute une nouvelle question dans la base de données.
     * @param question L'objet Question à ajouter
     * @throws SQLException En cas d'erreur SQL
     */
    public void ajouter(Question question) throws SQLException {
        String sql = "INSERT INTO question (quiz_id, text) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, question.getQuizId());
            pst.setString(2, question.getText());
            pst.executeUpdate();
            // Récupération de l'ID généré automatiquement
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                question.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Récupère toutes les questions associées à un quiz donné.
     * @param quizId L'identifiant du quiz
     * @return Une liste d'objets Question
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Question> recupererParQuiz(int quizId) throws SQLException {
        List<Question> questionList = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE quiz_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, quizId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getInt("quiz_id"),
                        rs.getString("text")
                );
                questionList.add(question);
            }
        }
        return questionList;
    }
}