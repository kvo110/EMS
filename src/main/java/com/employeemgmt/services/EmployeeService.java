package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.UserRole;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Employee Service Layer
 * 
 * TODO: Implement business logic for employee operations
 * 
 * This layer sits between the UI and DAO layers and handles:
 * - Business rule validation
 * - Transaction management
 * - Data transformation
 * - Complex business operations
 * 
 * Methods to implement:
 * - searchEmployees(SearchCriteria criteria, UserRole userRole)
 * - updateEmployee(Employee employee, UserRole userRole)
 * - addNewEmployee(Employee employee, UserRole userRole)
 * - updateSalariesByPercentage(double percentage, double minSalary, double maxSalary)
 * - validateEmployeeData(Employee employee)
 * 
 * Requirements:
 * - Enforce business rules (salary ranges, required fields, etc.)
 * - Handle role-based access control
 * - Validate data before database operations
 * - Manage transactions for complex operations
 */
public class EmployeeService {
    
    // TODO: Add DAO dependencies
    private final EmployeeDAO employeeDAO;
    private final Connection conn;
    
    // TODO: Implement constructor with dependency injection
    public EmployeeService(Connection conn, EmployeeDAO employeeDAO) {
        this.conn = conn;
        this.employeeDAO = employeeDAO;
    }

    // TODO: Implement business logic methods
    public boolean addNewEmployee(Employee employee, UserRole userRole) throws SQLException {
        if (!isAdmin(userRole)) {
            System.out.println("Access Denied: Only HR Admins");
            return false;
        }

        if (!validateEmployeeData(employee)) {
            System.out.println("Invalid employee data: please check required fields");
            return false;
        }

        employeeDAO.create(employee);
        System.out.println("Employee added successfully");
        return true;
    }
    
    public boolean updateEmployee(Employee employee, UserRole userRole) throws SQLException {
        if (!isAdmin(userRole)) {
            System.out.println("Access denied: only HR Admins can update employee information.");
            return false;
        }

        Employee existing = employeeDAO.findByEmpId(employee.getEmpId());
        if (existing == null) {
            System.out.println("Employee not found.");
            return false;
        }

        if (!validateEmployeeData(employee)) {
            System.out.println("Invalid data provided. Update failed.");
            return false;
        }

        employeeDAO.update(employee);
        System.out.println("Employee record updated successfully.");
        return true;
    }

    public List<Employee> searchEmployees(String keyword, UserRole userRole, int empId) throws SQLException {
        if (isAdmin(userRole)) {
            return employeeDAO.findByNameOrId(keyword);
        } else {
            // Non-admins can only see themselves
            return employeeDAO.findByEmpIdList(empId);
        }
    }

    public int updateSalariesByPercentage(double percentage, double minSalary, double maxSalary, UserRole userRole)
            throws SQLException {

        if (!isAdmin(userRole)) {
            System.out.println("Access denied: only HR Admins can perform batch salary updates.");
            return -1;
        }

        if (percentage <= 0 || minSalary < 0 || maxSalary < minSalary) {
            System.out.println("Invalid parameters for salary update.");
            return -2;
        }

        List<Employee> targetList = employeeDAO.findBySalaryRange(minSalary, maxSalary);
        if (targetList.isEmpty()) {
            System.out.println("No employees found within the specified range.");
            return 0;
        }

        try {
            conn.setAutoCommit(false); // start transaction

            for (Employee emp : targetList) {
                BigDecimal oldSalary = emp.getSalary();
                BigDecimal newSalary = oldSalary.multiply(BigDecimal.valueOf(1 + (percentage / 100.0)));
                emp.setSalary(newSalary);
                employeeDAO.update(emp);
            }

            conn.commit();
            System.out.println("Salaries updated for " + targetList.size() + " employees.");
            return targetList.size();

        } catch (Exception e) {
            conn.rollback();
            System.err.println("Error during salary update: " + e.getMessage());
            return -3;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // TODO: Add validation methods
    private boolean validateEmployeeData(Employee emp) {
        if (emp == null) return false;
        if (emp.getFullName() == null || emp.getFullName().isEmpty()) return false;
        if (emp.getJobTitle() == null || emp.getJobTitle().isEmpty()) return false;
        if (emp.getSalary() == null || emp.getSalary().doubleValue() <= 0) return false;
        return true;
    }
    
    // TODO: Add transaction management
    private boolean isAdmin(UserRole role) {
        return role != null && role.equals(UserRole.HR_ADMIN);
    }
}