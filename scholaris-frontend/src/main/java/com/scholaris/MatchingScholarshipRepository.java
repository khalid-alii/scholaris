package com.scholaris;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * The real backend implementation of ScholarshipRepository.
 *
 * Replaces StaticScholarshipRepository.  Loads scholarship data from
 * scholarships.csv (bundled as a classpath resource), applies the three-gate
 * eligibility filter (age, GPA, field of study), ranks survivors by fitScore(),
 * and returns the resulting list.
 *
 * Satisfies rubric:
 *   - Abstraction: implements ScholarshipRepository interface
 *   - Collections: uses ArrayList<Scholarship> for loaded data
 *   - File I/O: reads scholarships.csv via CsvParser (RFC 4180-aware)
 *   - Polymorphism: calls scholarship.isEligible(profile) polymorphically
 */
public class MatchingScholarshipRepository implements ScholarshipRepository {

    private static final String CSV_PATH = "/scholarships.csv";

    // Expected column indices (0-based) in the CSV
    private static final int COL_ID          = 0;
    private static final int COL_NAME        = 1;
    private static final int COL_MIN_GPA     = 2;
    private static final int COL_MAX_AGE     = 3;
    private static final int COL_LINK        = 4;
    private static final int COL_MAJORS      = 5;
    private static final int COL_DESCRIPTION = 6;

    private final StudentProfile profile;

    public MatchingScholarshipRepository(StudentProfile profile) {
        this.profile = profile;
    }

    @Override
    public List<Scholarship> getAllScholarships() {
        List<Scholarship> all = loadFromCsv();

        // Filter: every gate must pass
        List<Scholarship> matched = new ArrayList<>();
        for (Scholarship s : all) {
            if (s.isEligible(profile)) {   // ← polymorphic call
                matched.add(s);
            }
        }

        // Rank: highest fitScore first (most GPA headroom above the cutoff)
        matched.sort(Comparator.comparingDouble(
                (Scholarship s) -> s.fitScore(profile)).reversed());

        return matched;
    }

    // ── CSV loading ───────────────────────────────────────────────────────────

    private List<Scholarship> loadFromCsv() {
        List<Scholarship> result = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(CSV_PATH)) {
            if (is == null) {
                System.err.println("[Scholaris] WARNING: " + CSV_PATH + " not found on classpath. " +
                                   "Returning empty list.");
                return result;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            List<List<String>> rows = CsvParser.parse(content);

            // Skip header row (index 0)
            for (int i = 1; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                try {
                    Scholarship s = parseRow(row);
                    if (s != null) {
                        result.add(s);
                    }
                } catch (Exception e) {
                    System.err.println("[Scholaris] WARNING: skipping malformed row " + i +
                                       ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("[Scholaris] ERROR: could not read " + CSV_PATH +
                               ": " + e.getMessage());
        }

        return result;
    }

    /**
     * Maps one CSV row (list of field strings) to a GeneralScholarship.
     * Returns null and logs a warning if mandatory numeric fields cannot be parsed.
     */
    private Scholarship parseRow(List<String> fields) {
        if (fields.size() <= COL_DESCRIPTION) {
            System.err.println("[Scholaris] WARNING: row has too few columns (" +
                               fields.size() + "), skipping.");
            return null;
        }

        String id   = fields.get(COL_ID).trim();
        String name = fields.get(COL_NAME).trim();  // .trim() handles embedded newlines
        String link = fields.get(COL_LINK).trim();

        double minGpa;
        try {
            minGpa = Double.parseDouble(fields.get(COL_MIN_GPA).trim());
        } catch (NumberFormatException e) {
            System.err.println("[Scholaris] WARNING: bad Minimum GPA for \"" + name +
                               "\" — skipping row.");
            return null;
        }

        int maxAge;
        try {
            maxAge = Integer.parseInt(fields.get(COL_MAX_AGE).trim());
        } catch (NumberFormatException e) {
            System.err.println("[Scholaris] WARNING: bad Maximum Age for \"" + name +
                               "\" — skipping row.");
            return null;
        }

        // Majors: comma-separated inside a single quoted cell
        List<String> majors = parseMajors(fields.get(COL_MAJORS));

        // Description: strip "overview: " prefix, derive overview + short teaser
        String rawDescription = fields.get(COL_DESCRIPTION).trim();
        String overview  = deriveOverview(rawDescription);
        String shortDesc = deriveShortDescription(rawDescription);

        return new GeneralScholarship(id, name, shortDesc, overview, link, minGpa, maxAge, majors);
    }

    /**
     * Splits the Majors cell value by comma and trims each entry.
     */
    private List<String> parseMajors(String raw) {
        String[] parts = raw.split(",");
        List<String> majors = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                majors.add(trimmed);
            }
        }
        return majors;
    }

    /**
     * Strips the leading "overview: " prefix (case-insensitive) and trims.
     */
    private String deriveOverview(String rawDescription) {
        String lower = rawDescription.toLowerCase();
        if (lower.startsWith("overview:")) {
            return rawDescription.substring("overview:".length()).trim();
        }
        return rawDescription.trim();
    }

    /**
     * Derives a short card teaser from the raw description:
     *   - Strip the "overview: " prefix first
     *   - Truncate to the first sentence (up to and including the first ".")
     *   - If that's still > 140 chars, truncate at the nearest word boundary
     *     before 140 chars and append "…"
     */
    private String deriveShortDescription(String rawDescription) {
        String text = deriveOverview(rawDescription);

        // First sentence
        int dotIndex = text.indexOf('.');
        if (dotIndex != -1) {
            String sentence = text.substring(0, dotIndex + 1).trim();
            if (sentence.length() <= 140) {
                return sentence;
            }
        }

        // Fallback: truncate at word boundary near 140 chars
        if (text.length() <= 140) {
            return text;
        }
        int cutoff = text.lastIndexOf(' ', 140);
        if (cutoff == -1) cutoff = 140;
        return text.substring(0, cutoff).trim() + "…";
    }
}
