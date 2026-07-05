package com.crystalbank;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.crystalbank.database.DatabaseManager;
import java.io.IOException;

public class MainApp extends Application {

    public static Stage primaryStage;
    private static StackPane mainContainer; // The permanent window frame

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialize database and create tables on first run
        DatabaseManager.initialize();

        // 1. Create a permanent container that holds our pages
        mainContainer = new StackPane();

        // 2. Setup the scene ONCE with standard initial dimensions
        Scene scene = new Scene(mainContainer, 750, 700);
        scene.getStylesheets().add(
                MainApp.class.getResource("/com/crystalbank/styles.css").toExternalForm()
        );

        primaryStage.setTitle("CrystalBank");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        // 3. Load the login screen first
        loadScene("login.fxml", "CrystalBank — Login");

        primaryStage.show();
    }

    // FIX: Removed width and height parameters. The window size will now be preserved!
    public static void loadScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/crystalbank/" + fxml)
            );
            Parent root = loader.load();

            // Clear the previous page and drop the new page inside the same window frame
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(root);

            // Update the window title dynamically
            primaryStage.setTitle(title);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
