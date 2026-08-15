# Scholaris — Backend PRD

**Project:** Scholaris (SDG 4 – Quality Education)
**Course:** BIT1123 / BISE2093 / DIT1113 — Object Oriented Programming, Final Project
**This document covers:** the backend and matching engine only. The frontend (JavaFX) is complete; this PRD defines what needs to be built to replace its hardcoded data with real matching logic.

---

## 1. Overview & Goals

Scholaris is a JavaFX desktop app that takes a student's profile and shows them scholarships they're likely to qualify for. The frontend is done and currently renders 3 hardcoded scholarships through a placeholder repository (`StaticScholarshipRepository`). The goal of this PRD is to define the backend that replaces it: a real matching engine that reads scholarship data from a file, filters and ranks it against a student's profile, and returns it through the same interface the UI already consumes — so the UI needs zero structural changes.

**Definition of done:**
- `StaticScholarshipRepository` is deleted.
- A real repository loads scholarship data from a CSV file and returns a filtered, ranked list.
- The Profile screen's "Match Me" button passes real form data into that repository instead of doing nothing.
- The app satisfies the OOP rubric items listed in §6 (inheritance, polymorphism, abstraction, collections, file I/O).
- The two small UI simplifications in §8 (tag removal, eligibility-section removal) are applied.

## 2. Users & Use Case

Single persona: a student filling in age, GPA, nationality, and field of study, expecting to see scholarships they're realistically eligible for, ranked by fit — not just a dump of every scholarship in the system.

Core flow: `LandingScreen → ProfileScreen → MatchesScreen → ScholarshipDetailScreen`, unchanged from the existing build.

## 3. Scope

**In scope**
- Loading scholarship data from a local CSV file (sample: 5 rows; will later point at a live Google Sheet export).
- Filtering and ranking scholarships against a student profile.
- Wiring `ProfileScreen`'s form fields into that logic.
- The class hierarchy and File I/O needed to satisfy the OOP rubric.
- Removing tags from both `MatchesScreen` and `ScholarshipDetailScreen`.
- Removing the "Eligibility Criteria" section from `ScholarshipDetailScreen`.

**Out of scope (this phase)**
- Live Google Sheets API integration (stretch milestone, §11).
- Nationality-based filtering — nationality is collected for UI polish only and is never used in matching (confirmed decision, see §7).
- User accounts, saved applications, or an application tracker.
- Any change to `app.css` or `NavBar.java`.

## 4. System Architecture & Data Flow

```
scholarships.csv (bundled resource; later: published Google Sheet CSV URL)
        │
        ▼
CSV parser (quote- and newline-aware)  ──►  List<Scholarship>  (loaded via ArrayList)
        │
        ▼
MatchingScholarshipRepository.getAllScholarships()
   1. loads all scholarships from file
   2. filters: scholarship.isEligible(studentProfile)   ← polymorphic call
   3. ranks: sorted by scholarship.fitScore(studentProfile), descending
        │
        ▼
List<Scholarship>  ──►  MatchesScreen  ──►  ScholarshipDetailScreen
```

`ScholarshipRepository`'s method signature (`List<Scholarship> getAllScholarships()`) does not change — the student profile is injected via the repository's constructor, exactly as the original handoff doc proposed. This is the only reason the UI classes need no structural changes beyond §8.

## 5. Data Source & Field Mapping

Sheet columns, exactly as exported: `id, Name, Minimum GPA, Maximum Age, link, Majors, Description`

| CSV Column | Scholarship Field | Notes |
|---|---|---|
| `id` | `id` (String) | Not displayed; kept for stable identity/debugging and future dedup logic. |
| `Name` | `title` (String) | **Must be `.trim()`'d** — sample row 1 has an embedded trailing newline inside the quoted cell. |
| `Minimum GPA` | `minGpa` (double) | Internal — used only for eligibility/ranking, not displayed (§8 removes the section that would have shown it). |
| `Maximum Age` | `maxAge` (int) | Internal only, same as above. |
| `link` | `websiteUrl` (String) | Powers the "Visit Scholarship Website" button. |
| `Majors` | `majors` (`List<String>`) | Comma-separated inside one quoted cell (e.g. `STEM, Business & Economics, ...`). Internal only — no longer rendered as tags (§8), used purely to match against the student's field of study. |
| `Description` | **`overview`** (String) and **`description`** (String) — both derived from this one column | See derivation rule below. |

**Derivation rule for `Description` → `overview` / `description`:**
The sheet has only one long-form text field per scholarship, prefixed with the literal string `"overview: "`. There is no separate short blurb and no eligibility bullet list — and per §8, none is needed anymore.

- `overview` = the `Description` cell with the leading `"overview: "` prefix stripped (case-insensitive) and trimmed. Shown in full on `ScholarshipDetailScreen`.
- `description` (the short card teaser on `MatchesScreen`) = the same source text, truncated to the first sentence (up to and including the first `.`), or if that's longer than ~140 characters, truncated to ~140 characters at the nearest word boundary with `…` appended.

**Critical parsing requirement — do not use naive `String.split(",")`:**
Two things in this data will silently corrupt a naive parser:
1. The `Majors` and `Description` fields are quoted CSV cells that **contain commas inside them**. Splitting on every comma in the line will shred these into the wrong number of columns.
2. Row 1's `Name` field contains a quoted **embedded newline**, meaning that single logical CSV row spans two physical lines in the file. A parser that reads and splits one `readLine()` at a time will break on this row.

**Requirement:** implement a small RFC 4180–aware CSV parser — read the whole file as one string, then walk it character by character tracking whether you're inside a quoted field (toggle on `"`, and while inside quotes, commas and newlines are literal characters, not delimiters). This is a compact, self-contained piece of logic (no external library needed) and satisfies the File I/O rubric item with real substance rather than a one-line `split()`.

## 6. OOP Class Design (rubric-aligned)

| Rubric item | Where it's satisfied |
|---|---|
| Inheritance (≥1 hierarchy) | `Scholarship` (abstract) → `GeneralScholarship` |
| Polymorphism (overriding, runtime) | `isEligible(StudentProfile)` overridden in `GeneralScholarship`, called polymorphically from the repository |
| Abstraction | `ScholarshipRepository` interface (already exists); `Scholarship` abstract class |
| Encapsulation | All fields private, constructor + getters only, no setters (matches existing `Scholarship` pattern) |
| Collections | `ArrayList<Scholarship>` for loaded data; `HashMap<String, List<String>>` for the field-of-study synonym lookup (§7) |
| File I/O | Custom CSV parser reading `scholarships.csv` |

```java
public abstract class Scholarship {
    private final String id;
    private final String title;
    private final String description; // short card teaser
    private final String overview;    // full text, ScholarshipDetailScreen
    private final String websiteUrl;
    protected final double minGpa;
    protected final int maxAge;
    protected final List<String> majors;

    // constructor + getters only — no setters

    public abstract boolean isEligible(StudentProfile profile);

    // shared ranking logic — not abstract, since scoring doesn't need to vary by subclass
    public double fitScore(StudentProfile profile) {
        return profile.getGpa() - this.minGpa; // higher margin above the cutoff = ranked higher
    }
}

public class GeneralScholarship extends Scholarship {
    @Override
    public boolean isEligible(StudentProfile profile) {
        boolean ageOk = profile.getAge() <= this.maxAge;
        boolean gpaOk = profile.getGpa() >= this.minGpa;
        boolean fieldOk = FieldOfStudyMatcher.matches(profile.getFieldOfStudy(), this.majors);
        return ageOk && gpaOk && fieldOk;
    }
}

public class StudentProfile {
    private final int age;
    private final double gpa;
    private final String nationality;   // collected, never used in matching — see §7
    private final String fieldOfStudy;
    // constructor + getters
}
```

Only one concrete subclass exists for now because the real sheet has no "type" column (merit-based vs. need-based, etc.) to hang a second one off of — inventing an artificial split would just be noise. The rubric only requires "at least one" hierarchy, which this satisfies. If a future column is added (e.g. eligible nationalities, once that data exists), a second subclass like `RegionalScholarship` can extend `Scholarship` with its own `isEligible()` override without touching anything else.

## 7. Matching Algorithm

**Inputs used:** Age, GPA, Field of Study.
**Input collected but not used:** Nationality — confirmed decision: the field stays on `ProfileScreen` for polish, but its value never affects which scholarships are shown. Every nationality produces the same results for a given age/GPA/field-of-study combination.

**Step 1 — Eligibility filter (hard gate, all three must pass):**
- `profile.age <= scholarship.maxAge`
- `profile.gpa >= scholarship.minGpa`
- `profile.fieldOfStudy` maps to at least one entry in `scholarship.majors`

**Step 2 — Field-of-study matching:**
`ProfileScreen`'s Field of Study input is free text (e.g. `"Computer Science & Engineering"`), but the sheet's `Majors` column uses broad fixed categories (`STEM`, `Business & Economics`, `Health & Medicine`, `Arts & Humanities`, `Law`, `Education`, `Other`). A direct string-equality check will almost never match. Use a small synonym map (`HashMap<String, List<String>>`) to normalize common inputs to sheet categories, then do a case-insensitive containment check, e.g.:

```
"computer science", "software", "data science", "engineering" → STEM
"business", "finance", "economics", "accounting"               → Business & Economics
"medicine", "nursing", "health"                                 → Health & Medicine
"law", "legal"                                                  → Law
"education", "teaching"                                         → Education
"art", "history", "literature", "humanities"                    → Arts & Humanities
```
Anything unmatched falls back to a raw substring check against the majors list; if that also fails, treat as `Other`.

**Step 3 — Ranking:**
Among eligible scholarships, sort descending by `fitScore()` — `studentGpa - scholarship.minGpa`. A student who clears the bar comfortably ranks above one who barely qualifies. This produces the "highly compatible" ordering the UI copy implies without needing a more complex weighted model, which would be overkill for a 5–50 row dataset.

*(If field-of-study-as-hard-filter turns out to hide too many scholarships during testing — e.g. the real sheet has narrower `Majors` lists than the sample — the fallback is to downgrade it from a filter to a scoring bonus instead. Flagging this now so it's a one-line change, not a redesign, if needed.)*

## 8. UI Changes Required (beyond the ProfileScreen wiring)

These three changes are explicit decisions for this build and go slightly beyond the original handoff note ("don't touch `*Screen.java` files unless changing profile-data wiring"). Coordinate with the frontend teammate before implementing, since these touch `MatchesScreen.java` and `ScholarshipDetailScreen.java` directly:

- [ ] **Remove tags entirely** from the scholarship cards on `MatchesScreen` and from the tag row on `ScholarshipDetailScreen`. The `Scholarship` model has no `tags` field at all anymore (see class design in §6) — there's nothing to render.
- [ ] **Remove the "Eligibility Criteria" section** from `ScholarshipDetailScreen` completely. Keep only the "Overview" heading and body text, sourced from the `overview` field (derived from the CSV `Description` column per §5).
- [ ] **Make the matches-count subtitle dynamic.** Currently reads a static "we have identified 3 highly compatible opportunities" — change to reflect the real result count (0, 1, or however many pass the filter in §7).

## 9. Functional Requirements per Screen

| Screen | Behavior |
|---|---|
| `LandingScreen` | Unchanged. No backend dependency. |
| `ProfileScreen` | 4 fields unchanged (Age, GPA, Nationality, Field of Study). On "Match Me" click: parse into a `StudentProfile` (age → int, gpa → double, nationality → raw String, stored but unused, fieldOfStudy → raw String) and pass into `MatchesScreen`'s constructor, which passes it into `MatchingScholarshipRepository`. Resolves the existing `// TODO(backend)` marker. |
| `MatchesScreen` | Calls `repo.getAllScholarships()` as before. Card shows title, `description` teaser, "View Details" button — no tags (§8). Subtitle count is dynamic (§8). Empty state if zero matches (§10). |
| `ScholarshipDetailScreen` | Shows title, "Visit Scholarship Website" button (`websiteUrl`), and a single "Overview" section (`overview`) — no tags, no "Eligibility Criteria" (§8). Prev/Next in the header continues to cycle through the matched list returned by the repository, not the full dataset. |

## 10. Non-Functional Requirements & Edge Cases

- Java 17+, Maven, run via `mvn clean javafx:run` — unchanged.
- Dataset is small (5–50 rows expected) — no performance concerns; no need for indexing or caching beyond a simple `ArrayList`.
- Do not touch `app.css` or `NavBar.java`.
- **Malformed rows:** if `Minimum GPA` or `Maximum Age` fails to parse as a number, skip that row and log a warning — don't crash the app.
- **Zero matches:** `MatchesScreen` must show a friendly empty state (e.g. "No matching scholarships found yet.") rather than a blank list — this is a real possibility with a strict 3-filter match against a small dataset.
- **Unreadable file:** if the CSV resource is missing or unreadable, fail gracefully with a logged error and an empty result list, not an uncaught exception that kills the app on launch.

## 11. Milestones

- [ ] **M1 — Parser:** Custom CSV parser correctly handles quoted commas and the embedded newline in row 1; all 5 sample rows load without exceptions; malformed rows are skipped with a warning, not a crash.
- [ ] **M2 — Class hierarchy:** `Scholarship` (abstract), `GeneralScholarship`, `StudentProfile` compile; `isEligible()` and `fitScore()` manually tested against a handful of sample profiles with known expected outcomes.
- [ ] **M3 — Repository:** `MatchingScholarshipRepository` returns correctly filtered and ranked results; `StaticScholarshipRepository` deleted.
- [ ] **M4 — Wiring:** `ProfileScreen`'s "Match Me" button builds a real `StudentProfile` and flows it through to `MatchesScreen` → repository end-to-end.
- [ ] **M5 — UI simplification:** Tags removed from both screens; "Eligibility Criteria" section removed; match-count subtitle is dynamic; empty state implemented.
- [ ] **M6 — Stretch:** Swap the bundled CSV resource for a fetch against a live published Google Sheet CSV URL (`java.net.http.HttpClient`) — isolated entirely inside `MatchingScholarshipRepository`, no other class changes.

## 12. Appendix

**Screen flow (unchanged):**
```
LandingScreen -> ProfileScreen -> MatchesScreen -> ScholarshipDetailScreen
   (hero/CTA)    (profile form)   (list of cards)   (1 scholarship, Prev/Next in header)
```

**Existing interface (unchanged signature):**
```java
public interface ScholarshipRepository {
    List<Scholarship> getAllScholarships();
}
```

**CSV header + one full sample row, verbatim structure:**
```
id,Name,Minimum GPA,Maximum Age,link,Majors,Description
1,"Türkiye Scholarships
",2.8,21,https://www.turkiyeburslari.gov.tr/,"Business & Economics, Health & Medicine, STEM, Arts & Humanities, Law, Education","overview: Türkiye Scholarships is a highly competitive, government-funded program that provides international students with the opportunity to pursue undergraduate, master's, and PhD degrees at prestigious Turkish universities..."
```
(Note the literal embedded newline inside the quoted `Name` field on this row — this is the exact case the parser in §5 must handle.)
