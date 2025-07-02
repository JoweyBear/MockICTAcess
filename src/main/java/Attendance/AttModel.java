package Attendance;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttModel {

    private int rm_id;
    private String name;
    private String college;
    private String building;
    private String flr_lvl;
    private String type;
    private String desc;

    private int cs_id;
    private String class_type;
    private String day;
    private LocalTime time_strt;
    private LocalTime time_end;
    private String subject;
    private String faculty_id;
//    private int room_id;

    private String stud_id;
    private String year;
    private String section;

    private int att_id;
    private String status;
    private LocalDateTime attDateTime;
    private String att_method;
    private String user_id;
    private LocalDateTime created_at;
    
    
    
    public int getRm_id() {
        return rm_id;
    }

    public void setRm_id(int rm_id) {
        this.rm_id = rm_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFlr_lvl() {
        return flr_lvl;
    }

    public void setFlr_lvl(String flr_lvl) {
        this.flr_lvl = flr_lvl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCs_id() {
        return cs_id;
    }

    public void setCs_id(int cs_id) {
        this.cs_id = cs_id;
    }

    public String getClass_type() {
        return class_type;
    }

    public void setClass_type(String class_type) {
        this.class_type = class_type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public LocalTime getTime_strt() {
        return time_strt;
    }

    public void setTime_strt(LocalTime time_strt) {
        this.time_strt = time_strt;
    }

    public LocalTime getTime_end() {
        return time_end;
    }

    public void setTime_end(LocalTime time_end) {
        this.time_end = time_end;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFaculty_id() {
        return faculty_id;
    }

    public void setFaculty_id(String faculty_id) {
        this.faculty_id = faculty_id;
    }

    public String getStud_id() {
        return stud_id;
    }

    public void setStud_id(String stud_id) {
        this.stud_id = stud_id;
    }

    public int getAtt_id() {
        return att_id;
    }

    public void setAtt_id(int att_id) {
        this.att_id = att_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAttDateTime() {
        return attDateTime;
    }

    public void setAttDateTime(LocalDateTime attDateTime) {
        this.attDateTime = attDateTime;
    }

    public String getAtt_method() {
        return att_method;
    }

    public void setAtt_method(String att_method) {
        this.att_method = att_method;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

}
