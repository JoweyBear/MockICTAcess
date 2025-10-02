package SparkML;

import Connection.Ticket;
import Student.StudentModel;
import Utilities.Encryption;
import Utilities.GlobalVar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ParamDataLoader {

    static String college = GlobalVar.loggedInAdmin.getCollge();

    public static List<ParamDatasets> fetchParams() {
        List<ParamDatasets> param = new ArrayList<>();
        String sql = "SELECT * FROM params";

        try {
            Connection conn = Ticket.getConn();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ParamDatasets data = new ParamDatasets();
                data.setStudent_id(rs.getString("student_id"));
                data.setAddress_school_km(rs.getInt("address_school_km"));
                data.setFather_employed(rs.getString("father_employed"));
                data.setMother_employed(rs.getString(rs.getString("mother_employed")));
                data.setDegree_program(rs.getString("degree_program"));
                data.setYear(rs.getInt("year"));
                data.setBirth_order(rs.getInt("birth_order"));
                data.setDegree_holders_count(rs.getInt("degree_holder"));
                data.setAnnual_family_income(rs.getString("annual_family_income"));
                data.setSource_of_income(rs.getString("source_of_income"));
                data.setFourps_beneficiary(rs.getString("4ps_beneficiary"));
                data.setVulnerable_group_flag(rs.getString("vulnerable_group_flag"));

                param.add(data);

            }
        } catch (SQLException ex) {
            Logger.getLogger(ParamDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return param;
    }

    public static Dataset<Row> convertParams(SparkSession spark, List<ParamDatasets> records) {
        return spark.createDataFrame(records, ParamDatasets.class);
    }

    public static List<ParamDatasets> fetchCurrentParams() {
        List<ParamDatasets> paramList = new ArrayList<>();

        String attSql = "SELECT a.user_id, "
                + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS lateCount, "
                + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absentCount "
                + "FROM attendance a "
                + "JOIN user u ON a.user_id = u.user_id "
                + "WHERE u.role = 'student' AND u.college = ? "
                + "GROUP BY a.user_id";

        String paramSql = "SELECT * FROM params WHERE college = ?";
        Connection conn = Ticket.getConn();
        try (PreparedStatement attStmt = conn.prepareStatement(attSql); PreparedStatement paramStmt = conn.prepareStatement(paramSql)) {
            attStmt.setString(1, college);
            paramStmt.setString(1, college);

            ResultSet attRs = attStmt.executeQuery();
            Map<String, Integer[]> attendanceMap = new HashMap<>();

            while (attRs.next()) {
                String userId = attRs.getString("user_id");
                int late = attRs.getInt("lateCount");
                int absent = attRs.getInt("absentCount");
                attendanceMap.put(userId, new Integer[]{late, absent});
            }

            ResultSet paramRs = paramStmt.executeQuery();
            while (paramRs.next()) {
                String studentId = paramRs.getString("student_id");
                Integer[] att = attendanceMap.getOrDefault(studentId, new Integer[]{0, 0});

                ParamDatasets data = new ParamDatasets();
//                data.setStudentId(studentId);
//                data.setGpa(paramRs.getFloat("gpa"));
//                data.setFailedSubjects(paramRs.getInt("failed_subjects"));
//                data.setAcademicStatus(paramRs.getString("academic_status"));
//                data.setCurricularUnitsPassed(paramRs.getInt("curricular_unit_passed"));
//                data.setCurricularUnitsFailed(paramRs.getInt("curricular_unit_failed"));
//                data.setFamilyIncome(paramRs.getFloat("family_income"));
//                data.setParentEducation(paramRs.getString("parent_education"));
//                data.setTransportMode(paramRs.getString("transport_mode"));
//                data.setScholarshipStatus(paramRs.getString("scholarship_status"));
//                data.setAdmissionType(paramRs.getString("admission_type"));
//                data.setLateCounts(att[0]);
//                data.setAbsentCounts(att[1]);

                paramList.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch current parameters: " + e.getMessage());
        }
        return paramList;
    }

    public static Dataset<Row> convertCurrentParams(SparkSession spark, List<ParamDatasets> records) {
        return spark.createDataFrame(records, ParamDatasets.class);
    }

    public static StudentModel getStudentInfo(String student_id) {
        StudentModel student = new StudentModel();
        Encryption de = new Encryption();

        String sql = "SELECT s.user_id, s.fname, s.mname, s.lname, si.section, si.year, si.track "
                + "FROM user s "
                + "JOIN student_info si ON s.user_id = si.user_id "
                + "WHERE s.user_id = ? AND s.role = 'student' AND s.college = ?";

        try {
            Connection conn = Ticket.getConn();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, student_id);
            ps.setString(2, college);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                student.setStud_id(student_id);
                student.setFname(de.decrypt(rs.getString("fname")));
                student.setMname(de.decrypt(rs.getString("mname")));
                student.setLname(de.decrypt(rs.getString("lname")));
                student.setSection(rs.getString("section"));
                student.setYear(rs.getString("year"));
                student.setTrack(rs.getString("track"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch student info: " + e.getMessage());
        }

        return student;
    }
    
//    public static ParamDatasets fetchDatasets(){
//        
//    }

}
