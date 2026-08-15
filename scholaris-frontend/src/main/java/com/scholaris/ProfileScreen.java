package com.scholaris;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** "Tell Us About Yourself" profile form screen. */
public class ProfileScreen extends VBox {

    public ProfileScreen() {
        getStyleClass().add("screen");
        setAlignment(Pos.TOP_CENTER);

        NavBar nav = new NavBar();
        VBox.setMargin(nav, new Insets(36, 0, 0, 0));
        getChildren().add(nav);

        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(480);
        card.setPadding(new Insets(36));
        card.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tell Us About Yourself");
        title.getStyleClass().add("card-title");

        Label subtitle = new Label(
            "Fill out your profile details. Our matching algorithm uses these " +
            "criteria to pair you with high-probability opportunities."
        );
        subtitle.getStyleClass().add("card-subtitle");
        subtitle.setWrapText(true);

        TextField ageField = new TextField();
        ageField.setPromptText("e.g. 21");

        TextField gpaField = new TextField();
        gpaField.setPromptText("e.g. 3.85");

        TextField nationalityField = new TextField();
        nationalityField.setPromptText("e.g. United States");

        TextField fieldOfStudy = new TextField();
        fieldOfStudy.setPromptText("e.g. Computer Science & Engineering");

        Button matchBtn = new Button("Match Me  \u2192");
        matchBtn.getStyleClass().add("primary-button");
        matchBtn.setMaxWidth(Double.MAX_VALUE);

        matchBtn.setOnAction(e -> {
            // Parse age
            int age;
            try {
                age = Integer.parseInt(ageField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Please enter a valid age (e.g. 21).");
                return;
            }

            // Parse GPA
            double gpa;
            try {
                gpa = Double.parseDouble(gpaField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Please enter a valid GPA (e.g. 3.85).");
                return;
            }

            String nationality = nationalityField.getText().trim();
            String field       = fieldOfStudy.getText().trim();

            if (field.isEmpty()) {
                showError("Please enter your field of study.");
                return;
            }

            StudentProfile profile = new StudentProfile(age, gpa, nationality, field);
            Main.switchScreen(new MatchesScreen(profile));
        });

        card.getChildren().addAll(
            title, subtitle,
            labeledField("Age", ageField),
            labeledField("GPA", gpaField),
            labeledField("Nationality", nationalityField),
            labeledField("Field of Study", fieldOfStudy),
            matchBtn
        );

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(50, 20, 20, 20));
        getChildren().add(wrapper);
    }

    private VBox labeledField(String labelText, TextField field) {
        Label l = new Label(labelText);
        l.getStyleClass().add("field-label");
        field.getStyleClass().add("text-field");
        return new VBox(4, l, field);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
