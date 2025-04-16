package controllers;

import entities.Question;
import entities.Answer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.QuestionService;
import services.AnswerService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionManagementController {
    @FXML private Label quizLabel;
    @FXML private TextField questionTextField;
    @FXML private TextField answerTextField;
    @FXML private CheckBox isCorrectCheckBox;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, String> colQuestion;
    @FXML private TableColumn<Question, String> colAnswers;

    private final QuestionService questionService = new QuestionService();
    private final AnswerService answerService = new AnswerService();
    private ObservableList<Question> questionList = FXCollections.observableArrayList();
    private int quizId;

    public void setQuiz(int quizId, String quizTitle) {
        this.quizId = quizId;
        quizLabel.setText("Quiz : " + quizTitle);
        loadQuestions();
    }

    @FXML
    public void initialize() {
        colQuestion.setCellValueFactory(new PropertyValueFactory<>("text"));
        colAnswers.setCellValueFactory(cellData -> {
            try {
                // Vérification de nullité pour éviter NullPointerException
                if (cellData == null || cellData.getValue() == null) {
                    return new SimpleStringProperty("Données invalides");
                }
                List<Answer> answers = answerService.recupererParQuestion(cellData.getValue().getId());
                if (answers == null || answers.isEmpty()) {
                    return new SimpleStringProperty("Aucune réponse");
                }
                String answerText = answers.stream()
                        .map(a -> a.getText() + (a.isCorrect() ? " (Correcte)" : ""))
                        .collect(Collectors.joining(", "));
                return new SimpleStringProperty(answerText);
            } catch (SQLException e) {
                return new SimpleStringProperty("Erreur : " + e.getMessage());
            }
        });
        questionTable.setItems(questionList);
    }

    private void loadQuestions() {
        try {
            questionList.setAll(questionService.recupererParQuiz(quizId));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les questions : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterQuestionEtReponse() {
        String questionText = questionTextField.getText();
        String answerText = answerTextField.getText();
        boolean isCorrect = isCorrectCheckBox.isSelected();

        if (questionText.isEmpty() || answerText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            Question question = new Question(0, quizId, questionText);
            questionService.ajouter(question);

            Answer answer = new Answer(0, question.getId(), answerText, isCorrect);
            answerService.ajouter(answer);

            loadQuestions();
            questionTextField.clear();
            answerTextField.clear();
            isCorrectCheckBox.setSelected(false);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l’ajout : " + e.getMessage());
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