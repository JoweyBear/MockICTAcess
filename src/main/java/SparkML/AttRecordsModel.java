package SparkML;

public class AttRecordsModel {
    private String student_id;
    private int absent_count;
    private int late_count;
    private String status;

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public int getAbsent_count() {
        return absent_count;
    }

    public void setAbsent_count(int absent_count) {
        this.absent_count = absent_count;
    }

    public int getLate_count() {
        return late_count;
    }

    public void setLate_count(int late_count) {
        this.late_count = late_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
