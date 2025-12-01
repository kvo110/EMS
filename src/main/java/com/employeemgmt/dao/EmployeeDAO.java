package com.employeemgmt.dao;

import com.employeemgmt.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
    EmployeeDAO
    -----------
    This class is where all the direct SQL for employees lives.
    The UI should never talk to the database directly, it should go through here.
*/
public class EmployeeDAO {

    // helper that knows how to open connections using database.properties
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    // maps one row from ResultSet into a nice Employee object
    private Employee mapRow(ResultSet rs) throws SQLException {
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

    // find a single employee by ID
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

    // return all employees in the table
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

    // case-insensitive search by first and/or last name
    public List<Employee> findByName(String first, String last) {
        List<Employee> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");

        if (first != null && !first.isBlank()) {
            sql.append(" AND LOWER(Fname) LIKE ?");
        }
        if (last != null && !last.isBlank()) {
            sql.append(" AND LOWER(Lname) LIKE ?");
        }

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;

            if (first != null && !first.isBlank()) {
                ps.setString(idx++, "%" + first.toLowerCase() + "%");
            }
            if (last != null && !last.isBlank()) {
                ps.setString(idx++, "%" + last.toLowerCase() + "%");
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

    // find a single employee by SSN
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

    // insert a new employee row
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
                    // update the object with the new primary key
                    emp.setEmpid(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error saving employee: " + e.getMessage());
        }
        return false;
    }

    // update an existing employee row
    public boolean update(Employee emp) {
        String sql = """
            UPDATE employees
            SET Fname = ?, Lname = ?, email = ?, HireDate = ?, Salary = ?, SSN = ?
            WHERE empid = ?
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

    // delete an employee and related address/user_account rows
    public boolean delete(int empid) {

        try (Connection conn = db.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("DELETE FROM address WHERE empid = ?");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM user_account WHERE empid = ?");
             PreparedStatement ps3 = conn.prepareStatement("DELETE FROM employees WHERE empid = ?")) {

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

    // used by Salary Tools to give everyone in a salary band a raise
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
            System.out.println("Error updateSalaryByRange: " + e.getMessage());
            return 0;
        }
    }
}
