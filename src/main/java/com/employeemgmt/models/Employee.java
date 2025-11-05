package com.employeemgmt.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

/**
 * Employee model class
 * Represents a single employee record from the "employees" table.
 * 
 * Matches MySQL schema:
 * empid | Fname | Lname | email | HireDate | Salary | SSN
 */
public class Employee implements Comparable<Employee> {

    // === Private fields ===
    private int empId;
    private String fname;
    private String lname;
    private String email;
    private Date hireDate;
    private BigDecimal salary;
    private String ssn;

    // === Constructors ===

    // Default constructor
    public Employee() {
    }

    // Parameterized constructor (for quick creation)
    public Employee(String fname, String lname, String email, Date hireDate, BigDecimal salary, String ssn) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.ssn = ssn;
    }

    // === Getters and Setters ===

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        if (fname == null || fname.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        if (lname == null || lname.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than 0.");
        }
        this.salary = salary;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        if (ssn != null && !ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException("Invalid SSN format (expected ###-##-####).");
        }
        this.ssn = ssn;
    }

    // === Utility Methods ===

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name=%s %s, email=%s, hireDate=%s, salary=%.2f, ssn=%s}",
                empId, fname, lname, email, hireDate, salary, ssn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee that = (Employee) o;
        return empId == that.empId &&
                Objects.equals(email, that.email) &&
                Objects.equals(ssn, that.ssn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, email, ssn);
    }

    @Override
    public int compareTo(Employee other) {
        // Sort employees alphabetically by last name
        return this.lname.compareToIgnoreCase(other.lname);
    }
}