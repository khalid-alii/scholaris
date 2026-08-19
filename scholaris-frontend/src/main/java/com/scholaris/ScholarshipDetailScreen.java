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
 *
 * The StudentProfile is carried through so that "← Back to My Matches" can
 * reconstruct the exact same MatchesScreen (same profile → same filtered &
 * ranked list) without re-running the form.
 */
public class ScholarshipDetailScreen extends VBox {

    public ScholarshipDetailScreen(List<Scholarship> scholarships, int index,
                                   StudentProfile profile) {
        Scholarship s = scholarships.get(index);
        int size      = scholarships.size();
        int prevIndex = (index - 1 + size) % size;
        int nextIndex = (index + 1) % size;

        getStyleClass().add("screen");
        setAlignment(Pos.TOP_CENTER);

        // Pass the profile along so Prev/Next also carry it forward
        NavBar nav = new NavBar(
            () -> Main.switchScreen(new ScholarshipDetailScreen(scholarships, prevIndex, profile)),
            () -> Main.switchScreen(new ScholarshipDetailScreen(scholarships, nextIndex, profile))
        );
        VBox.setMargin(nav, new Insets(36, 0, 0, 0));
        getChildren().add(nav);

        VBox outer = new VBox(16);
        outer.setMaxWidth(650);
        outer.setPadding(new Insets(40, 20, 60, 20));
        outer.setAlignment(Pos.TOP_LEFT);

        // "← Back" reconstructs MatchesScreen with the same profile →
        // the repository reruns the same query → the same list comes back.
        Hyperlink back = new Hyperlink("\u2190 Back to My Matches");
        back.getStyleClass().add("nav-link");
        back.setOnAction(e -> Main.switchScreen(new MatchesScreen(profile)));

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
