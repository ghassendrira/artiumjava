package controllers;

import entities.Course; // Assurez-vous d'avoir une classe Course
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CourseService; // Assurez-vous d'avoir un service CourseService

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CourseManagementController {

    @FXML private Label categoryLabel;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private Label fileLabel;
    @FXML private TextField searchField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colTitle;
    @FXML private TableColumn<Course, String> colDescription;
    @FXML private TableColumn<Course, String> colFilePath;

    private final CourseService courseService = new CourseService();
    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private ObservableList<Course> filteredCourseList = FXCollections.observableArrayList();
    private File selectedFile;
    private int categoryId;

    public void setCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        categoryLabel.setText("Catégorie : " + categoryName);
        loadCourses();
    }

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleField.setText(newSelection.getTitle());
                descriptionField.setText(newSelection.getDescription());
                fileLabel.setText(newSelection.getFilePath() != null ? newSelection.getFilePath() : "Aucun fichier");
                selectedFile = null;
            }
        });
    }

    private void loadCourses() {
        try {
            List<Course> courses = courseService.recuperer().stream()
                    .filter(course -> course.getCategoryId() == categoryId)
                    .collect(Collectors.toList());
            courseList.setAll(courses);
            filteredCourseList.setAll(courses);
            courseTable.setItems(filteredCourseList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage());
        }
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    public void ajouterCourse() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String filePath = saveFile(selectedFile);

        if (title.isEmpty() || description.isEmpty() || filePath == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs et sélectionner un fichier !");
            return;
        }

        Course newCourse = new Course(0, title, description, filePath, categoryId);
        try {
            courseService.ajouter(newCourse);
            loadCourses();
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    public void modifierCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours à modifier !");
            return;
        }

        selectedCourse.setTitle(titleField.getText());
        selectedCourse.setDescription(descriptionField.getText());

        if (selectedFile != null) {
            String filePath = saveFile(selectedFile);
            if (filePath != null) {
                String oldFilePath = selectedCourse.getFilePath();
                if (oldFilePath != null) {
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                selectedCourse.setFilePath(filePath);
            }
        }

        try {
            courseService.modifier(selectedCourse);
            loadCourses();
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours !");
            return;
        }

        try {
            String filePath = selectedCourse.getFilePath();
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            courseService.supprimer(selectedCourse);
            loadCourses();
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    public void searchCourses() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            filteredCourseList.setAll(courseList);
        } else {
            filteredCourseList.setAll(courseList.stream()
                    .filter(course -> course.getTitle().toLowerCase().contains(searchText) ||
                            course.getDescription().toLowerCase().contains(searchText))
                    .collect(Collectors.toList()));
        }
        courseTable.setItems(filteredCourseList);
    }

    @FXML
    public void clearFields() {
        titleField.clear();
        descriptionField.clear();
        fileLabel.setText("Aucun fichier sélectionné");
        selectedFile = null;
        courseTable.getSelectionModel().clearSelection();
    }

    /** Nouvelle méthode pour gérer les quiz */
    @FXML
    public void openQuizManagement() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizManagement.fxml"));
            Scene scene = new Scene(loader.load());

            QuizManagementController controller = loader.getController();
            controller.setCourse(selectedCourse.getId(), selectedCourse.getTitle());

            Stage stage = new Stage();
            stage.setTitle("Gestion des quiz - " + selectedCourse.getTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l’interface : " + e.getMessage());
        }
    }

    private String saveFile(File file) {
        if (file == null) return null;

        String destinationFolder = "uploads/";
        File destDir = new File(destinationFolder);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String fileName = file.getName();
        File destFile = new File(destinationFolder + fileName);
        try (InputStream in = new FileInputStream(file);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la copie du fichier : " + e.getMessage());
            return null;
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