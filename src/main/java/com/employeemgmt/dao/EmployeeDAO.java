package com.employeemgmt.dao;

import com.employeemgmt.models.Employee;
import com.employeemgmt.utils.SecurityUtils;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Employee Data Access Object
 * Handles all database operations for Employee entities with comprehensive search functionality
 */
public class EmployeeDAO {
    
    private DatabaseConnection dbConnection;
    
    // Base query with all employee and address information
    private static final String BASE_EMPLOYEE_QUERY = """
        SELECT e.empid, e.first_name, e.last_name, e.ssn, e.dob, e.email, e.hire_date, 
               e.base_salary, e.active,
               a.street, a.city_id, a.zip, a.gender, a.identified_race, a.mobile_phone,
               c.name as city_name, s.name as state_name,
               d.name as division_name, jt.title as job_title
        FROM employees e
        LEFT JOIN address a ON e.empid = a.empid
        LEFT JOIN city c ON a.city_id = c.city_id
        LEFT JOIN state s ON c.state_id = s.state_id
        LEFT JOIN employee_division ed ON e.empid = ed.empid
        LEFT JOIN division d ON ed.div_id = d.id
        LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid
        LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
        """;
    
    // Search queries
    private static final String FIND_BY_ID = BASE_EMPLOYEE_QUERY + "WHERE e.empid = ?";
    
    private static final String FIND_BY_NAME = BASE_EMPLOYEE_QUERY + 
        "WHERE LOWER(e.first_name) LIKE LOWER(?) AND LOWER(e.last_name) LIKE LOWER(?)";
    
    private static final String FIND_BY_SSN = BASE_EMPLOYEE_QUERY + "WHERE e.ssn = ?";
    
    private static final String FIND_BY_DOB = BASE_EMPLOYEE_QUERY + "WHERE e.dob = ?";
    
    private static final String FIND_ALL_ACTIVE = BASE_EMPLOYEE_QUERY + "WHERE e.active = true ORDER BY e.last_name, e.first_name";
    
    private static final String SEARCH_EMPLOYEES = BASE_EMPLOYEE_QUERY + """
        WHERE (? IS NULL OR e.empid = ?)
        AND (? IS NULL OR LOWER(e.first_name) LIKE LOWER(?))
        AND (? IS NULL OR LOWER(e.last_name) LIKE LOWER(?))
        AND (? IS NULL OR e.ssn = ?)
        AND (? IS NULL OR e.dob = ?)
        AND e.active = true
        ORDER BY e.last_name, e.first_name
        """;
    
    // CRUD operations
    private static final String INSERT_EMPLOYEE = """
        INSERT INTO employees (first_name, last_name, ssn, dob, email, hire_date, base_salary, active)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
    
    private static final String UPDATE_EMPLOYEE = """
        UPDATE employees 
        SET first_name = ?, last_name = ?, ssn = ?, dob = ?, email = ?, 
            hire_date = ?, base_salary = ?, active = ?
        WHERE empid = ?
        """;
    
    private static final String DELETE_EMPLOYEE = """
        UPDATE employees SET active = false WHERE empid = ?
        """;
    
    private static final String UPDATE_SALARY_BY_RANGE = """
        UPDATE employees 
        SET base_salary = base_salary * (1 + ? / 100)
        WHERE base_salary >= ? AND base_salary < ? AND active = true
        """;
    
    // Constructor
    public EmployeeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Find employee by ID
     * @param empid The employee ID
     * @return Optional containing Employee if found, empty otherwise
     */
    public Optional<Employee> findById(int empid) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID);
            stmt.setInt(1, empid);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding employee by ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find employees by name (supports partial matching)
     * @param firstName First name (can be partial, use % for wildcards)
     * @param lastName Last name (can be partial, use % for wildcards)
     * @return List of matching employees
     */
    public List<Employee> findByName(String firstName, String lastName) {
        List<Employee> employees = new ArrayList<>();
        
        // Sanitize inputs
        firstName = SecurityUtils.sanitizeInput(firstName);
        lastName = SecurityUtils.sanitizeInput(lastName);
        
        if (SecurityUtils.containsSQLInjectionPatterns(firstName) || 
            SecurityUtils.containsSQLInjectionPatterns(lastName)) {
            return employees; // Return empty list for security
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_NAME);
            
            // Add wildcards for partial matching if not already present
            String firstNamePattern = firstName.contains("%") ? firstName : "%" + firstName + "%";
            String lastNamePattern = lastName.contains("%") ? lastName : "%" + lastName + "%";
            
            stmt.setString(1, firstNamePattern);
            stmt.setString(2, lastNamePattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding employees by name: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return employees;
    }
    
    /**
     * Find employee by SSN
     * @param ssn The Social Security Number
     * @return Optional containing Employee if found, empty otherwise
     */
    public Optional<Employee> findBySSN(String ssn) {
        if (ssn == null || !ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
            return Optional.empty();
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_SSN);
            stmt.setString(1, ssn);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding employee by SSN: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find employees by date of birth
     * @param dob The date of birth
     * @return List of employees with matching DOB
     */
    public List<Employee> findByDOB(LocalDate dob) {
        List<Employee> employees = new ArrayList<>();
        
        if (dob == null) {
            return employees;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_DOB);
            stmt.setDate(1, Date.valueOf(dob));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding employees by DOB: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return employees;
    }
    
    /**
     * Get all active employees
     * @return List of all active employees
     */
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_ALL_ACTIVE);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all employees: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return employees;
    }
    
    /**
     * Advanced search with multiple criteria
     * @param searchCriteria The search criteria object
     * @return List of matching employees
     */
    public List<Employee> searchEmployees(SearchCriteria searchCriteria) {
        List<Employee> employees = new ArrayList<>();
        
        if (searchCriteria == null) {
            return employees;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(SEARCH_EMPLOYEES);
            
            // Set parameters (each parameter is set twice for the IS NULL check and the actual comparison)
            Integer empid = searchCriteria.getEmpid();
            stmt.setObject(1, empid);
            stmt.setObject(2, empid);
            
            String firstName = searchCriteria.getFirstName();
            if (firstName != null) {
                firstName = SecurityUtils.sanitizeInput(firstName);
                firstName = firstName.contains("%") ? firstName : "%" + firstName + "%";
            }
            stmt.setString(3, firstName);
            stmt.setString(4, firstName);
            
            String lastName = searchCriteria.getLastName();
            if (lastName != null) {
                lastName = SecurityUtils.sanitizeInput(lastName);
                lastName = lastName.contains("%") ? lastName : "%" + lastName + "%";
            }
            stmt.setString(5, lastName);
            stmt.setString(6, lastName);
            
            String ssn = searchCriteria.getSsn();
            stmt.setString(7, ssn);
            stmt.setString(8, ssn);
            
            LocalDate dob = searchCriteria.getDob();
            stmt.setObject(9, dob != null ? Date.valueOf(dob) : null);
            stmt.setObject(10, dob != null ? Date.valueOf(dob) : null);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return employees;
    }
    
    /**
     * Save a new employee
     * @param employee The employee to save
     * @return true if successful, false otherwise
     */
    public boolean save(Employee employee) {
        if (employee == null || !employee.isValid()) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getSsn());
            stmt.setDate(4, Date.valueOf(employee.getDob()));
            stmt.setString(5, employee.getEmail());
            stmt.setDate(6, Date.valueOf(employee.getHireDate()));
            stmt.setBigDecimal(7, employee.getBaseSalary());
            stmt.setBoolean(8, employee.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated employee ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setEmpid(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving employee: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Update an existing employee
     * @param employee The employee to update
     * @return true if successful, false otherwise
     */
    public boolean update(Employee employee) {
        if (employee == null || !employee.isValid() || employee.getEmpid() <= 0) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_EMPLOYEE);
            
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getSsn());
            stmt.setDate(4, Date.valueOf(employee.getDob()));
            stmt.setString(5, employee.getEmail());
            stmt.setDate(6, Date.valueOf(employee.getHireDate()));
            stmt.setBigDecimal(7, employee.getBaseSalary());
            stmt.setBoolean(8, employee.isActive());
            stmt.setInt(9, employee.getEmpid());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Soft delete an employee (set active = false)
     * @param empid The employee ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(int empid) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_EMPLOYEE);
            stmt.setInt(1, empid);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Update salaries by percentage for employees in a salary range
     * @param percentage The percentage increase (e.g., 3.2 for 3.2%)
     * @param minSalary Minimum salary (inclusive)
     * @param maxSalary Maximum salary (exclusive)
     * @return Number of employees updated
     */
    public int updateSalaryByRange(double percentage, double minSalary, double maxSalary) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SALARY_BY_RANGE);
            
            stmt.setDouble(1, percentage);
            stmt.setDouble(2, minSalary);
            stmt.setDouble(3, maxSalary);
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating salaries: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return 0;
    }
    
    /**
     * Create Employee object from ResultSet
     * @param rs The ResultSet containing employee data
     * @return Employee object
     * @throws SQLException if database error occurs
     */
    private Employee createEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        
        // Core employee data
        employee.setEmpid(rs.getInt("empid"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setSsn(rs.getString("ssn"));
        
        Date dobDate = rs.getDate("dob");
        if (dobDate != null) {
            employee.setDob(dobDate.toLocalDate());
        }
        
        employee.setEmail(rs.getString("email"));
        
        Date hireDateDate = rs.getDate("hire_date");
        if (hireDateDate != null) {
            employee.setHireDate(hireDateDate.toLocalDate());
        }
        
        employee.setBaseSalary(rs.getBigDecimal("base_salary"));
        employee.setActive(rs.getBoolean("active"));
        
        // Address data
        employee.setStreet(rs.getString("street"));
        employee.setCityId(rs.getInt("city_id"));
        employee.setZip(rs.getString("zip"));
        employee.setGender(rs.getString("gender"));
        employee.setIdentifiedRace(rs.getString("identified_race"));
        employee.setMobilePhone(rs.getString("mobile_phone"));
        
        // Display data
        employee.setCityName(rs.getString("city_name"));
        employee.setStateName(rs.getString("state_name"));
        employee.setDivisionName(rs.getString("division_name"));
        employee.setJobTitle(rs.getString("job_title"));
        
        return employee;
    }
    
    /**
     * Search criteria class for advanced employee search
     */
    public static class SearchCriteria {
        private Integer empid;
        private String firstName;
        private String lastName;
        private String ssn;
        private LocalDate dob;
        
        // Constructors
        public SearchCriteria() {}
        
        public SearchCriteria(Integer empid, String firstName, String lastName, String ssn, LocalDate dob) {
            this.empid = empid;
            this.firstName = firstName;
            this.lastName = lastName;
            this.ssn = ssn;
            this.dob = dob;
        }
        
        // Getters and Setters
        public Integer getEmpid() { return empid; }
        public void setEmpid(Integer empid) { this.empid = empid; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getSsn() { return ssn; }
        public void setSsn(String ssn) { this.ssn = ssn; }
        
        public LocalDate getDob() { return dob; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        
        public boolean isEmpty() {
            return empid == null && 
                   (firstName == null || firstName.trim().isEmpty()) &&
                   (lastName == null || lastName.trim().isEmpty()) &&
                   (ssn == null || ssn.trim().isEmpty()) &&
                   dob == null;
        }
    }
}
