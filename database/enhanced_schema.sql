/*
    Enhanced Database Schema for Employee Management System
    
    TODO: Create additional tables and relationships as specified in project requirements:
    
    1. Address table with empid as primary key and foreign key to employees
    2. City table (ID for cities - 20 or less entries)
    3. State table (ID for all 50 states)
    4. Additional demographic fields: gender, identified_race, DOB, mobile/phone
    
    Primary/Foreign Key Relationships to implement:
    - employees.empid (primary) to employee_division.empid (foreign)
    - employees.empid (primary) to payroll.empid (foreign)
    - employees.empid (primary) to address.empid (foreign)
    - employees.empid (primary) to employee_job_titles.empid (foreign)
    - employee_division.div_ID (foreign) to division.ID (primary)
    - employee_job_titles.job_title_id (foreign) to job_titles.job_title_id (primary)
    
    Indexes to create for performance optimization
*/

-- TODO: Implement enhanced schema here
