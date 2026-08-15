package com.scholaris;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

/**
 * Detail screen for a single scholarship: title, website button, and overview.
 * Tags and the "Eligibility Criteria" section have been removed per PRD §8.
 * Previous/Next controls live in the header (NavBar) and wrap around.
 */
public class ScholarshipDetailScreen extends VBox {

    public ScholarshipDetailScreen(List<Scholarship> scholarships, int index) {
        Scholarship s = scholarships.get(index);
        int size      = scholarships.size();
        int prevIndex = (index - 1 + size) % size;
        int nextIndex = (index + 1) % size;

        getStyleClass().add("screen");
        setAlignment(Pos.TOP_CENTER);

        NavBar nav = new NavBar(
            () -> Main.switchScreen(new ScholarshipDetailScreen(scholarships, prevIndex)),
            () -> Main.switchScreen(new ScholarshipDetailScreen(scholarships, nextIndex))
        );
        VBox.setMargin(nav, new Insets(36, 0, 0, 0));
        getChildren().add(nav);

        VBox outer = new VBox(16);
        outer.setMaxWidth(760);
        outer.setPadding(new Insets(40, 20, 60, 20));
        outer.setAlignment(Pos.TOP_LEFT);

        // "← Back" returns to ProfileScreen so the user can re-run the match
        // with different inputs (MatchesScreen requires a profile, so we can't
        // reconstruct it here without holding a reference — ProfileScreen is the
        // clean entry point).
        Hyperlink back = new Hyperlink("\u2190 Back to My Matches");
        back.getStyleClass().add("nav-link");
        back.setOnAction(e -> Main.switchScreen(new ProfileScreen()));

        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(40));

        // Tags removed per PRD §8

        Label title = new Label(s.getTitle());
        title.getStyleClass().add("hero-heading-small");
        title.setWrapText(true);

        Button visitBtn = new Button("Visit Scholarship Website  \u2192");
        visitBtn.getStyleClass().add("primary-button");
        visitBtn.setOnAction(e -> openLink(s.getWebsiteUrl()));

        Label overviewTitle = new Label("Overview");
        overviewTitle.getStyleClass().add("section-title-small");

        Label overview = new Label(s.getOverview());
        overview.getStyleClass().add("card-desc");
        overview.setWrapText(true);

        // Eligibility Criteria section removed per PRD §8

        card.getChildren().addAll(title, visitBtn, overviewTitle, overview);

        outer.getChildren().addAll(back, card);

        VBox wrapper = new VBox(outer);
        wrapper.setAlignment(Pos.CENTER);
        getChildren().add(wrapper);
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            System.out.println("Could not open link: " + url);
        }
    }
}
