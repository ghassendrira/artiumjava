package controllers;

import entities.Category;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import services.CategoryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseController {

    @FXML
    private TreeView<Category> categoryTreeView;

    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        try {
            // Charger les catégories depuis la base de données
            List<Category> categories = categoryService.recuperer();

            // Créer une racine virtuelle pour le TreeView
            TreeItem<Category> root = new TreeItem<>(new Category(0, "Catégories", 0));
            root.setExpanded(true);

            // Construire l’arbre dynamiquement
            buildCategoryTree(root, categories, 0);

            // Définir la racine du TreeView
            categoryTreeView.setRoot(root);

            // Listener pour détecter la sélection d’une catégorie
            categoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.isLeaf()) {
                    Category selectedCategory = newValue.getValue();
                    openCourseManagement(selectedCategory);
                }
            });
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage());
        }
    }

    // Méthode récursive pour construire l’arbre des catégories
    private void buildCategoryTree(TreeItem<Category> parentItem, List<Category> categories, int parentId) {
        for (Category category : categories) {
            if (category.getParentId() == parentId) {
                TreeItem<Category> item = new TreeItem<>(category);
                parentItem.getChildren().add(item);
                buildCategoryTree(item, categories, category.getId());
            }
        }
    }

    // Ouvre l’interface de gestion des cours
    private void openCourseManagement(Category selectedCategory) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/courseManagement.fxml"));
            Scene scene = new Scene(loader.load());

            // Assumer que CourseManagementController existe et a une méthode setCategory
            CourseManagementController controller = loader.getController();
            controller.setCategory(selectedCategory.getId(), selectedCategory.getName());

            Stage stage = new Stage();
            stage.setTitle("Gestion des cours - " + selectedCategory.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l’interface : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte en cas d’erreur
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}