package services;

import entities.Comments;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService implements Service<Comments> {

    private Connection cnx;

    public CommentService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Comments comment) throws SQLException {
        String sql = "INSERT INTO comments (content, post_id) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, comment.getContent());
            pst.setInt(2, comment.getPostId());
            pst.executeUpdate();
            System.out.println("Commentaire ajouté avec succès !");
        }
    }

    @Override
    public void modifier(Comments comment) throws SQLException {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, comment.getContent());
            pst.setInt(2, comment.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Commentaire mis à jour avec succès !");
            } else {
                System.out.println("Aucun commentaire trouvé avec cet ID !");
            }
        }
    }

    @Override
    public void supprimer(Comments comment) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, comment.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Commentaire supprimé avec succès !");
            } else {
                System.out.println("Aucun commentaire trouvé avec cet ID !");
            }
        }
    }

    @Override
    public List<Comments> recuperer() throws SQLException {
        List<Comments> commentsList = new ArrayList<>();
        String sql = "SELECT * FROM comments";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Comments comment = new Comments();
                comment.setId(rs.getInt("id"));
                comment.setContent(rs.getString("content"));
                comment.setPostId(rs.getInt("post_id"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                commentsList.add(comment);
            }
        }
        return commentsList;
    }

    // Method to retrieve comments for a specific post
    public List<Comments> recupererByPostId(int postId) throws SQLException {
        List<Comments> commentsList = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, postId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Comments comment = new Comments();
                    comment.setId(rs.getInt("id"));
                    comment.setContent(rs.getString("content"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    commentsList.add(comment);
                }
            }
        }
        return commentsList;
    }
}