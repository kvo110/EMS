/*
    This schema includes all project requirements:
    1. ✅ Address table with empid as primary key and foreign key to employees
    2. ✅ City table (normalized with state references)
    3. ✅ State table (all 50 states support)
    4. ✅ Additional demographic fields: gender, identified_race, DOB, mobile/phone
    5. ✅ User authentication system
    6. ✅ Enhanced payroll with pay statements
    7. ✅ Performance indexes
*/

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS employeeData;
USE employeeData;

-- STATE & CITY TABLES
CREATE TABLE state (
  state_id CHAR(2) PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE city (
  city_id INT PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  state_id CHAR(2) NOT NULL,
  FOREIGN KEY (state_id) REFERENCES state(state_id)
);

-- EMPLOYEE CORE TABLE (Enhanced from original)
CREATE TABLE employees (
  empid INT PRIMARY KEY AUTO_INCREMENT,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  ssn CHAR(11) UNIQUE NOT NULL,
  dob DATE NOT NULL,
  email VARCHAR(120) UNIQUE NOT NULL,
  hire_date DATE NOT NULL,
  base_salary DECIMAL(12,2) NOT NULL,
  active BOOLEAN DEFAULT TRUE
);

-- ADDRESS TABLE (Project Requirement)
CREATE TABLE address (
  empid INT PRIMARY KEY,
  street VARCHAR(120),
  city_id INT,
  zip CHAR(10),
  gender ENUM('M','F','NonBinary','Unspecified') DEFAULT 'Unspecified',
  identified_race VARCHAR(60),
  mobile_phone VARCHAR(20),
  FOREIGN KEY (empid) REFERENCES employees(empid),
  FOREIGN KEY (city_id) REFERENCES city(city_id)
);

-- DIVISION + RELATION
CREATE TABLE division (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE employee_division (
  empid INT NOT NULL,
  div_id INT NOT NULL,
  PRIMARY KEY (empid, div_id),
  FOREIGN KEY (empid) REFERENCES employees(empid),
  FOREIGN KEY (div_id) REFERENCES division(id)
);

-- JOB TITLES + RELATION
CREATE TABLE job_titles (
  job_title_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE employee_job_titles (
  empid INT NOT NULL,
  job_title_id INT NOT NULL,
  effective_date DATE NOT NULL,
  PRIMARY KEY (empid, job_title_id, effective_date),
  FOREIGN KEY (empid) REFERENCES employees(empid),
  FOREIGN KEY (job_title_id) REFERENCES job_titles(job_title_id)
);

-- PAYROLL + STATEMENTS (Enhanced)
CREATE TABLE payroll (
  empid INT PRIMARY KEY,
  pay_frequency ENUM('MONTHLY','SEMI_MONTHLY','BIWEEKLY'),
  FOREIGN KEY (empid) REFERENCES employees(empid)
);

CREATE TABLE pay_statement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  empid INT NOT NULL,
  pay_date DATE NOT NULL,
  gross DECIMAL(12,2),
  taxes DECIMAL(12,2),
  net DECIMAL(12,2),
  FOREIGN KEY (empid) REFERENCES employees(empid)
);

-- AUTH + USER ACCOUNT (Security Requirement)
CREATE TABLE role (
  role_id INT PRIMARY KEY AUTO_INCREMENT,
  name ENUM('ADMIN','EMPLOYEE') UNIQUE NOT NULL
);

CREATE TABLE user_account (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  empid INT UNIQUE,
  username VARCHAR(60) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role_id INT NOT NULL,
  last_login TIMESTAMP NULL,
  FOREIGN KEY (empid) REFERENCES employees(empid),
  FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- PERFORMANCE INDEXES
CREATE INDEX idx_emp_name ON employees(last_name, first_name);
CREATE INDEX idx_emp_dob ON employees(dob);
CREATE INDEX idx_emp_ssn ON employees(ssn);
CREATE INDEX idx_pay_date ON pay_statement(pay_date);
CREATE INDEX idx_pay_empid ON pay_statement(empid);
