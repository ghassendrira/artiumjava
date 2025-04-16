package controllers;

import entities.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.QuizService;

import java.io.IOException;
import java.sql.SQLException;

public class QuizManagementController {
    @FXML private Label courseLabel;
    @FXML private TextField titleField;
    @FXML private TableView<Quiz> quizTable; // Ajout de la déclaration pour quizTable
    @FXML private TableColumn<Quiz, String> colTitle; // Ajout de la déclaration pour colTitle

    private final QuizService quizService = new QuizService();
    private ObservableList<Quiz> quizList = FXCollections.observableArrayList(); // Ajout de la déclaration pour quizList
    private int courseId;

    public void setCourse(int courseId, String courseTitle) {
        this.courseId = courseId;
        courseLabel.setText("Cours : " + courseTitle);
        loadQuizzes();
    }

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        quizTable.setItems(quizList);
    }

    private void loadQuizzes() {
        try {
            quizList.setAll(quizService.recupererParCours(courseId));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les quiz : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterQuiz() {
        String title = titleField.getText();
        if (title.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un titre !");
            return;
        }

        Quiz quiz = new Quiz(0, courseId, title);
        try {
            quizService.ajouter(quiz);
            loadQuizzes();
            titleField.clear();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l’ajout : " + e.getMessage());
        }
    }

    @FXML
    public void openQuestionManagement() {
        Quiz selectedQuiz = quizTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert("Erreur", "Veuillez sélectionner un quiz !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/questionManagement.fxml"));
            Scene scene = new Scene(loader.load());

            QuestionManagementController controller = loader.getController();
            controller.setQuiz(selectedQuiz.getId(), selectedQuiz.getTitle());

            Stage stage = new Stage();
            stage.setTitle("Gestion des questions - " + selectedQuiz.getTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l’interface : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}