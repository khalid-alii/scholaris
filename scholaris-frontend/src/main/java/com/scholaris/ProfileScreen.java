package com.scholaris;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** "Tell Us About Yourself" profile form screen. */
public class ProfileScreen extends VBox {

    // ── World countries (alphabetical, 195 UN member states + common territories) ──
    private static final ObservableList<String> ALL_COUNTRIES =
        FXCollections.observableArrayList(
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola",
            "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria",
            "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
            "Belarus", "Belgium", "Belize", "Benin", "Bhutan",
            "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
            "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia",
            "Cameroon", "Canada", "Central African Republic", "Chad", "Chile",
            "China", "Colombia", "Comoros", "Congo (Republic)", "Costa Rica",
            "Croatia", "Cuba", "Cyprus", "Czechia", "Democratic Republic of the Congo",
            "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador",
            "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
            "Eswatini", "Ethiopia", "Fiji", "Finland", "France",
            "Gabon", "Gambia", "Georgia", "Germany", "Ghana",
            "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau",
            "Guyana", "Haiti", "Honduras", "Hungary", "Iceland",
            "India", "Indonesia", "Iran", "Iraq", "Ireland",
            "Israel", "Italy", "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan",
            "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia",
            "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar",
            "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
            "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia",
            "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco",
            "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
            "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria",
            "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan",
            "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay",
            "Peru", "Philippines", "Poland", "Portugal", "Qatar",
            "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Vincent and the Grenadines", "Samoa", "San Marino",
            "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia",
            "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia",
            "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan",
            "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden",
            "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand",
            "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia",
            "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine",
            "United Arab Emirates", "United Kingdom", "United States", "Uruguay",
            "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
            "Yemen", "Zambia", "Zimbabwe"
        );

    // ── Field-of-study categories — exact values from the CSV Majors column ──
    private static final ObservableList<String> FIELDS_OF_STUDY =
        FXCollections.observableArrayList(
            "STEM",
            "Business & Economics",
            "Health & Medicine",
            "Arts & Humanities",
            "Law",
            "Education",
            "Other"
        );

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

        // ── Age & GPA (plain text fields, unchanged) ──────────────────────────
        TextField ageField = new TextField();
        ageField.setPromptText("e.g. 21");

        TextField gpaField = new TextField();
        gpaField.setPromptText("e.g. 3.85");

        // ── Nationality: searchable ComboBox ──────────────────────────────────
        FilteredList<String> filteredCountries =
                new FilteredList<>(ALL_COUNTRIES, p -> true);

        ComboBox<String> nationalityBox = new ComboBox<>(filteredCountries);
        nationalityBox.setEditable(true);
        nationalityBox.setPromptText("e.g. Philippines");
        nationalityBox.setMaxWidth(Double.MAX_VALUE);
        nationalityBox.setVisibleRowCount(8);

        // Filter the list as the user types; skip re-filtering when an item
        // was just selected (the editor text would equal the selected item).
        nationalityBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            String selectedItem = nationalityBox.getSelectionModel().getSelectedItem();

            // User selected an item — the editor was set programmatically; reset filter.
            if (selectedItem != null && selectedItem.equals(newVal)) {
                filteredCountries.setPredicate(p -> true);
                return;
            }

            // User is typing — apply filter.
            String filter = (newVal == null) ? "" : newVal.toLowerCase().trim();
            filteredCountries.setPredicate(country ->
                filter.isEmpty() || country.toLowerCase().contains(filter));

            // Keep the dropdown open while typing.
            if (!nationalityBox.isShowing() && !filter.isEmpty()) {
                nationalityBox.show();
            }
        });

        // ── Field of Study: simple fixed-list ComboBox ────────────────────────
        ComboBox<String> fieldBox = new ComboBox<>(FIELDS_OF_STUDY);
        fieldBox.setEditable(false);
        fieldBox.setPromptText("Select your field of study");
        fieldBox.setMaxWidth(Double.MAX_VALUE);
        fieldBox.setVisibleRowCount(7);

        // ── Match Me button ───────────────────────────────────────────────────
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

            // Nationality: read directly from the editor (handles both typed & selected)
            String nationality = nationalityBox.getEditor().getText().trim();

            // Field of Study: must have a selection
            String field = fieldBox.getValue();
            if (field == null || field.isBlank()) {
                showError("Please select your field of study.");
                return;
            }

            StudentProfile profile = new StudentProfile(age, gpa, nationality, field);
            Main.switchScreen(new MatchesScreen(profile));
        });

        card.getChildren().addAll(
            title, subtitle,
            labeledField("Age",            ageField),
            labeledField("GPA",            gpaField),
            labeledField("Nationality",    nationalityBox),
            labeledField("Field of Study", fieldBox),
            matchBtn
        );

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(50, 20, 20, 20));
        getChildren().add(wrapper);
    }

    /**
     * Wraps any Control (TextField or ComboBox) in a labelled VBox row,
     * applying the existing field styling so the layout stays consistent.
     */
    private VBox labeledField(String labelText, Control field) {
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
