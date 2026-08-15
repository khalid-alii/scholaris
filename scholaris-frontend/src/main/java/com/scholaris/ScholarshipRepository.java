package com.scholaris;

import java.util.List;

/**
 * Abstraction between the UI and wherever scholarship data actually comes
 * from (file, database, matching algorithm, etc).
 *
 * The frontend only ever talks to this interface. Your backend teammate
 * can implement it with the real matching logic (reading from a file,
 * applying the GPA/nationality/field-of-study rules, etc.) and the UI
 * will not need to change - just swap StaticScholarshipRepository for
 * the real implementation in MatchesScreen.
 */
public interface ScholarshipRepository {
    List<Scholarship> getAllScholarships();
}
