///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package AdminDashboard;
//
///**
// *
// * @author Administrator
// */
//public class NewClass {
//        @Override
//    public DefaultTableModel getAllAttendaceRecordsBetween(Date date1, Date date2) {
//
//        String sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
//                + "u.fname AS 'FirstName', u.mname AS 'MiddleName', u.lname AS 'LastName', "
//                + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
//                + "FROM attendance a "
//                + "JOIN user u ON a.user_id = u.user_id "
//                + "JOIN student_info si ON a.user_id = si.user_id "
//                + "WHERE u.college = ? "
//                + "AND DATE(a.att_date_time) BETWEEN ? AND ?";
//
//        String sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
//                + "FROM attendance a "
//                + "JOIN user s ON s.user_id = a.user_id "
//                + "WHERE s.college = ? "
//                + "AND DATE(att_date_time) BETWEEN ? AND ? "
//                + "GROUP BY a.user_id, a.status";
//
//        Map<String, Map<String, Integer>> statusMap = new HashMap<>();
//
//        try (PreparedStatement ps = conn.prepareStatement(sqlStatusCounts)) {
//            ps.setString(1, college);
//
//            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
//            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
//            ps.setDate(2, sqlDate1);
//            ps.setDate(3, sqlDate2);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String userId = rs.getString("user_id");
//                String status = rs.getString("status");
//                int count = rs.getInt("count");
//
//                statusMap.computeIfAbsent(userId, k -> new HashMap<>()).merge(status, count, Integer::sum);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
//            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
//            ps.setDate(2, sqlDate1);
//            ps.setDate(3, sqlDate2);
//            rs = ps.executeQuery();
//
//            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));
//
//            Vector<Vector<Object>> data = new Vector<>();
//            while (rs.next()) {
//                Vector<Object> row = new Vector<>();
//
//                String studentId = rs.getString("Student ID");
//                String fname = de.decrypt(rs.getString("FirstName"));
//                String mname = de.decrypt(rs.getString("MiddleName")).substring(0, 1);
//                String lname = de.decrypt(rs.getString("LastName"));
//
//                String name = fname + " " + mname + ". " + lname;
//
//                row.add(studentId);
//                row.add(name);
//                row.add(rs.getString("Section"));
//                row.add(rs.getString("Year"));
//                row.add(rs.getString("Track"));
//
//                Map<String, Integer> counts = statusMap.getOrDefault(studentId, new HashMap<>());
//                int present = counts.getOrDefault("Complete", 0);
//                int absent = counts.getOrDefault("Absent", 0);
//                int late = counts.getOrDefault("Late", 0);
//                int incomplete = counts.getOrDefault("Incomplete", 0);
//                int leftEarly = counts.getOrDefault("Early Time Out", 0);
//                int total = present + absent + late + incomplete + leftEarly;
//
//                String remarks;
//                if (total == 0) {
//                    remarks = "No Records";
//                } else {
//                    double presentRate = (double) present / total;
//                    double absentRate = (double) absent / total;
//                    double lateRate = (double) late / total;
//                    double incompleteRate = (double) incomplete / total;
//                    double earlyRate = (double) leftEarly / total;
//
//                    if (lateRate >= 0.3) {
//                        remarks = "Tardy (" + String.format("%.0f%%", lateRate * 100) + ")";
//                    } else if (presentRate >= 0.5) {
//                        remarks = "Mostly Present (" + String.format("%.0f%%", presentRate * 100) + ")";
//                    } else if (absentRate >= 0.5) {
//                        remarks = "Mostly Absent (" + String.format("%.0f%%", absentRate * 100) + ")";
//                    } else if (incompleteRate >= 0.5) {
//                        remarks = "Mostly Incomplete (" + String.format("%.0f%%", incompleteRate * 100) + ")";
//                    } else if (earlyRate >= 0.5) {
//                        remarks = "Mostly Left Early (" + String.format("%.0f%%", earlyRate * 100) + ")";
//                    } else {
//                        remarks = "Unclassified";
//                    }
//                }
//
//                row.add(remarks);
//
//                data.add(row);
//            }
//            return new DefaultTableModel(data, columnNames);
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));
//        return new DefaultTableModel(new Vector<>(), columns);
//    }
//
//    @Override
//    public Map<String, Integer> getAttendanceCountsBetween(Date date1, Date date2) {
//        Map<String, Integer> statusCount = new HashMap<>();
//
//        String sql = "SELECT a.status, COUNT(*) AS count "
//                + "FROM attendance a "
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "WHERE u.college = ? "
//                + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
//                + "GROUP BY a.status";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
//            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
//            ps.setDate(2, sqlDate1);
//            ps.setDate(3, sqlDate2);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                statusCount.put(rs.getString("status"), rs.getInt("count"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return statusCount;
//    }
//
//    @Override
//    public Map<String, Map<String, Integer>> getAttendanceByGenderBetween(Date date1, Date date2) {
//        Map<String, Map<String, Integer>> result = new HashMap<>();
//        String sql = "SELECT u.sex, a.status, COUNT(*) AS count "
//                + "FROM attendance a "
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "WHERE u.college = ? "
//                + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
//                + "GROUP BY u.gender, a.status";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, college);
//            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
//            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
//            ps.setDate(2, sqlDate1);
//            ps.setDate(3, sqlDate2);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String gender = rs.getString("sex");
//                String status = rs.getString("status");
//                int count = rs.getInt("count");
//
//                result.computeIfAbsent(gender, k -> new HashMap<>()).merge(status, count, Integer::sum);
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @Override
//    public AttModel getAttendanceStatusBetween(Date date1, Date date2) {
//        AttModel summary = new AttModel();
//        String sql = "SELECT "
//                + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
//                + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
//                + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
//                + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
//                + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
//                + "COUNT (CASE WHEN a.status = 'Early Time Out' THEN 1 END AS left_early) "
//                + "FROM attendance a "
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "WHERE u.college = ? "
//                + "AND DATE(att_date_time) BETWEEN ? AND ?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
//            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
//            ps.setDate(2, sqlDate1);
//            ps.setDate(3, sqlDate2);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                summary.setTimeInCount(rs.getInt("time_in_count"));
//                summary.setTimeOutCount(rs.getInt("time_out_count"));
//                summary.setLateCount(rs.getInt("late_count"));
//                summary.setAbsentCount(rs.getInt("absent_count"));
//                summary.setIncompleteCount(rs.getInt("incomplete_count"));
//                summary.setLeftEarly(rs.getInt("left_early"));
//
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return summary;
//    }
//
//    @Override
//    public DefaultTableModel getAllAttendanceCS(String cs_id) {
//        String sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
//                + "s.fname AS 'FirstName', s.mname AS 'MiddleName', s.lname AS 'LastName', "
//                + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
//                + "FROM attendance a "
//                + "JOIN user u ON a.user_id = u.user_id "
//                + "WHERE u.college = ? AND a.class_schedule_id = ?";
//
//        String sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
//                + "FROM attendance a "
//                + "JOIN user s ON s.user_id = a.user_id "
//                + "WHERE s.college = ? a.class_schedule_id = ? "
//                + "GROUP BY a.user_id, a.status";
//
//        Map<String, Map<String, Integer>> statusMap = new HashMap<>();
//
//        try (PreparedStatement ps = conn.prepareStatement(sqlStatusCounts)) {
//            ps.setString(1, college);
//            ps.setString(2, cs_id);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String userId = rs.getString("user_id");
//                String status = rs.getString("status");
//                int count = rs.getInt("count");
//
//                statusMap.computeIfAbsent(userId, k -> new HashMap<>()).merge(status, count, Integer::sum);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            ps.setString(2, cs_id);
//            rs = ps.executeQuery();
//
//            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));
//
//            Vector<Vector<Object>> data = new Vector<>();
//            while (rs.next()) {
//                Vector<Object> row = new Vector<>();
//
//                String studentId = rs.getString("Student ID");
//                String fname = de.decrypt(rs.getString("FirstName"));
//                String mname = de.decrypt(rs.getString("MiddleName")).substring(0, 1);
//                String lname = de.decrypt(rs.getString("LastName"));
//
//                String name = fname + " " + mname + ". " + lname;
//
//                row.add(studentId);
//                row.add(name);
//                row.add(rs.getString("Section"));
//                row.add(rs.getString("Year"));
//                row.add(rs.getString("Track"));
//
//                Map<String, Integer> counts = statusMap.getOrDefault(studentId, new HashMap<>());
//                int present = counts.getOrDefault("Complete", 0);
//                int absent = counts.getOrDefault("Absent", 0);
//                int late = counts.getOrDefault("Late", 0);
//                int incomplete = counts.getOrDefault("Incomplete", 0);
//                int leftEarly = counts.getOrDefault("Early Time Out", 0);
//                int total = present + absent + late + incomplete + leftEarly;
//
//                String remarks;
//                if (total == 0) {
//                    remarks = "No Records";
//                } else {
//                    double presentRate = (double) present / total;
//                    double absentRate = (double) absent / total;
//                    double lateRate = (double) late / total;
//                    double incompleteRate = (double) incomplete / total;
//                    double earlyRate = (double) leftEarly / total;
//
//                    if (lateRate >= 0.3) {
//                        remarks = "Tardy (" + String.format("%.0f%%", lateRate * 100) + ")";
//                    } else if (presentRate >= 0.5) {
//                        remarks = "Mostly Present (" + String.format("%.0f%%", presentRate * 100) + ")";
//                    } else if (absentRate >= 0.5) {
//                        remarks = "Mostly Absent (" + String.format("%.0f%%", absentRate * 100) + ")";
//                    } else if (incompleteRate >= 0.5) {
//                        remarks = "Mostly Incomplete (" + String.format("%.0f%%", incompleteRate * 100) + ")";
//                    } else if (earlyRate >= 0.5) {
//                        remarks = "Mostly Left Early (" + String.format("%.0f%%", earlyRate * 100) + ")";
//                    } else {
//                        remarks = "Unclassified";
//                    }
//                }
//
//                row.add(remarks);
//
//                data.add(row);
//            }
//            return new DefaultTableModel(data, columnNames);
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));
//        return new DefaultTableModel(new Vector<>(), columns);
//    }
//
//    @Override
//    public Map<String, Integer> getAttendanceCountsCS(String cs_id) {
//        Map<String, Integer> statusCounts = new HashMap<>();
//        String sql = "SELECT a.status, COUNT(*) AS count "
//                + "FROM attendance a "
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "WHERE u.college = ? AND a.class_schedule_id = ? "
//                + "GROUP BY a.status";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            ps.setString(2, cs_id);
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                statusCounts.put(rs.getString("status"), rs.getInt("count"));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return statusCounts;
//    }
//
//    @Override
//    public Map<String, Map<String, Integer>> getAttendanceByGenderCS(String cs_id) {
//        Map<String, Map<String, Integer>> result = new HashMap<>();
//        String sql = "SELECT u.sex, a.status, COUNT(*) AS count "
//                + "FROM attendance a"
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "WHERE u.college = ? AND a.class_schedule_id = ? "
//                + "GROUP BY u.gender, a.status";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, college);
//            ps.setString(2, cs_id);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String gender = rs.getString("sex");
//                String status = rs.getString("status");
//                int count = rs.getInt("count");
//
//                result.computeIfAbsent(gender, k -> new HashMap<>()).merge(status, count, Integer::sum);
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    @Override
//    public AttModel getAttedanceStatusCS(String cs_id) {
//        AttModel summary = new AttModel();
//        String sql = "SELECT "
//                + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
//                + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
//                + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
//                + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
//                + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
//                + "COUNT (CASE WHEN a.status = 'Early Time Out' THEN 1 END AS left_early) "
//                + "FROM attendance a "
//                + "JOIN user u ON u.user_id = a.user_id "
//                + "JOIN student_info si ON si.user_id = a.user_id "
//                + "WHERE u.college = ? AND a.class_schedule_id = ?";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, college);
//            ps.setString(2, cs_id);
//            rs = ps.executeQuery();
//            if (rs.next()) {
//                summary.setTimeInCount(rs.getInt("time_in_count"));
//                summary.setTimeOutCount(rs.getInt("time_out_count"));
//                summary.setAbsentCount(rs.getInt("absent_count"));
//                summary.setLateCount(rs.getInt("late_count"));
//                summary.setIncompleteCount(rs.getInt("incomplete_count"));
//                summary.setLeftEarly(rs.getInt("left_early"));
//            }
//
//        } catch (SQLException e) {
//            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, e);
//        }
//
//        return summary;
//    }
//}
