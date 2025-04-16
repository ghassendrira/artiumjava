package services;

import entities.Answer;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerService {
    // Connexion à la base de données via le singleton MyDatabase
    private Connection cnx = MyDatabase.getInstance().getCnx();

    /**
     * Ajoute une nouvelle réponse dans la base de données.
     * @param answer L'objet Answer à ajouter
     * @throws SQLException En cas d'erreur SQL
     */
    public void ajouter(Answer answer) throws SQLException {
        String sql = "INSERT INTO answer (question_id, text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, answer.getQuestionId());
            pst.setString(2, answer.getText());
            pst.setBoolean(3, answer.isCorrect());
            pst.executeUpdate();
            // Récupération de l'ID généré automatiquement
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                answer.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Récupère toutes les réponses associées à une question donnée.
     * @param questionId L'identifiant de la question
     * @return Une liste d'objets Answer
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Answer> recupererParQuestion(int questionId) throws SQLException {
        List<Answer> answerList = new ArrayList<>();
        String sql = "SELECT * FROM answer WHERE question_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, questionId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Answer answer = new Answer(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getString("text"),
                        rs.getBoolean("is_correct")
                );
                answerList.add(answer);
            }
        }
        return answerList;
    }
}