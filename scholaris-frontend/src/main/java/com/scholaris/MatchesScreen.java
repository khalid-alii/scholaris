package com.scholaris;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** "Scholarships For You" - list of matched scholarship cards. */
public class MatchesScreen extends VBox {

    public MatchesScreen(StudentProfile profile) {
        getStyleClass().add("screen");
        setAlignment(Pos.TOP_CENTER);

        // NavBar sits outside the scroll area so it stays fixed at the top
        NavBar nav = new NavBar();
        VBox.setMargin(nav, new Insets(36, 0, 0, 0));
        getChildren().add(nav);

        // ── Scrollable content area ───────────────────────────────────────────
        VBox content = new VBox(22);
        content.setPadding(new Insets(46, 60, 60, 60));
        content.setMaxWidth(900);

        Label title = new Label("Scholarships For You");
        title.getStyleClass().add("section-title");

        ScholarshipRepository repo = new MatchingScholarshipRepository(profile);
        List<Scholarship> scholarships = repo.getAllScholarships();

        // Dynamic subtitle: reflects the actual number of matched results
        Label subtitle = new Label(
            "Based on your academic profile, we have identified " +
            scholarships.size() + " highly compatible " +
            (scholarships.size() == 1 ? "opportunity" : "opportunities") + "."
        );
        subtitle.getStyleClass().add("card-subtitle");

        content.getChildren().addAll(title, subtitle);

        if (scholarships.isEmpty()) {
            Label emptyMsg = new Label(
                "No matching scholarships found.\n" +
                "Try adjusting your GPA, age, or field of study."
            );
            emptyMsg.getStyleClass().add("card-subtitle");
            emptyMsg.setWrapText(true);
            emptyMsg.setStyle("-fx-opacity: 0.65; -fx-padding: 24 0 0 0;");
            content.getChildren().add(emptyMsg);
        } else {
            VBox cardList = new VBox(18);
            for (int i = 0; i < scholarships.size(); i++) {
                cardList.getChildren().add(buildCard(scholarships.get(i), scholarships, i));
            }
            content.getChildren().add(cardList);
        }

        // Centre the content column the same way as before
        VBox wrapper = new VBox(content);
        wrapper.setAlignment(Pos.TOP_CENTER);

        // Wrap in a ScrollPane so long lists are reachable
        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);   // content stretches to fill width
        scrollPane.setFitToHeight(false); // height grows with content → scroll appears
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);   // no horizontal bar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // vertical only when needed
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // The ScrollPane fills all remaining vertical space below the NavBar
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    private VBox buildCard(Scholarship s, List<Scholarship> all, int index) {
        VBox card = new VBox(10);
        card.getStyleClass().add("scholarship-card");
        card.setPadding(new Insets(24));

        // Tags removed per PRD §8 — Scholarship model no longer has a tags field

        Label titleLbl = new Label(s.getTitle());
        titleLbl.getStyleClass().add("card-title");

        Label desc = new Label(s.getDescription());
        desc.getStyleClass().add("card-desc");
        desc.setWrapText(true);

        Button viewBtn = new Button("View Details  \u2192");
        viewBtn.getStyleClass().add("secondary-button");
        viewBtn.setOnAction(e -> Main.switchScreen(new ScholarshipDetailScreen(all, index)));

        card.getChildren().addAll(titleLbl, desc, viewBtn);
        return card;
    }
}
