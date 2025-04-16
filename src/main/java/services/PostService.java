package services;

import entities.Posts;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService implements Service<Posts> {

    private Connection cnx;

    public PostService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Posts posts) throws SQLException {
        String sql = "INSERT INTO posts (title, content, image) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, posts.getTitle());
            pst.setString(2, posts.getContent());
            pst.setString(3, posts.getImage());
            pst.executeUpdate();
            System.out.println("Post ajouté avec succès !");
        }
    }

    @Override
    public void modifier(Posts posts) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ?, image = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, posts.getTitle());
            pst.setString(2, posts.getContent());
            pst.setString(3, posts.getImage());
            pst.setInt(4, posts.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Post mis à jour avec succès !");
            } else {
                System.out.println("Aucun post trouvé avec cet ID !");
            }
        }
    }

    @Override
    public void supprimer(Posts posts) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, posts.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Post supprimé avec succès !");
            } else {
                System.out.println("Aucun post trouvé avec cet ID !");
            }
        }
    }

    @Override
    public List<Posts> recuperer() throws SQLException {
        List<Posts> postsList = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Posts post = new Posts();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setImage(rs.getString("image"));
                postsList.add(post);
            }
        }
        return postsList;
    }
}