package com.scholaris;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** First screen: hero section with the "Find My Scholarship" call to action. */
public class LandingScreen extends VBox {

    public LandingScreen() {
        getStyleClass().add("screen");
        setAlignment(Pos.TOP_CENTER);

        NavBar nav = new NavBar();
        VBox.setMargin(nav, new Insets(36, 0, 0, 0));
        getChildren().add(nav);

        VBox hero = new VBox(18);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(110, 60, 60, 60));
        hero.setMaxWidth(700);

        Label tag = new Label("\u2726 Next-Gen Scholar Matching");
        tag.getStyleClass().add("pill-tag");

        Label heading = new Label("Unlock your future.");
        heading.getStyleClass().add("hero-heading");

        Label sub = new Label("Find your scholarship.");
        sub.getStyleClass().add("hero-subheading");

        Label desc = new Label(
            "We match you with scholarships based on your unique academic profile. " +
            "Stop searching, start applying."
        );
        desc.getStyleClass().add("hero-desc");
        desc.setWrapText(true);
        desc.setTextAlignment(TextAlignment.CENTER);

        Button cta = new Button("Find My Scholarship  \u2192");
        cta.getStyleClass().add("primary-button");
        cta.setOnAction(e -> Main.switchScreen(new ProfileScreen()));

        hero.getChildren().addAll(tag, heading, sub, desc, cta);
        getChildren().add(hero);
    }
}
