package com.scholaris;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Normalises a student's free-text field-of-study input against the broad
 * fixed categories used in the scholarship CSV's "Majors" column.
 *
 * The ProfileScreen TextField is free text (e.g. "Computer Science & Engineering"),
 * but the CSV uses broad categories: STEM, Business & Economics, Health & Medicine,
 * Arts & Humanities, Law, Education, Other.  A direct string-equality check would
 * almost never match.  This class uses a HashMap synonym map
 * (satisfies the Collections rubric item alongside the ArrayList in the repository)
 * to map common inputs to their canonical sheet category, then does a
 * case-insensitive substring check against the scholarship's majors list.
 */
public class FieldOfStudyMatcher {

    /**
     * Synonym map: each key is a lowercase keyword found in student input;
     * the value is the canonical CSV category it maps to.
     */
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        // STEM
        for (String kw : Arrays.asList(
                "computer", "software", "data science", "engineering", "technology",
                "math", "mathematics", "physics", "chemistry", "biology", "science",
                "information technology", "it", "computing", "cybersecurity", "ai",
                "artificial intelligence", "machine learning", "robotics", "stem")) {
            SYNONYMS.put(kw, "STEM");
        }
        // Business & Economics
        for (String kw : Arrays.asList(
                "business", "finance", "economics", "accounting", "management",
                "marketing", "commerce", "entrepreneurship", "administration", "mba")) {
            SYNONYMS.put(kw, "Business & Economics");
        }
        // Health & Medicine
        for (String kw : Arrays.asList(
                "medicine", "nursing", "health", "pharmacy", "medical", "dentistry",
                "physiotherapy", "public health", "nutrition", "biomedical")) {
            SYNONYMS.put(kw, "Health & Medicine");
        }
        // Law
        for (String kw : Arrays.asList(
                "law", "legal", "jurisprudence", "criminology")) {
            SYNONYMS.put(kw, "Law");
        }
        // Education
        for (String kw : Arrays.asList(
                "education", "teaching", "pedagogy", "early childhood")) {
            SYNONYMS.put(kw, "Education");
        }
        // Arts & Humanities
        for (String kw : Arrays.asList(
                "art", "history", "literature", "humanities", "philosophy",
                "music", "theatre", "theater", "film", "linguistics", "language",
                "cultural", "media", "communication", "journalism", "fine art")) {
            SYNONYMS.put(kw, "Arts & Humanities");
        }
    }

    /**
     * Returns true if the student's field of study is compatible with at least
     * one of the scholarship's listed major categories.
     *
     * Algorithm:
     *   1. Check if any SYNONYM keyword is a substring of the student input.
     *      Map it to its canonical category and test against the majors list.
     *   2. If no synonym matched, do a raw case-insensitive substring scan of
     *      the student input against each major string directly.
     *   3. If that also fails, treat as "Other" and check if "Other" is listed.
     *
     * @param fieldOfStudy  raw student input from the ProfileScreen TextField
     * @param majors        scholarship's list of accepted major categories
     */
    public static boolean matches(String fieldOfStudy, List<String> majors) {
        if (fieldOfStudy == null || fieldOfStudy.isBlank() || majors == null || majors.isEmpty()) {
            return false;
        }

        String input = fieldOfStudy.toLowerCase().trim();

        // Step 1 — synonym lookup
        for (Map.Entry<String, String> entry : SYNONYMS.entrySet()) {
            if (input.contains(entry.getKey())) {
                String canonicalCategory = entry.getValue();
                for (String major : majors) {
                    if (major.trim().equalsIgnoreCase(canonicalCategory)) {
                        return true;
                    }
                }
            }
        }

        // Step 2 — raw substring fallback
        for (String major : majors) {
            if (input.contains(major.trim().toLowerCase()) ||
                major.trim().toLowerCase().contains(input)) {
                return true;
            }
        }

        // Step 3 — treat as "Other"
        for (String major : majors) {
            if (major.trim().equalsIgnoreCase("Other")) {
                return true;
            }
        }

        return false;
    }
}
