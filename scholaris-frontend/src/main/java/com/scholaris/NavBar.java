package com.scholaris;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Floating pill-shaped top navigation bar: logo (Home), "My Matches",
 * "How It Works", and optionally a pair of small Previous/Next arrow
 * icons (used on the scholarship detail screen to page through matches).
 */
public class NavBar extends HBox {

    public NavBar() {
        this(null, null);
    }

    /** onPrev/onNext non-null -> shows small round arrow buttons at the right edge. */
    public NavBar(Runnable onPrev, Runnable onNext) {
        getStyleClass().add("nav-pill");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(14, 22, 14, 18));
        setSpacing(28);
        setMaxWidth(880);

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setCursor(javafx.scene.Cursor.HAND);
        logoBox.setOnMouseClicked(e -> Main.switchScreen(new LandingScreen()));

        Label icon = new Label("\uD83C\uDF93"); // graduation cap
        icon.getStyleClass().add("logo-icon");

        Label text = new Label("Scholaris");
        text.getStyleClass().add("logo-text");

        logoBox.getChildren().addAll(icon, text);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink matches = new Hyperlink("My Matches");
        matches.getStyleClass().add("nav-link");
        matches.setOnAction(e -> Main.switchScreen(new ProfileScreen()));

        Hyperlink how = new Hyperlink("How It Works");
        how.getStyleClass().add("nav-link");

        getChildren().addAll(logoBox, spacer, matches, how);

        if (onPrev != null && onNext != null) {
            HBox pager = new HBox(8);
            pager.setAlignment(Pos.CENTER_RIGHT);
            pager.setPadding(new Insets(0, 0, 0, 8));

            Button prevBtn = new Button("\u2190");
            prevBtn.getStyleClass().add("nav-page-btn");
            prevBtn.setOnAction(e -> onPrev.run());

            Button nextBtn = new Button("\u2192");
            nextBtn.getStyleClass().add("nav-page-btn");
            nextBtn.setOnAction(e -> onNext.run());

            pager.getChildren().addAll(prevBtn, nextBtn);
            getChildren().add(pager);
        }
    }
}
