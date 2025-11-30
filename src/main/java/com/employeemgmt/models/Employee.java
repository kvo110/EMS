package com.employeemgmt.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee model class
 * Matches real MySQL schema:
 *
 * employees(
 *   empid INT,
 *   Fname VARCHAR,
 *   Lname VARCHAR,
 *   email VARCHAR,
 *   HireDate DATE,
 *   Salary DECIMAL,
 *   SSN VARCHAR
 * )
 */
public class Employee implements Comparable<Employee> {

    // Core DB fields
    private int empid;
    private String firstName;
    private String lastName;
    private String ssn;
    private String email;
    private LocalDate hireDate;
    private BigDecimal baseSalary;

    // Optional fields (not in DB)
    private LocalDate dob;
    private boolean active = true;

    private String street;
    private Integer cityId;  // must be Integer (nullable)
    private String zip;
    private String gender;
    private String identifiedRace;
    private String mobilePhone;

    private String cityName;
    private String stateName;
    private String divisionName;
    private String jobTitle;

    public Employee() {}

    // Minimal constructor matching DB schema
    public Employee(int empid, String firstName, String lastName,
                    String email, LocalDate hireDate,
                    BigDecimal baseSalary, String ssn) {

        this.empid = empid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.baseSalary = baseSalary;
        this.ssn = ssn;
    }

    // Getters/Setters
    public int getEmpid() { return empid; }
    public void setEmpid(int empid) { this.empid = empid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getIdentifiedRace() { return identifiedRace; }
    public void setIdentifiedRace(String identifiedRace) { this.identifiedRace = identifiedRace; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    // Business logic (optional)
    public String getFullName() { return firstName + " " + lastName; }

    public String getFormattedSalary() {
        return baseSalary != null ? String.format("$%,.2f", baseSalary) : "$0.00";
    }

    // Validation ONLY checks the actual DB columns
    public boolean isValid() {
        return firstName != null && !firstName.isBlank() &&
               lastName != null && !lastName.isBlank() &&
               email != null && !email.isBlank() &&
               ssn != null &&
               hireDate != null &&
               baseSalary != null;
    }

    @Override
    public int compareTo(Employee o) {
        int cmp = this.lastName.compareToIgnoreCase(o.lastName);
        return (cmp != 0) ? cmp : this.firstName.compareToIgnoreCase(o.firstName);
    }
}