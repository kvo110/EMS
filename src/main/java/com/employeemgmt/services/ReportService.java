package com.employeemgmt.services;

import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.models.Payroll;
import com.employeemgmt.models.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Report Service Layer
 * 
 * Handles report generation business logic with role-based access control:
 * - General employees can only view their own pay statement history
 * - HR Admin can view all reports (total pay by job title/division, hiring reports)
 * 
 * Methods:
 * - generatePayStatementHistory(int empid, User user) - for general employees (own data only)
 * - generateTotalPayByJobTitle(int month, int year, User user) - for HR Admin only
 * - generateTotalPayByDivision(int month, int year, User user) - for HR Admin only
 * - generateEmployeesHiredReport(LocalDate startDate, LocalDate endDate, User user) - for HR Admin only
 */
public class ReportService {
    
    private final PayrollDAO payrollDAO;
    
    /**
     * Constructor
     * @param payrollDAO PayrollDAO instance
     */
    public ReportService(PayrollDAO payrollDAO) {
        this.payrollDAO = payrollDAO;
    }
    
    /**
     * Generate pay statement history for an employee
     * 
     * Role-based access:
     * - General employees can only view their own pay statements
     * - HR Admin can view any employee's pay statements
     * 
     * @param empid Employee ID
     * @param user Current user making the request
     * @return List of Payroll records (pay statements)
     * @throws SecurityException if user doesn't have permission
     * @throws Exception if database error occurs
     */
    public List<Payroll> generatePayStatementHistory(int empid, User user) throws SecurityException, Exception {
        // Check access permission
        if (!hasAccessToPayStatementHistory(empid, user)) {
            throw new SecurityException(
                "Access denied: You can only view your own pay statement history. " +
                "HR Admin can view any employee's pay statements."
            );
        }
        
        try {
            return payrollDAO.getPayStatementHistory(empid);
        } catch (Exception e) {
            throw new Exception("Error generating pay statement history: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate total pay by job title report for a specific month and year
     * 
     * Role-based access: HR Admin only
     * 
     * @param month Month (1-12)
     * @param year Year (e.g., 2024)
     * @param user Current user making the request
     * @return Map of job title to total pay amount
     * @throws SecurityException if user is not HR Admin
     * @throws Exception if database error occurs
     */
    public Map<String, Double> generateTotalPayByJobTitle(int month, int year, User user) 
            throws SecurityException, Exception {
        // Check access permission - HR Admin only
        if (!user.isHRAdmin()) {
            throw new SecurityException(
                "Access denied: This report is only available to HR Admin users."
            );
        }
        
        // Validate month range
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        
        try {
            return payrollDAO.getTotalPayByJobTitle(month, year);
        } catch (Exception e) {
            throw new Exception("Error generating total pay by job title report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate total pay by division report for a specific month and year
     * 
     * Role-based access: HR Admin only
     * 
     * @param month Month (1-12)
     * @param year Year (e.g., 2024)
     * @param user Current user making the request
     * @return Map of division name to total pay amount
     * @throws SecurityException if user is not HR Admin
     * @throws Exception if database error occurs
     */
    public Map<String, Double> generateTotalPayByDivision(int month, int year, User user) 
            throws SecurityException, Exception {
        // Check access permission - HR Admin only
        if (!user.isHRAdmin()) {
            throw new SecurityException(
                "Access denied: This report is only available to HR Admin users."
            );
        }
        
        // Validate month range
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        
        try {
            return payrollDAO.getTotalPayByDivision(month, year);
        } catch (Exception e) {
            throw new Exception("Error generating total pay by division report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate employees hired report for a date range
     * 
     * Role-based access: HR Admin only
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param user Current user making the request
     * @return List of employee IDs hired in the date range
     * @throws SecurityException if user is not HR Admin
     * @throws IllegalArgumentException if date range is invalid
     * @throws Exception if database error occurs
     */
    public List<Integer> generateEmployeesHiredReport(LocalDate startDate, LocalDate endDate, User user) 
            throws SecurityException, IllegalArgumentException, Exception {
        // Check access permission - HR Admin only
        if (!user.isHRAdmin()) {
            throw new SecurityException(
                "Access denied: This report is only available to HR Admin users."
            );
        }
        
        // Validate date range
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        try {
            return payrollDAO.getEmployeesHiredInDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new Exception("Error generating employees hired report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Format pay statement history report as a string
     * 
     * @param payStatements List of pay statements
     * @return Formatted string representation of the report
     */
    public String formatPayStatementHistoryReport(List<Payroll> payStatements) {
        if (payStatements == null || payStatements.isEmpty()) {
            return "No pay statements found.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("PAY STATEMENT HISTORY REPORT\n");
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%-12s %-12s %-15s %-15s %-15s\n", 
                "Pay Date", "Employee ID", "Gross Pay", "Taxes", "Net Pay"));
        report.append("-".repeat(80)).append("\n");
        
        double totalGross = 0;
        double totalTaxes = 0;
        double totalNet = 0;
        
        for (Payroll payroll : payStatements) {
            report.append(String.format("%-12s %-12d $%-14.2f $%-14.2f $%-14.2f\n",
                    payroll.getPayDate(),
                    payroll.getEmpid(),
                    payroll.getGross().doubleValue(),
                    payroll.getTaxes().doubleValue(),
                    payroll.getNet().doubleValue()));
            
            totalGross += payroll.getGross().doubleValue();
            totalTaxes += payroll.getTaxes().doubleValue();
            totalNet += payroll.getNet().doubleValue();
        }
        
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("%-12s %-12s $%-14.2f $%-14.2f $%-14.2f\n",
                "TOTALS", "", totalGross, totalTaxes, totalNet));
        report.append("=".repeat(80)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Format total pay by job title report as a string
     * 
     * @param reportData Map of job title to total pay
     * @param month Month
     * @param year Year
     * @return Formatted string representation of the report
     */
    public String formatTotalPayByJobTitleReport(Map<String, Double> reportData, int month, int year) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("TOTAL PAY BY JOB TITLE REPORT - %s %d\n", getMonthName(month), year));
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%-50s %-20s\n", "Job Title", "Total Pay"));
        report.append("-".repeat(80)).append("\n");
        
        double grandTotal = 0;
        
        for (Map.Entry<String, Double> entry : reportData.entrySet()) {
            report.append(String.format("%-50s $%-19.2f\n", entry.getKey(), entry.getValue()));
            grandTotal += entry.getValue();
        }
        
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("%-50s $%-19.2f\n", "GRAND TOTAL", grandTotal));
        report.append("=".repeat(80)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Format total pay by division report as a string
     * 
     * @param reportData Map of division name to total pay
     * @param month Month
     * @param year Year
     * @return Formatted string representation of the report
     */
    public String formatTotalPayByDivisionReport(Map<String, Double> reportData, int month, int year) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("TOTAL PAY BY DIVISION REPORT - %s %d\n", getMonthName(month), year));
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%-50s %-20s\n", "Division", "Total Pay"));
        report.append("-".repeat(80)).append("\n");
        
        double grandTotal = 0;
        
        for (Map.Entry<String, Double> entry : reportData.entrySet()) {
            report.append(String.format("%-50s $%-19.2f\n", entry.getKey(), entry.getValue()));
            grandTotal += entry.getValue();
        }
        
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("%-50s $%-19.2f\n", "GRAND TOTAL", grandTotal));
        report.append("=".repeat(80)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Format employees hired report as a string
     * 
     * @param employeeIds List of employee IDs
     * @param startDate Start date
     * @param endDate End date
     * @return Formatted string representation of the report
     */
    public String formatEmployeesHiredReport(List<Integer> employeeIds, LocalDate startDate, LocalDate endDate) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("EMPLOYEES HIRED REPORT - %s to %s\n", startDate, endDate));
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("Total Employees Hired: %d\n", employeeIds.size()));
        report.append("-".repeat(80)).append("\n");
        
        if (employeeIds.isEmpty()) {
            report.append("No employees were hired in this date range.\n");
        } else {
            report.append("Employee IDs:\n");
            for (int i = 0; i < employeeIds.size(); i++) {
                report.append(employeeIds.get(i));
                if (i < employeeIds.size() - 1) {
                    report.append(", ");
                }
                if ((i + 1) % 10 == 0) {
                    report.append("\n");
                }
            }
            report.append("\n");
        }
        
        report.append("=".repeat(80)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Check if user has access to pay statement history for a given employee
     * 
     * @param empid Employee ID
     * @param user Current user
     * @return true if user has access, false otherwise
     */
    private boolean hasAccessToPayStatementHistory(int empid, User user) {
        if (user == null) {
            return false;
        }
        
        // HR Admin has access to all employees
        if (user.isHRAdmin()) {
            return true;
        }
        
        // General employees can only access their own data
        if (user.isGeneralEmployee() && user.getEmpid() != null) {
            return user.getEmpid().equals(empid);
        }
        
        return false;
    }
    
    /**
     * Get month name from month number
     * 
     * @param month Month number (1-12)
     * @return Month name
     */
    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        
        if (month >= 1 && month <= 12) {
            return monthNames[month - 1];
        }
        
        return "Unknown";
    }
}
