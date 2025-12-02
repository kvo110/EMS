package com.employeemgmt.dao;

import com.employeemgmt.models.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Quick DAO just for reporting queries
public class ReportDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    // Used for job title + division totals
    public static class NameAndTotal {
        private final String name;
        private final BigDecimal totalNet;

        public NameAndTotal(String name, BigDecimal totalNet) {
            this.name = name;
            this.totalNet = totalNet;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getTotalNet() {
            return totalNet;
        }
    }

    private Employee mapEmployeeRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmpid(rs.getInt("empid"));
        e.setFirstName(rs.getString("Fname"));
        e.setLastName(rs.getString("Lname"));
        e.setEmail(rs.getString("email"));
        Date hire = rs.getDate("HireDate");
        e.setHireDate(hire != null ? hire.toLocalDate() : null);
        e.setBaseSalary(rs.getBigDecimal("Salary"));
        e.setSsn(rs.getString("SSN"));
        return e;
    }

    // Total net pay for a given year/month grouped by job title
    public List<NameAndTotal> totalNetPayByJob(int year, int month) {
        List<NameAndTotal> list = new ArrayList<>();

        String sql = """
            SELECT jt.job_title AS name,
                   SUM(ps.net_pay) AS total_net
            FROM pay_statements ps
            JOIN employees e        ON e.empid = ps.empid
            JOIN employee_job ej    ON ej.empid = e.empid
            JOIN job_title jt       ON jt.job_id = ej.job_id
            WHERE YEAR(ps.pay_date) = ?
              AND MONTH(ps.pay_date) = ?
            GROUP BY jt.job_title
            ORDER BY jt.job_title
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                BigDecimal total = rs.getBigDecimal("total_net");
                list.add(new NameAndTotal(name, total));
            }
        } catch (Exception e) {
            System.out.println("Error totalNetPayByJob: " + e.getMessage());
        }

        return list;
    }

    // Total net pay for a given year/month grouped by division
    public List<NameAndTotal> totalNetPayByDivision(int year, int month) {
        List<NameAndTotal> list = new ArrayList<>();

        String sql = """
            SELECT d.Name AS name,
                   SUM(ps.net_pay) AS total_net
            FROM pay_statements ps
            JOIN employees e           ON e.empid = ps.empid
            JOIN employee_division ed  ON ed.empid = e.empid
            JOIN division d            ON d.ID = ed.div_ID
            WHERE YEAR(ps.pay_date) = ?
              AND MONTH(ps.pay_date) = ?
            GROUP BY d.ID, d.Name
            ORDER BY d.Name
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                BigDecimal total = rs.getBigDecimal("total_net");
                list.add(new NameAndTotal(name, total));
            }
        } catch (Exception e) {
            System.out.println("Error totalNetPayByDivision: " + e.getMessage());
        }

        return list;
    }

    // Employees hired between two dates
    public List<Employee> employeesHiredBetween(LocalDate start, LocalDate end) {
        List<Employee> list = new ArrayList<>();

        String sql = """
            SELECT *
            FROM employees
            WHERE HireDate BETWEEN ? AND ?
            ORDER BY HireDate, empid
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapEmployeeRow(rs));
            }
        } catch (Exception e) {
            System.out.println("Error employeesHiredBetween: " + e.getMessage());
        }

        return list;
    }
}
