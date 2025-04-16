package services;

import entities.Category;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private Connection cnx = MyDatabase.getInstance().getCnx();

    // Récupérer toutes les catégories
    public List<Category> recuperer() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("parent_id")
                );
                categories.add(category);
            }
        }
        return categories;
    }

    // Récupérer les sous-catégories d'une catégorie donnée
    public List<Category> recupererSousCategories(int parentId) throws SQLException {
        List<Category> subCategories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE parent_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, parentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("parent_id")
                );
                subCategories.add(category);
            }
        }
        return subCategories;
    }
}