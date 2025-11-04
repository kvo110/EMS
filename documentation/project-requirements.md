# Employee Management System - Project Requirements

## User Story
Your software development team is hired as consultants and tasked with delivering a new, working employee management system for company 'Z'. The project requires a software design document as a set of designs for this software system involving its data schemas, UX, general programming components and security.

## Current State
- Company 'Z' has one HR admin person maintaining employee data using dBeaver and MySQL scripts
- No security except for HR admin password
- About 55 full-time employees (no hourly or part-time)
- Plan to triple employee count within 18 months

## Required System Features

### 1. User Authentication & Authorization
- **HR Admin:** Secure CRUD functionality on entire database
- **General Employee:** SELECT-only access to their own data

### 2. Employee Search Functionality
- Search by: name, DOB, SSN, empid
- **HR Admin:** Can search and edit any employee
- **General Employee:** Can search and view their own data only

### 3. Employee Data Management
- **HR Admin only:** Update employee data from search results
- **HR Admin only:** Add new employees to system

### 4. Salary Management  
- **HR Admin only:** Update employee salaries by percentage for specific salary ranges
- Example: 3.2% increase for salaries ≥$58K but <$105K

### 5. Reporting System
- **General Employee:** View personal pay statement history (sorted by most recent)
- **HR Admin:** Total pay for month by job title
- **HR Admin:** Total pay for month by division  
- **HR Admin:** Employees hired within date range

## Technical Requirements

### Database
- **Must use MySQL database**
- Enhanced schema with additional tables:
  - Address table (street, city, state, zip) with empid as primary key
  - City table (ID for 20 or less cities)
  - State table (ID for all 50 states)
  - Additional demographics: gender, identified_race, DOB, mobile/phone

### Primary/Foreign Key Relationships
- employees.empid (primary) → employee_division.empid (foreign)
- employees.empid (primary) → payroll.empid (foreign)
- employees.empid (primary) → address.empid (foreign)  
- employees.empid (primary) → employee_job_titles.empid (foreign)
- employee_division.div_ID (foreign) → division.ID (primary)
- employee_job_titles.job_title_id (foreign) → job_titles.job_title_id (primary)

### User Interface Options
- **Console/Terminal interface** (character-based)
- **GUI options:** JavaFX or Swing
- **Extra Credit:** HTML with microframeworks, Node.js, services to connect to MySQL

### Programming Language
- **Java only** for core application
- JDBC for database connectivity
- Object-oriented design with proper class organization

## Security Requirements
- Secure user authentication
- Role-based access control
- Password protection
- Data validation and sanitization
- Audit trail for sensitive operations

## Performance Requirements
- Support current 55 employees
- Scale to handle 165+ employees (triple growth)
- Efficient search and reporting
- Database indexing for performance
