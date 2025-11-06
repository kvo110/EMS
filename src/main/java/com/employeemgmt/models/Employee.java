package com.employeemgmt.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Employee model class
 * Matches the enhanced database schema with normalized fields
 */
public class Employee implements Comparable<Employee> {
    
    // Core employee fields (from employees table)
    private int empid;
    private String firstName;
    private String lastName;
    private String ssn;
    private LocalDate dob;
    private String email;
    private LocalDate hireDate;
    private BigDecimal baseSalary;
    private boolean active;
    
    // Address fields (from address table)
    private String street;
    private int cityId;
    private String zip;
    private String gender;
    private String identifiedRace;
    private String mobilePhone;
    
    // Related entity fields (for display purposes)
    private String cityName;
    private String stateName;
    private String divisionName;
    private String jobTitle;
    
    // Default constructor
    public Employee() {
        this.active = true; // Default to active
    }
    
    // Constructor with core fields
    public Employee(String firstName, String lastName, String ssn, LocalDate dob, 
                   String email, LocalDate hireDate, BigDecimal baseSalary) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.dob = dob;
        this.email = email;
        this.hireDate = hireDate;
        this.baseSalary = baseSalary;
    }
    
    // Full constructor
    public Employee(int empid, String firstName, String lastName, String ssn, LocalDate dob,
                   String email, LocalDate hireDate, BigDecimal baseSalary, boolean active,
                   String street, int cityId, String zip, String gender, 
                   String identifiedRace, String mobilePhone) {
        this.empid = empid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.dob = dob;
        this.email = email;
        this.hireDate = hireDate;
        this.baseSalary = baseSalary;
        this.active = active;
        this.street = street;
        this.cityId = cityId;
        this.zip = zip;
        this.gender = gender;
        this.identifiedRace = identifiedRace;
        this.mobilePhone = mobilePhone;
    }
    
    // Getters and Setters
    public int getEmpid() { return empid; }
    public void setEmpid(int empid) { this.empid = empid; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName.trim(); 
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName.trim(); 
    }
    
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { 
        if (ssn != null && !ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException("SSN must be in format XXX-XX-XXXX");
        }
        this.ssn = ssn; 
    }
    
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { 
        if (dob != null && dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        this.dob = dob; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email; 
    }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { 
        if (baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.baseSalary = baseSalary; 
    }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public int getCityId() { return cityId; }
    public void setCityId(int cityId) { this.cityId = cityId; }
    
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getIdentifiedRace() { return identifiedRace; }
    public void setIdentifiedRace(String identifiedRace) { this.identifiedRace = identifiedRace; }
    
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    
    // Display fields (read-only)
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    
    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    // Business logic methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return Period.between(hireDate, LocalDate.now()).getYears();
    }
    
    public String getFormattedSalary() {
        if (baseSalary == null) return "$0.00";
        return String.format("$%,.2f", baseSalary);
    }
    
    // Validation method
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               ssn != null && ssn.matches("\\d{3}-\\d{2}-\\d{4}") &&
               dob != null && hireDate != null &&
               baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Comparable implementation (sort by last name, then first name)
    @Override
    public int compareTo(Employee other) {
        if (other == null) return 1;
        
        int lastNameComparison = this.lastName.compareToIgnoreCase(other.lastName);
        if (lastNameComparison != 0) {
            return lastNameComparison;
        }
        return this.firstName.compareToIgnoreCase(other.firstName);
    }
    
    // Object methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Employee employee = (Employee) obj;
        return empid == employee.empid && Objects.equals(ssn, employee.ssn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(empid, ssn);
    }
    
    @Override
    public String toString() {
        return String.format("Employee{empid=%d, name='%s %s', email='%s', salary=%s, active=%s}",
                empid, firstName, lastName, email, getFormattedSalary(), active);
    }
}