package test;

import entities.Posts;
import services.PostService;
import utils.MyDatabase;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MyDatabase.getInstance();
        PostService postService = new PostService();
        // Use constructor without ID and with image (null for no image)
        Posts newPost = new Posts("Cristiano", "Suiiiiiiiiiii!", null);

        try {
            postService.ajouter(newPost);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}