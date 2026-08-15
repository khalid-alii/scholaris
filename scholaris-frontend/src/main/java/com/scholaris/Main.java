package com.scholaris;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point for the Scholaris desktop application.
 * Uses a single StackPane as a "router" - screens are swapped in and out
 * of it instead of opening new windows, similar to how a single-page
 * web app swaps views.
 */
public class Main extends Application {

    private static StackPane rootPane;

    @Override
    public void start(Stage stage) {
        rootPane = new StackPane();
        switchScreen(new LandingScreen());

        Scene scene = new Scene(rootPane, 1100, 750);
        scene.getStylesheets().add(Main.class.getResource("/app.css").toExternalForm());

        stage.setTitle("Scholaris - Find Your Scholarship");
        stage.setScene(scene);
        stage.setMinWidth(820);
        stage.setMinHeight(600);
        stage.show();
    }

    /** Swap the currently displayed screen. Called by nav links / buttons. */
    public static void switchScreen(Parent screen) {
        rootPane.getChildren().setAll(screen);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
