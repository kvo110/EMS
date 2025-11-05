package com.employeemgmt.dao;

import com.employeemgmt.models.Payroll;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payroll Data Access Object
 * 
 * Handles payroll-related database operations including:
 * - Pay statement history retrieval
 * - Report generation queries (total pay by job title/division)
 * - Employee hiring date range queries
 * - CRUD operations for payroll records
 */
public class PayrollDAO {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructor
     * @param dbConnection DatabaseConnection instance
     */
    public PayrollDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    /**
     * Get pay statement history for a specific employee
     * Sorted by most recent pay date first
     * 
     * @param empid Employee ID
     * @return List of Payroll records sorted by pay date (most recent first)
     * @throws SQLException if database error occurs
     */
    public List<Payroll> getPayStatementHistory(int empid) throws SQLException {
        List<Payroll> payStatements = new ArrayList<>();
        String sql = "SELECT id, empid, pay_date, gross, taxes, net " +
                     "FROM pay_statement " +
                     "WHERE empid = ? " +
                     "ORDER BY pay_date DESC";
        
        try (Connection conn = dbConnection.getEmployeeConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, empid);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payStatements.add(mapResultSetToPayroll(rs));
                }
            }
        }
        
        return payStatements;
    }
    
    /**
     * Get total pay by job title for a specific month and year
     * Used for HR Admin reports
     * 
     * @param month Month (1-12)
     * @param year Year (e.g., 2024)
     * @return Map of job title to total pay amount
     * @throws SQLException if database error occurs
     */
    public Map<String, Double> getTotalPayByJobTitle(int month, int year) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT jt.title, COALESCE(SUM(ps.gross), 0) as total_pay " +
                     "FROM job_titles jt " +
                     "LEFT JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id " +
                     "LEFT JOIN pay_statement ps ON ejt.empid = ps.empid " +
                     "AND MONTH(ps.pay_date) = ? AND YEAR(ps.pay_date) = ? " +
                     "GROUP BY jt.job_title_id, jt.title " +
                     "ORDER BY jt.title";
        
        try (Connection conn = dbConnection.getAdminConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String jobTitle = rs.getString("title");
                    double totalPay = rs.getDouble("total_pay");
                    result.put(jobTitle, totalPay);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get total pay by division for a specific month and year
     * Used for HR Admin reports
     * 
     * @param month Month (1-12)
     * @param year Year (e.g., 2024)
     * @return Map of division name to total pay amount
     * @throws SQLException if database error occurs
     */
    public Map<String, Double> getTotalPayByDivision(int month, int year) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT d.name as division_name, COALESCE(SUM(ps.gross), 0) as total_pay " +
                     "FROM division d " +
                     "LEFT JOIN employee_division ed ON d.id = ed.div_id " +
                     "LEFT JOIN pay_statement ps ON ed.empid = ps.empid " +
                     "AND MONTH(ps.pay_date) = ? AND YEAR(ps.pay_date) = ? " +
                     "GROUP BY d.id, d.name " +
                     "ORDER BY d.name";
        
        try (Connection conn = dbConnection.getAdminConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String divisionName = rs.getString("division_name");
                    double totalPay = rs.getDouble("total_pay");
                    result.put(divisionName, totalPay);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get employees hired within a date range
     * Used for HR Admin reports
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of employee IDs hired in the date range
     * @throws SQLException if database error occurs
     */
    public List<Integer> getEmployeesHiredInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Integer> employeeIds = new ArrayList<>();
        String sql = "SELECT empid FROM employees " +
                     "WHERE hire_date BETWEEN ? AND ? " +
                     "ORDER BY hire_date DESC";
        
        try (Connection conn = dbConnection.getAdminConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employeeIds.add(rs.getInt("empid"));
                }
            }
        }
        
        return employeeIds;
    }
    
    /**
     * Save a new payroll record
     * 
     * @param payroll Payroll object to save
     * @return Generated ID of the new record, or -1 if insertion failed
     * @throws SQLException if database error occurs
     */
    public long save(Payroll payroll) throws SQLException {
        String sql = "INSERT INTO pay_statement (empid, pay_date, gross, taxes, net) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getAdminConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, payroll.getEmpid());
            pstmt.setDate(2, Date.valueOf(payroll.getPayDate()));
            pstmt.setBigDecimal(3, payroll.getGross());
            pstmt.setBigDecimal(4, payroll.getTaxes());
            pstmt.setBigDecimal(5, payroll.getNet());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    payroll.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        }
    }
    
    /**
     * Update an existing payroll record
     * 
     * @param payroll Payroll object to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean update(Payroll payroll) throws SQLException {
        String sql = "UPDATE pay_statement " +
                     "SET empid = ?, pay_date = ?, gross = ?, taxes = ?, net = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = dbConnection.getAdminConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, payroll.getEmpid());
            pstmt.setDate(2, Date.valueOf(payroll.getPayDate()));
            pstmt.setBigDecimal(3, payroll.getGross());
            pstmt.setBigDecimal(4, payroll.getTaxes());
            pstmt.setBigDecimal(5, payroll.getNet());
            pstmt.setLong(6, payroll.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Find payroll record by ID
     * 
     * @param id Payroll record ID
     * @return Payroll object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public Payroll findById(long id) throws SQLException {
        String sql = "SELECT id, empid, pay_date, gross, taxes, net " +
                     "FROM pay_statement " +
                     "WHERE id = ?";
        
        try (Connection conn = dbConnection.getEmployeeConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayroll(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to Payroll object
     * 
     * @param rs ResultSet from database query
     * @return Payroll object
     * @throws SQLException if database error occurs
     */
    private Payroll mapResultSetToPayroll(ResultSet rs) throws SQLException {
        Payroll payroll = new Payroll();
        payroll.setId(rs.getLong("id"));
        payroll.setEmpid(rs.getInt("empid"));
        
        Date payDate = rs.getDate("pay_date");
        if (payDate != null) {
            payroll.setPayDate(payDate.toLocalDate());
        }
        
        payroll.setGross(rs.getBigDecimal("gross"));
        payroll.setTaxes(rs.getBigDecimal("taxes"));
        payroll.setNet(rs.getBigDecimal("net"));
        
        return payroll;
    }
}
