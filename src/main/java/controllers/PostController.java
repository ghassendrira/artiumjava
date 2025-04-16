package controllers;

import entities.Comments;
import entities.Posts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import services.CommentService;
import services.PostService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostController {

    @FXML private TextField titleField;
    @FXML private TextArea contentField;
    @FXML private ImageView imageView;
    @FXML private Button uploadImageButton;
    @FXML private VBox postsContainer;

    private final PostService postService = new PostService();
    private final CommentService commentService = new CommentService();
    private String imagePath;

    @FXML
    public void initialize() {
        loadPosts();
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();
        try {
            for (Posts post : postService.recuperer()) {
                postsContainer.getChildren().add(createPostBox(post));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du chargement des posts : " + e.getMessage());
        }
    }

    private VBox createPostBox(Posts post) {
        VBox postBox = new VBox(8);
        postBox.getStyleClass().add("post-box");

        // Post Header (Title + Timestamp)
        Text title = new Text(post.getTitle());
        title.getStyleClass().add("post-header");
        Text timestamp = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
        timestamp.getStyleClass().add("post-timestamp");
        HBox header = new HBox(10, title, timestamp);

        // Post Content
        Text content = new Text(post.getContent());
        content.getStyleClass().add("post-content");

        // Post Image
        ImageView postImage = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            postImage.setImage(new Image(new File(post.getImage()).toURI().toString()));
            postImage.setFitWidth(300);
            postImage.setFitHeight(200);
            postImage.setPreserveRatio(true);
            postImage.getStyleClass().add("post-image");
        }

        // Interaction Buttons
        Button likeButton = new Button("Like");
        likeButton.getStyleClass().add("interaction-button");
        Button commentButton = new Button("Comment");
        commentButton.getStyleClass().add("interaction-button");
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("interaction-button");
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("interaction-button");
        HBox interactions = new HBox(10, likeButton, commentButton, editButton, deleteButton);

        // Comments Section
        VBox commentsBox = new VBox(5);
        commentsBox.getStyleClass().add("comments-box");
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Écrire un commentaire...");
        commentInput.getStyleClass().add("comment-input");
        commentInput.setPrefHeight(40);
        Button commentSubmit = new Button("Envoyer");
        commentSubmit.getStyleClass().add("comment-button");

        // Load existing comments
        try {
            for (Comments comment : commentService.recupererByPostId(post.getId())) {
                Text commentText = new Text(comment.getContent());
                commentText.getStyleClass().add("comment-text");
                commentsBox.getChildren().add(commentText);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du chargement des commentaires : " + e.getMessage());
        }

        // Comment submission
        commentSubmit.setOnAction(e -> {
            String commentContent = commentInput.getText();
            if (!commentContent.isEmpty()) {
                Comments newComment = new Comments(commentContent, post.getId());
                try {
                    commentService.ajouter(newComment);
                    Text commentText = new Text(commentContent);
                    commentText.getStyleClass().add("comment-text");
                    commentsBox.getChildren().add(commentText);
                    commentInput.clear();
                } catch (SQLException ex) {
                    showAlert("Erreur", "Échec de l'ajout du commentaire : " + ex.getMessage());
                }
            }
        });

        // Edit and Delete actions
        editButton.setOnAction(e -> {
            titleField.setText(post.getTitle());
            contentField.setText(post.getContent());
            imagePath = post.getImage();
            if (imagePath != null) {
                imageView.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                postService.supprimer(post);
                loadPosts();
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de la suppression : " + ex.getMessage());
            }
        });

        commentsBox.getChildren().addAll(commentInput, commentSubmit);
        postBox.getChildren().addAll(header, content, postImage, interactions, commentsBox);
        return postBox;
    }

    @FXML
    public void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                Path imagesDir = Paths.get("images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectory(imagesDir);
                }
                Path targetPath = imagesDir.resolve(file.getName());
                Files.copy(file.toPath(), targetPath);
                imagePath = targetPath.toString();
                imageView.setImage(new Image(file.toURI().toString()));
            } catch (Exception e) {
                showAlert("Erreur", "Échec du téléchargement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    public void ajouterPost() {
        String title = titleField.getText();
        String content = contentField.getText();
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir les champs titre et contenu !");
            return;
        }

        Posts newPost = new Posts(title, content, imagePath);
        try {
            postService.ajouter(newPost);
            loadPosts();
            titleField.clear();
            contentField.clear();
            imageView.setImage(null);
            imagePath = null;
            showAlert("Succès", "Post publié avec succès !");
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'ajout du post : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}