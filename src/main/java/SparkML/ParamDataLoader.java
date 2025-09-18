package SparkML;

import Connection.Ticket;
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
                data.setStudentId(rs.getString("student_id"));
                data.setGpa(rs.getFloat("gpa"));
                data.setFailedSubjects(rs.getInt("failed_subjects"));
                data.setAcademicStatus(rs.getString("academic_status"));
                data.setCurricularUnitsPassed(rs.getInt("curricular_units_passed"));
                data.setCurricularUnitsFailed(rs.getInt("curricular_units_failed"));
                data.setLateCounts(rs.getInt("late_counts"));
                data.setAbsentCounts(rs.getInt("absent_counts"));
                data.setLatenessReason(rs.getString("lateness_reason"));
                data.setAbsenceReason(rs.getString("absence_reason"));
                data.setFamilyIncome(rs.getFloat("family_income"));
                data.setParentEducation(rs.getString("parent_education"));
                data.setTransportMode(rs.getString("transport_mode"));
                data.setScholarshipStatus(rs.getString("scholarship_status"));
                data.setDegreeProgram(rs.getString("degree_program"));
                data.setYearLevel(rs.getInt("year_level"));
                data.setAdmissionType(rs.getString("admission_type"));
                data.setDropoutStatus(rs.getString("dropout_status"));

                param.add(data);

            }
        } catch (SQLException ex) {
            Logger.getLogger(ParamDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return param;
    }

    public static Dataset<Row> convertHistoryParams(SparkSession spark, List<ParamDatasets> records) {
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
                data.setStudentId(studentId);
                data.setGpa(paramRs.getFloat("gpa"));
                data.setFailedSubjects(paramRs.getInt("failed_subjects"));
                data.setAcademicStatus(paramRs.getString("academic_status"));
                data.setCurricularUnitsPassed(paramRs.getInt("curricular_unit_passed"));
                data.setCurricularUnitsFailed(paramRs.getInt("curricular_unit_failed"));
                data.setFamilyIncome(paramRs.getFloat("family_income"));
                data.setParentEducation(paramRs.getString("parent_education"));
                data.setTransportMode(paramRs.getString("transport_mode"));
                data.setScholarshipStatus(paramRs.getString("scholarship_status"));
                data.setAdmissionType(paramRs.getString("admission_type"));
                data.setLateCounts(att[0]);
                data.setAbsentCounts(att[1]);

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

}
