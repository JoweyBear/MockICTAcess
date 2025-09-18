
package SparkML;

public class ParamDatasets {

    // 🎓 Academic Performance
    private String studentId;
    private float gpa;
    private int failedSubjects;
    private String academicStatus;
    private int curricularUnitsPassed;
    private int curricularUnitsFailed;

    // 🕒 Attendance Behavior
    private int lateCounts;
    private int absentCounts;
    private String latenessReason;
    private String absenceReason;

    // 🧠 Psychosocial & Behavioral
//    private int disciplinaryActions;
//    private int counselingVisits;
//    private String stressLevel;
//    private String peerRelationships;

    // 🏠 Socioeconomic Background
    private float familyIncome;
    private String parentEducation;
    private String transportMode;
    private String scholarshipStatus;

    // 🧾 Enrollment Info
    private String degreeProgram;
    private int yearLevel;
    private String admissionType;

    // 🎯 Target Label
    private String dropoutStatus;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    public int getFailedSubjects() {
        return failedSubjects;
    }

    public void setFailedSubjects(int failedSubjects) {
        this.failedSubjects = failedSubjects;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public int getCurricularUnitsPassed() {
        return curricularUnitsPassed;
    }

    public void setCurricularUnitsPassed(int curricularUnitsPassed) {
        this.curricularUnitsPassed = curricularUnitsPassed;
    }

    public int getCurricularUnitsFailed() {
        return curricularUnitsFailed;
    }

    public void setCurricularUnitsFailed(int curricularUnitsFailed) {
        this.curricularUnitsFailed = curricularUnitsFailed;
    }

    public int getLateCounts() {
        return lateCounts;
    }

    public void setLateCounts(int lateCounts) {
        this.lateCounts = lateCounts;
    }

    public int getAbsentCounts() {
        return absentCounts;
    }

    public void setAbsentCounts(int absentCounts) {
        this.absentCounts = absentCounts;
    }

    public String getLatenessReason() {
        return latenessReason;
    }

    public void setLatenessReason(String latenessReason) {
        this.latenessReason = latenessReason;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }

//    public int getDisciplinaryActions() {
//        return disciplinaryActions;
//    }
//
//    public void setDisciplinaryActions(int disciplinaryActions) {
//        this.disciplinaryActions = disciplinaryActions;
//    }
//
//    public int getCounselingVisits() {
//        return counselingVisits;
//    }
//
//    public void setCounselingVisits(int counselingVisits) {
//        this.counselingVisits = counselingVisits;
//    }
//
//    public String getStressLevel() {
//        return stressLevel;
//    }
//
//    public void setStressLevel(String stressLevel) {
//        this.stressLevel = stressLevel;
//    }
//
//    public String getPeerRelationships() {
//        return peerRelationships;
//    }
//
//    public void setPeerRelationships(String peerRelationships) {
//        this.peerRelationships = peerRelationships;
//    }

    public float getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(float familyIncome) {
        this.familyIncome = familyIncome;
    }

    public String getParentEducation() {
        return parentEducation;
    }

    public void setParentEducation(String parentEducation) {
        this.parentEducation = parentEducation;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getScholarshipStatus() {
        return scholarshipStatus;
    }

    public void setScholarshipStatus(String scholarshipStatus) {
        this.scholarshipStatus = scholarshipStatus;
    }

    public String getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(String degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public String getDropoutStatus() {
        return dropoutStatus;
    }

    public void setDropoutStatus(String dropoutStatus) {
        this.dropoutStatus = dropoutStatus;
    }
    
    
}
