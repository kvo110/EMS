package com.employeemgmt.dao;

import com.employeemgmt.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
   EmployeeDAO.java
   ----------------
   This file handles all direct SQL calls for employees.
   I tried to keep the queries simple because our schema
   at this point is pretty clean (empid, fname, lname, etc).

   Notes for team members:
   - Every method opens a connection, does its job, and closes it.
   - We always map SQL rows → Employee objects inside this DAO.
   - No UI logic belongs here.
*/
public class EmployeeDAO {

    // Just grabbing our DB connection helper
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    // Helper function to turn one row into an Employee object
    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmpid(rs.getInt("empid"));
        e.setFirstName(rs.getString("Fname"));
        e.setLastName(rs.getString("Lname"));
        e.setEmail(rs.getString("email"));
        e.setHireDate(rs.getDate("HireDate") != null ? rs.getDate("HireDate").toLocalDate() : null);
        e.setBaseSalary(rs.getBigDecimal("Salary"));
        e.setSsn(rs.getString("SSN"));
        return e;
    }

    // ------------------------------
    // Find by ID
    // ------------------------------
    public Optional<Employee> findById(int empId) {
        String sql = "SELECT * FROM employees WHERE empid = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            System.out.println("Error findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ------------------------------
    // Find all employees
    // ------------------------------
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY empid";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.out.println("Error findAll: " + e.getMessage());
        }

        return list;
    }

    // ------------------------------
    // Find by name (first + last)
    // Partial matches allowed
    // ------------------------------
    public List<Employee> findByName(String first, String last) {
        List<Employee> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");

        if (first != null && !first.isBlank()) {
            sql.append(" AND Fname LIKE ?");
        }
        if (last != null && !last.isBlank()) {
            sql.append(" AND Lname LIKE ?");
        }

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;

            if (first != null && !first.isBlank()) {
                ps.setString(idx++, "%" + first + "%");
            }
            if (last != null && !last.isBlank()) {
                ps.setString(idx++, "%" + last + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.out.println("Error findByName: " + e.getMessage());
        }

        return list;
    }

    // ------------------------------
    // Find by SSN
    // ------------------------------
    public Optional<Employee> findBySSN(String ssn) {
        String sql = "SELECT * FROM employees WHERE SSN = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (Exception e) {
            System.out.println("Error findBySSN: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ------------------------------
    // INSERT employees
    // ------------------------------
    public boolean save(Employee emp) {
        String sql = """
            INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setDate(4, emp.getHireDate() != null ? Date.valueOf(emp.getHireDate()) : null);
            ps.setBigDecimal(5, emp.getBaseSalary());
            ps.setString(6, emp.getSsn());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    emp.setEmpid(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error saving employee: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------
    // UPDATE employees
    // ------------------------------
    public boolean update(Employee emp) {
        String sql = """
            UPDATE employees
            SET Fname=?, Lname=?, email=?, HireDate=?, Salary=?, SSN=?
            WHERE empid=?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setDate(4, emp.getHireDate() != null ? Date.valueOf(emp.getHireDate()) : null);
            ps.setBigDecimal(5, emp.getBaseSalary());
            ps.setString(6, emp.getSsn());
            ps.setInt(7, emp.getEmpid());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error update: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------
    // DELETE employees
    // Note: Must delete referencing FK rows first (address, user_account)
    // ------------------------------
    public boolean delete(int empid) {

        // Delete address records first
        try (Connection conn = db.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("DELETE FROM address WHERE empid=?");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM user_account WHERE empid=?");
             PreparedStatement ps3 = conn.prepareStatement("DELETE FROM employees WHERE empid=?")) {

            conn.setAutoCommit(false);

            ps1.setInt(1, empid);
            ps1.executeUpdate();

            ps2.setInt(1, empid);
            ps2.executeUpdate();

            ps3.setInt(1, empid);
            int rows = ps3.executeUpdate();

            conn.commit();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error delete: " + e.getMessage());
        }

        return false;
    }

    // ------------------------------
    // Salary update by range
    // ------------------------------
    public int updateSalaryByRange(double percent, double min, double max) {
        String sql = """
            UPDATE employees
            SET Salary = Salary * (1 + (? / 100))
            WHERE Salary BETWEEN ? AND ?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, percent);
            ps.setDouble(2, min);
            ps.setDouble(3, max);

            return ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error updateSalaryRange: " + e.getMessage());
            return 0;
        }
    }
}