package org.example;

import Db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;


public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Test database connection first
        testDatabaseConnection();

        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Set the scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.setResizable(false);

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace(); // This will print the full stack trace
        }
    }

    private void testDatabaseConnection() {
        try {
            Connection connection = Database.getConnection();
            if (connection == null) {
                System.out.println("Warning: Could not connect to database at startup.");
            } else {
                System.out.println("Database connection successful.");
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not connect to database at startup.");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}