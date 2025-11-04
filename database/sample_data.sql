/*
    Sample Data for Employee Management System
    Based on Danny's Enhanced Schema
    
    Includes:
    - States and cities for address normalization
    - Sample employees (can expand to 55+ for full testing)
    - Job titles and divisions
    - User accounts for authentication testing
    - Pay statements for reporting
*/

USE employeeData;

-- Insert States (sample - add all 50 as needed)
INSERT INTO state (state_id, name) VALUES 
('CA', 'California'),
('TX', 'Texas'),
('NY', 'New York'),
('FL', 'Florida'),
('IL', 'Illinois');

-- Insert Cities
INSERT INTO city (city_id, name, state_id) VALUES 
(1, 'Los Angeles', 'CA'),
(2, 'San Francisco', 'CA'),
(3, 'Houston', 'TX'),
(4, 'Dallas', 'TX'),
(5, 'New York City', 'NY'),
(6, 'Miami', 'FL'),
(7, 'Chicago', 'IL');

-- Insert Divisions
INSERT INTO division (name) VALUES 
('Engineering'),
('Human Resources'),
('Finance'),
('Marketing'),
('Operations');

-- Insert Job Titles
INSERT INTO job_titles (title) VALUES 
('Software Engineer'),
('Senior Software Engineer'),
('HR Manager'),
('Financial Analyst'),
('Marketing Specialist'),
('Operations Manager');

-- Insert Roles
INSERT INTO role (name) VALUES 
('ADMIN'),
('EMPLOYEE');

-- Insert Sample Employees
INSERT INTO employees (first_name, last_name, ssn, dob, email, hire_date, base_salary) VALUES 
('John', 'Smith', '123-45-6789', '1985-03-15', 'john.smith@companyz.com', '2020-01-15', 75000.00),
('Jane', 'Doe', '987-65-4321', '1990-07-22', 'jane.doe@companyz.com', '2021-03-10', 82000.00),
('Mike', 'Johnson', '456-78-9123', '1988-11-08', 'mike.johnson@companyz.com', '2019-06-01', 95000.00),
('Sarah', 'Wilson', '789-12-3456', '1992-02-14', 'sarah.wilson@companyz.com', '2022-01-20', 68000.00),
('David', 'Brown', '321-54-9876', '1987-09-30', 'david.brown@companyz.com', '2020-08-15', 105000.00);

-- Insert Address Information
INSERT INTO address (empid, street, city_id, zip, gender, identified_race, mobile_phone) VALUES 
(1, '123 Main St', 1, '90210', 'M', 'Caucasian', '555-0101'),
(2, '456 Oak Ave', 2, '94102', 'F', 'Hispanic', '555-0102'),
(3, '789 Pine Rd', 3, '77001', 'M', 'African American', '555-0103'),
(4, '321 Elm St', 5, '10001', 'F', 'Asian', '555-0104'),
(5, '654 Maple Dr', 7, '60601', 'M', 'Caucasian', '555-0105');

-- Insert Employee-Division Relationships
INSERT INTO employee_division (empid, div_id) VALUES 
(1, 1), -- John Smith - Engineering
(2, 1), -- Jane Doe - Engineering  
(3, 1), -- Mike Johnson - Engineering
(4, 2), -- Sarah Wilson - HR
(5, 3); -- David Brown - Finance

-- Insert Employee-Job Title Relationships
INSERT INTO employee_job_titles (empid, job_title_id, effective_date) VALUES 
(1, 1, '2020-01-15'), -- John Smith - Software Engineer
(2, 2, '2021-03-10'), -- Jane Doe - Senior Software Engineer
(3, 2, '2019-06-01'), -- Mike Johnson - Senior Software Engineer
(4, 3, '2022-01-20'), -- Sarah Wilson - HR Manager
(5, 4, '2020-08-15'); -- David Brown - Financial Analyst

-- Insert Payroll Configuration
INSERT INTO payroll (empid, pay_frequency) VALUES 
(1, 'SEMI_MONTHLY'),
(2, 'SEMI_MONTHLY'),
(3, 'SEMI_MONTHLY'),
(4, 'MONTHLY'),
(5, 'SEMI_MONTHLY');

-- Insert Sample Pay Statements
INSERT INTO pay_statement (empid, pay_date, gross, taxes, net) VALUES 
(1, '2024-10-15', 3125.00, 750.00, 2375.00),
(1, '2024-10-31', 3125.00, 750.00, 2375.00),
(2, '2024-10-15', 3416.67, 820.00, 2596.67),
(2, '2024-10-31', 3416.67, 820.00, 2596.67),
(3, '2024-10-15', 3958.33, 950.00, 3008.33),
(4, '2024-10-31', 5666.67, 1360.00, 4306.67),
(5, '2024-10-15', 4375.00, 1050.00, 3325.00);

-- Insert User Accounts (for authentication testing)
INSERT INTO user_account (empid, username, password_hash, role_id) VALUES 
(4, 'hr_admin', '$2a$10$example.hash.for.hr.admin.password', 1), -- Sarah Wilson as HR Admin
(1, 'john.smith', '$2a$10$example.hash.for.employee.password', 2), -- John Smith as Employee
(2, 'jane.doe', '$2a$10$example.hash.for.employee.password', 2), -- Jane Doe as Employee
(3, 'mike.johnson', '$2a$10$example.hash.for.employee.password', 2); -- Mike Johnson as Employee

-- Note: In production, use proper BCrypt hashed passwords
-- Example: BCrypt.hashpw("password123", BCrypt.gensalt())
