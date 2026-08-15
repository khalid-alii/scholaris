# Scholaris - Frontend (JavaFX)

GUI frontend for the Scholaris scholarship-matching app, built with JavaFX
(styled via `app.css` to match the Figma design). This satisfies the
project's "GUI-based (Swing / JavaFX / Any Java Framework)" requirement.

## Requirements
- JDK 17 or higher
- Maven (3.6+)

## How to run
```bash
mvn clean javafx:run
```
That's it - Maven will download JavaFX for you, no manual SDK setup needed.

### Running in IntelliJ IDEA instead
1. Open the `scholaris-frontend` folder as a project (IntelliJ will detect the `pom.xml`).
2. Wait for Maven to finish importing dependencies.
3. Open `Main.java`, right-click it, and choose **Run 'Main.main()'**.
   - If it complains about JavaFX modules, instead add a Maven run
     configuration that runs the goal `javafx:run`.

## Project structure
```
src/main/java/com/scholaris/
  Main.java                     - app entry point, screen switching
  NavBar.java                   - shared top navigation
  LandingScreen.java            - hero / "Find My Scholarship"
  ProfileScreen.java            - profile input form
  MatchesScreen.java            - matched scholarship cards
  ScholarshipDetailScreen.java  - single scholarship detail view
  Scholarship.java              - data model
  ScholarshipRepository.java    - interface the backend should implement
  StaticScholarshipRepository.java - TEMP hardcoded data (delete once backend is ready)
src/main/resources/app.css      - all styling
```

## Handoff to backend teammate
The UI never talks to hardcoded data directly - it goes through the
`ScholarshipRepository` interface. To plug in the real matching logic:

1. Create a new class, e.g. `MatchingScholarshipRepository implements ScholarshipRepository`.
2. Implement `getAllScholarships()` using your file-handling / matching logic
   (this is also where the collections + file I/O rubric requirement can live).
3. In `MatchesScreen.java`, change:
   ```java
   ScholarshipRepository repo = new StaticScholarshipRepository();
   ```
   to:
   ```java
   ScholarshipRepository repo = new MatchingScholarshipRepository(studentProfile);
   ```
4. Wire `ProfileScreen`'s "Match Me" button to pass the entered Age/GPA/
   Nationality/Field of Study into that repository instead of just
   navigating to a static screen (there's a `// TODO(backend)` comment
   marking exactly where).

## Notes for the report / rubric
- **Abstraction**: `ScholarshipRepository` interface
- **Encapsulation**: `Scholarship` model (private fields, getters only)
- **Inheritance/Polymorphism**: not fully used yet on the frontend side -
  recommend the backend introduce an abstract `Scholarship` with subclasses
  (e.g. `MeritScholarship`, `NeedBasedScholarship`) overriding a method like
  `checkEligibility()`, since that's the natural place for it once real data
  exists.
