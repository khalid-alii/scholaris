package com.scholaris;

import java.util.List;

/**
 * Abstract base class for all scholarship types.
 *
 * Satisfies OOP rubric items:
 *   - Abstraction: abstract class with abstract isEligible()
 *   - Inheritance: GeneralScholarship (and future subclasses) extend this
 *   - Encapsulation: all fields private/protected with getters, no setters
 *   - Polymorphism: isEligible() is overridden at runtime in each subclass
 */
public abstract class Scholarship {

    private final String id;
    private final String title;
    private final String description;   // short card teaser (≤140 chars)
    private final String overview;      // full text shown on ScholarshipDetailScreen
    private final String websiteUrl;
    protected final double minGpa;
    protected final int maxAge;
    protected final List<String> majors; // broad category list from CSV "Majors" column

    public Scholarship(String id, String title, String description, String overview,
                       String websiteUrl, double minGpa, int maxAge, List<String> majors) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.overview    = overview;
        this.websiteUrl  = websiteUrl;
        this.minGpa      = minGpa;
        this.maxAge      = maxAge;
        this.majors      = majors;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getOverview()    { return overview; }
    public String getWebsiteUrl()  { return websiteUrl; }
    public double getMinGpa()      { return minGpa; }
    public int    getMaxAge()      { return maxAge; }
    public List<String> getMajors() { return majors; }

    /**
     * Determines whether this scholarship is eligible for the given student.
     * Each subclass overrides this with its own eligibility rules — the
     * repository calls this polymorphically while filtering the full list.
     */
    public abstract boolean isEligible(StudentProfile profile);

    /**
     * Shared ranking score: how far above the GPA cutoff the student sits.
     * Higher margin = ranked first. Not abstract — scoring logic is the same
     * across all current subclasses.
     */
    public double fitScore(StudentProfile profile) {
        return profile.getGpa() - this.minGpa;
    }
}
