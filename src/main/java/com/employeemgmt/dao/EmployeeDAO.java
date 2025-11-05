package com.employeemgmt.dao;

import com.employeemgmt.models.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee Data Access Object (DAO)
 * Handles all direct interactions with the "employees" table.
 * 
 * This version matches the MySQL schema:
 * empid | Fname | Lname | email | HireDate | Salary | SSN
 */
public class EmployeeDAO {

    private final Connection conn;

    // Constructor gets a live database connection
    public EmployeeDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new employee into the database.
     * Returns true if successful, false otherwise.
     */
    public boolean create(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getFname());
            stmt.setString(2, emp.getLname());
            stmt.setString(3, emp.getEmail());
            stmt.setDate(4, emp.getHireDate());
            stmt.setBigDecimal(5, emp.getSalary());
            stmt.setString(6, emp.getSsn());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Finds a single employee by their ID.
     */
    public Employee findByEmpId(int empId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE empid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Finds all employees whose name (first or last) matches a keyword.
     * Used for admin searches.
     */
    public List<Employee> findByNameOrId(String keyword) throws SQLException {
        String sql = "SELECT * FROM employees WHERE Fname LIKE ? OR Lname LIKE ?";
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapResultSet(rs));
            }
        }
        return employees;
    }

    /**
     * Returns a single employee as a list (for non-admin users).
     */
    public List<Employee> findByEmpIdList(int empId) throws SQLException {
        List<Employee> list = new ArrayList<>();
        Employee emp = findByEmpId(empId);
        if (emp != null) list.add(emp);
        return list;
    }

    /**
     * Updates an existing employee’s information.
     */
    public boolean update(Employee emp) throws SQLException {
        String sql = "UPDATE employees SET Fname=?, Lname=?, email=?, HireDate=?, Salary=?, SSN=? WHERE empid=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getFname());
            stmt.setString(2, emp.getLname());
            stmt.setString(3, emp.getEmail());
            stmt.setDate(4, emp.getHireDate());
            stmt.setBigDecimal(5, emp.getSalary());
            stmt.setString(6, emp.getSsn());
            stmt.setInt(7, emp.getEmpId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Finds employees whose salaries fall within a given range.
     * Used for the "batch salary update" feature.
     */
    public List<Employee> findBySalaryRange(double minSalary, double maxSalary) throws SQLException {
        String sql = "SELECT * FROM employees WHERE Salary BETWEEN ? AND ?";
        List<Employee> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minSalary);
            stmt.setDouble(2, maxSalary);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Helper method to convert a ResultSet row into an Employee object.
     */
    private Employee mapResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpId(rs.getInt("empid"));
        emp.setFname(rs.getString("Fname"));
        emp.setLname(rs.getString("Lname"));
        emp.setEmail(rs.getString("email"));
        emp.setHireDate(rs.getDate("HireDate"));
        emp.setSalary(rs.getBigDecimal("Salary"));
        emp.setSsn(rs.getString("SSN"));
        return emp;
    }
}