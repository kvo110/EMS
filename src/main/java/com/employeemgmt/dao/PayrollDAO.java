package com.employeemgmt.dao;

import com.employeemgmt.models.Payroll;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Payroll Data Access Object
 * Handles all database operations for payroll and pay statements
 */
public class PayrollDAO {
    
    private DatabaseConnection dbConnection;
    
    // SQL Queries
    private static final String GET_PAY_STATEMENT_HISTORY = """
        SELECT ps.id, ps.empid, ps.pay_date, ps.gross, ps.taxes, ps.net,
               e.first_name, e.last_name
        FROM pay_statement ps
        JOIN employees e ON ps.empid = e.empid
        WHERE ps.empid = ? AND e.active = true
        ORDER BY ps.pay_date DESC
        """;
    
    private static final String GET_TOTAL_PAY_BY_JOB_TITLE = """
        SELECT jt.title, SUM(ps.gross) as total_gross, SUM(ps.net) as total_net, COUNT(*) as employee_count
        FROM pay_statement ps
        JOIN employees e ON ps.empid = e.empid
        JOIN employee_job_titles ejt ON e.empid = ejt.empid
        JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
        WHERE YEAR(ps.pay_date) = ? AND MONTH(ps.pay_date) = ? AND e.active = true
        GROUP BY jt.job_title_id, jt.title
        ORDER BY total_gross DESC
        """;
    
    private static final String GET_TOTAL_PAY_BY_DIVISION = """
        SELECT d.name, SUM(ps.gross) as total_gross, SUM(ps.net) as total_net, COUNT(*) as employee_count
        FROM pay_statement ps
        JOIN employees e ON ps.empid = e.empid
        JOIN employee_division ed ON e.empid = ed.empid
        JOIN division d ON ed.div_id = d.id
        WHERE YEAR(ps.pay_date) = ? AND MONTH(ps.pay_date) = ? AND e.active = true
        GROUP BY d.id, d.name
        ORDER BY total_gross DESC
        """;
    
    private static final String GET_EMPLOYEES_HIRED_IN_RANGE = """
        SELECT e.empid, e.first_name, e.last_name, e.hire_date, e.base_salary,
               jt.title as job_title, d.name as division_name
        FROM employees e
        LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid
        LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
        LEFT JOIN employee_division ed ON e.empid = ed.empid
        LEFT JOIN division d ON ed.div_id = d.id
        WHERE e.hire_date >= ? AND e.hire_date <= ? AND e.active = true
        ORDER BY e.hire_date DESC
        """;
    
    private static final String INSERT_PAY_STATEMENT = """
        INSERT INTO pay_statement (empid, pay_date, gross, taxes, net)
        VALUES (?, ?, ?, ?, ?)
        """;
    
    private static final String UPDATE_PAY_STATEMENT = """
        UPDATE pay_statement 
        SET pay_date = ?, gross = ?, taxes = ?, net = ?
        WHERE id = ?
        """;
    
    private static final String DELETE_PAY_STATEMENT = """
        DELETE FROM pay_statement WHERE id = ?
        """;
    
    // Constructor
    public PayrollDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Get pay statement history for an employee (sorted by most recent)
     * @param empid The employee ID
     * @return List of pay statements
     */
    public List<PayStatementRecord> getPayStatementHistory(int empid) {
        List<PayStatementRecord> payStatements = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_PAY_STATEMENT_HISTORY);
            stmt.setInt(1, empid);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PayStatementRecord record = new PayStatementRecord();
                record.setId(rs.getLong("id"));
                record.setEmpid(rs.getInt("empid"));
                record.setPayDate(rs.getDate("pay_date").toLocalDate());
                record.setGross(rs.getBigDecimal("gross"));
                record.setTaxes(rs.getBigDecimal("taxes"));
                record.setNet(rs.getBigDecimal("net"));
                record.setEmployeeName(rs.getString("first_name") + " " + rs.getString("last_name"));
                
                payStatements.add(record);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pay statement history: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return payStatements;
    }
    
    /**
     * Get total pay by job title for a specific month/year (HR Admin only)
     * @param month The month (1-12)
     * @param year The year
     * @return List of job title pay summaries
     */
    public List<PaySummaryByJobTitle> getTotalPayByJobTitle(int month, int year) {
        List<PaySummaryByJobTitle> summaries = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_TOTAL_PAY_BY_JOB_TITLE);
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PaySummaryByJobTitle summary = new PaySummaryByJobTitle();
                summary.setJobTitle(rs.getString("title"));
                summary.setTotalGross(rs.getBigDecimal("total_gross"));
                summary.setTotalNet(rs.getBigDecimal("total_net"));
                summary.setEmployeeCount(rs.getInt("employee_count"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total pay by job title: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return summaries;
    }
    
    /**
     * Get total pay by division for a specific month/year (HR Admin only)
     * @param month The month (1-12)
     * @param year The year
     * @return List of division pay summaries
     */
    public List<PaySummaryByDivision> getTotalPayByDivision(int month, int year) {
        List<PaySummaryByDivision> summaries = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_TOTAL_PAY_BY_DIVISION);
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PaySummaryByDivision summary = new PaySummaryByDivision();
                summary.setDivisionName(rs.getString("name"));
                summary.setTotalGross(rs.getBigDecimal("total_gross"));
                summary.setTotalNet(rs.getBigDecimal("total_net"));
                summary.setEmployeeCount(rs.getInt("employee_count"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total pay by division: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return summaries;
    }
    
    /**
     * Get employees hired within a date range
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of employees hired in the date range
     */
    public List<EmployeeHireRecord> getEmployeesHiredInDateRange(LocalDate startDate, LocalDate endDate) {
        List<EmployeeHireRecord> employees = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_EMPLOYEES_HIRED_IN_RANGE);
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EmployeeHireRecord record = new EmployeeHireRecord();
                record.setEmpid(rs.getInt("empid"));
                record.setFirstName(rs.getString("first_name"));
                record.setLastName(rs.getString("last_name"));
                record.setHireDate(rs.getDate("hire_date").toLocalDate());
                record.setBaseSalary(rs.getBigDecimal("base_salary"));
                record.setJobTitle(rs.getString("job_title"));
                record.setDivisionName(rs.getString("division_name"));
                
                employees.add(record);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting employees hired in date range: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return employees;
    }
    
    /**
     * Save a new pay statement
     * @param payroll The payroll record to save
     * @return true if successful, false otherwise
     */
    public boolean save(Payroll payroll) {
        if (payroll == null || !payroll.isValid()) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_PAY_STATEMENT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, payroll.getEmpid());
            stmt.setDate(2, Date.valueOf(payroll.getPayDate()));
            stmt.setBigDecimal(3, payroll.getGrossPay());
            stmt.setBigDecimal(4, payroll.getTotalTaxDeductions());
            stmt.setBigDecimal(5, payroll.getNetPay());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    payroll.setPayrollId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving pay statement: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Update an existing pay statement
     * @param payroll The payroll record to update
     * @return true if successful, false otherwise
     */
    public boolean update(Payroll payroll) {
        if (payroll == null || !payroll.isValid() || payroll.getPayrollId() <= 0) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_PAY_STATEMENT);
            
            stmt.setDate(1, Date.valueOf(payroll.getPayDate()));
            stmt.setBigDecimal(2, payroll.getGrossPay());
            stmt.setBigDecimal(3, payroll.getTotalTaxDeductions());
            stmt.setBigDecimal(4, payroll.getNetPay());
            stmt.setInt(5, payroll.getPayrollId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating pay statement: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Delete a pay statement
     * @param id The pay statement ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(long id) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_PAY_STATEMENT);
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting pay statement: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    // Helper classes for report data
    public static class PayStatementRecord {
        private long id;
        private int empid;
        private LocalDate payDate;
        private BigDecimal gross;
        private BigDecimal taxes;
        private BigDecimal net;
        private String employeeName;
        
        // Getters and setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public int getEmpid() { return empid; }
        public void setEmpid(int empid) { this.empid = empid; }
        
        public LocalDate getPayDate() { return payDate; }
        public void setPayDate(LocalDate payDate) { this.payDate = payDate; }
        
        public BigDecimal getGross() { return gross; }
        public void setGross(BigDecimal gross) { this.gross = gross; }
        
        public BigDecimal getTaxes() { return taxes; }
        public void setTaxes(BigDecimal taxes) { this.taxes = taxes; }
        
        public BigDecimal getNet() { return net; }
        public void setNet(BigDecimal net) { this.net = net; }
        
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    }
    
    public static class PaySummaryByJobTitle {
        private String jobTitle;
        private BigDecimal totalGross;
        private BigDecimal totalNet;
        private int employeeCount;
        
        // Getters and setters
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        
        public BigDecimal getTotalGross() { return totalGross; }
        public void setTotalGross(BigDecimal totalGross) { this.totalGross = totalGross; }
        
        public BigDecimal getTotalNet() { return totalNet; }
        public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }
        
        public int getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
    }
    
    public static class PaySummaryByDivision {
        private String divisionName;
        private BigDecimal totalGross;
        private BigDecimal totalNet;
        private int employeeCount;
        
        // Getters and setters
        public String getDivisionName() { return divisionName; }
        public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
        
        public BigDecimal getTotalGross() { return totalGross; }
        public void setTotalGross(BigDecimal totalGross) { this.totalGross = totalGross; }
        
        public BigDecimal getTotalNet() { return totalNet; }
        public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }
        
        public int getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
    }
    
    public static class EmployeeHireRecord {
        private int empid;
        private String firstName;
        private String lastName;
        private LocalDate hireDate;
        private BigDecimal baseSalary;
        private String jobTitle;
        private String divisionName;
        
        // Getters and setters
        public int getEmpid() { return empid; }
        public void setEmpid(int empid) { this.empid = empid; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getFullName() { return firstName + " " + lastName; }
        
        public LocalDate getHireDate() { return hireDate; }
        public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
        
        public BigDecimal getBaseSalary() { return baseSalary; }
        public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
        
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        
        public String getDivisionName() { return divisionName; }
        public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
    }
}
