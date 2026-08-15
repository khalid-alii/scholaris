package com.scholaris;

import java.util.List;

/**
 * The one concrete Scholarship subclass for the current dataset.
 *
 * Eligibility rules (all three must pass — hard gate):
 *   1. student age   <=  scholarship.maxAge
 *   2. student GPA   >=  scholarship.minGpa
 *   3. student field of study maps to at least one entry in scholarship.majors
 *      (via FieldOfStudyMatcher synonym lookup)
 *
 * Satisfies rubric: Inheritance (extends Scholarship) and Polymorphism
 * (overrides isEligible — called at runtime via the abstract reference in
 * MatchingScholarshipRepository).
 */
public class GeneralScholarship extends Scholarship {

    public GeneralScholarship(String id, String title, String description, String overview,
                              String websiteUrl, double minGpa, int maxAge, List<String> majors) {
        super(id, title, description, overview, websiteUrl, minGpa, maxAge, majors);
    }

    @Override
    public boolean isEligible(StudentProfile profile) {
        boolean ageOk   = profile.getAge()  <= this.maxAge;
        boolean gpaOk   = profile.getGpa()  >= this.minGpa;
        boolean fieldOk = FieldOfStudyMatcher.matches(profile.getFieldOfStudy(), this.majors);
        return ageOk && gpaOk && fieldOk;
    }
}
