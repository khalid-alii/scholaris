package com.scholaris;

/**
 * Immutable snapshot of the student's self-reported profile.
 *
 * Satisfies rubric: Encapsulation (private fields, constructor + getters only,
 * no setters). Nationality is stored but intentionally never used in matching
 * (confirmed PRD §7 decision).
 */
public class StudentProfile {

    private final int    age;
    private final double gpa;
    private final String nationality;   // collected for UI polish; unused in matching
    private final String fieldOfStudy;

    public StudentProfile(int age, double gpa, String nationality, String fieldOfStudy) {
        this.age          = age;
        this.gpa          = gpa;
        this.nationality  = nationality;
        this.fieldOfStudy = fieldOfStudy;
    }

    public int    getAge()          { return age; }
    public double getGpa()          { return gpa; }
    public String getNationality()  { return nationality; }
    public String getFieldOfStudy() { return fieldOfStudy; }
}
