package test;

import entities.Course;
import services.CourseService;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        MyDatabase.getInstance();
        CourseService courseService = new CourseService();
        Course newCourse = new Course(0, "Cristiano", "Suiiiiiiiiiii!", "\"C:\\Users\\mdain\\Documents\\chapitre1.pdf\"",7);

        try {
            courseService.ajouter(newCourse);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}