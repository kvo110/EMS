# Employee Management System - Team Project

## Project Overview
A comprehensive employee management system for Company 'Z' built with Java and MySQL. The system provides secure, role-based access for HR administrators and general employees to manage employee data, payroll, and generate reports.

## 🗂️ Project Structure

```
Employee-Management-System-TeamProject/
├── README.md                          # This file
├── database/                          # Database scripts and schema
│   ├── employeeData_MySQL_create.sql  # Original database schema
│   ├── enhanced_schema.sql            # Enhanced schema with additional tables
│   ├── sample_data.sql               # Sample data for testing
│   └── database_setup.sql            # Database setup and security
├── src/                              # Java source code
│   ├── main/java/com/employeemgmt/
│   │   ├── Main.java                 # Application entry point
│   │   ├── models/                   # Data models (Employee, Address, Payroll, User)
│   │   ├── dao/                      # Data Access Objects
│   │   ├── services/                 # Business logic layer
│   │   ├── controllers/              # Controller layer
│   │   ├── ui/                       # User interface (Console/JavaFX)
│   │   └── utils/                    # Utility classes (Validation, Security)
│   └── test/java/                    # Test classes
├── uml-diagrams/                     # UML diagrams and documentation
├── documentation/                    # Project documentation
├── deliverables/                     # Assignment deliverables
│   ├── individual/                   # Individual assignments
│   └── group/                        # Group assignments
├── resources/                        # Configuration files
└── lib/                             # External libraries
```

## 📅 Project Timeline & Deadlines

### ⚠️ CRITICAL DEADLINES

| Date | Type | Points | Assignment | Status |
|------|------|--------|------------|--------|
| **11/02/2025 11:59PM** | Individual | 150pts | UML Use Case + Sequence + DB Schema | ❌ |
| **11/11/2025 11:59PM** | Group | 160pts | Programming Tasks + Test Cases | ❌ |
| **11/16/2025 11:59PM** | Individual | 110pts | Sequence Diagrams (Salary + Add Employee) | ❌ |
| **12/08/2025 11:59PM** | Group | 150pts | Software Design Document (SDD) | ❌ |
| **12/11/2025 11:59PM** | Group | 250pts | Final Demo Video + Code | ❌ |

> **⚠️ NO LATE PENALTIES ALLOWED** for Group assignments (11/11, 12/08, 12/11)

## 🎯 Key Features

### User Roles
- **HR Admin**: Full CRUD access to all employee data
- **General Employee**: Read-only access to personal data

### Core Functionality
- ✅ Secure user authentication and authorization
- ✅ Employee search (name, DOB, SSN, empid)
- ✅ Employee data management (CRUD operations)
- ✅ Salary updates by percentage for salary ranges
- ✅ Comprehensive reporting system
- ✅ Pay statement history tracking

### Reports Available
- **General Employee**: Personal pay statement history
- **HR Admin**: Total pay by job title (monthly)
- **HR Admin**: Total pay by division (monthly)
- **HR Admin**: Employees hired within date range

## 🛠️ Technology Stack

- **Language**: Java
- **Database**: MySQL
- **UI Options**: Console/Terminal or JavaFX/Swing
- **Database Connectivity**: JDBC
- **Testing**: JUnit
- **Security**: BCrypt password hashing, role-based access

## 🚀 Getting Started

### Prerequisites
- Java JDK 11 or higher
- MySQL Server 8.0+
- MySQL Connector/J (JDBC driver)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Setup Instructions
1. **Database Setup**
   ```bash
   # Run database scripts in order:
   mysql -u root -p < database/database_setup.sql
   mysql -u root -p < database/employeeData_MySQL_create.sql
   mysql -u root -p < database/enhanced_schema.sql
   mysql -u root -p < database/sample_data.sql
   ```

2. **Configuration**
   - Update `resources/database.properties` with your MySQL credentials
   - Configure security settings and connection parameters

3. **Compilation & Execution**
   ```bash
   # Compile Java files
   javac -cp "lib/*:src/main/java" src/main/java/com/employeemgmt/Main.java
   
   # Run application
   java -cp "lib/*:src/main/java" com.employeemgmt.Main
   ```

## 📋 Individual Assignments

### Due 11/02/2025 (150pts)
- [ ] **UML Use Case Diagram** - Overall system components and actors
- [ ] **Sequence Diagram** - Employee search functionality  
- [ ] **Database Schema Diagram** - Generated from dBeaver

### Due 11/16/2025 (110pts)
- [ ] **Sequence Diagram** - Salary increase by percentage
- [ ] **Sequence Diagram** - Add new employee process

## 📋 Group Assignments

### Due 11/11/2025 (160pts) - NO LATE PENALTY
- [ ] **5 Programming Tasks** from user story requirements
- [ ] **Test Cases** for: update employee, search employee, salary update

### Due 12/08/2025 (150pts) - NO LATE PENALTY  
- [ ] **Software Design Document (SDD)** - Complete technical documentation

### Due 12/11/2025 (250pts) - NO LATE PENALTY
- [ ] **Demo Video** (10-15 minutes) - Working system demonstration
- [ ] **Java Source Code** - Complete implementation

## 🧪 Testing Strategy

### Test Categories
1. **Unit Tests** - Individual component testing
2. **Integration Tests** - Database connectivity and service layer
3. **Security Tests** - Authentication and authorization
4. **Performance Tests** - Large dataset handling

### Required Test Cases
- **a)** Update employee data (general functionality)
- **b)** Search for employee (admin user access)
- **c)** Update salary for employees below threshold

## 🔒 Security Features

- **Authentication**: Secure login system with password hashing
- **Authorization**: Role-based access control (HR Admin vs Employee)
- **Data Protection**: Encryption for sensitive data (SSN, etc.)
- **Input Validation**: SQL injection prevention and data sanitization
- **Session Management**: Secure session handling and timeout

## 📊 Database Schema

### Original Tables
- `employees` - Core employee information
- `payroll` - Payroll and deduction data
- `job_titles` - Job title definitions
- `employee_job_titles` - Employee-job title relationships
- `division` - Company divisions
- `employee_division` - Employee-division relationships

### Enhanced Tables (To Be Added)
- `address` - Employee addresses with city/state normalization
- `city` - City lookup table (≤20 entries)
- `state` - State lookup table (50 states)
- Additional demographic fields in employee table

## 🎥 Demo Video Requirements

### Technical Specs
- **Duration**: 10-15 minutes (continuous, no editing)
- **Format**: MPEG-4, MPEG, WMV, AVI, MKV, WebM, 3GP, MOV
- **Content**: Live software demonstration with presenter visible
- **Slides**: Maximum 5 PowerPoint slides allowed

### Demo Content
1. System login (both user types)
2. Employee search and data viewing
3. Employee data updates (HR Admin)
4. New employee creation (HR Admin)
5. Report generation
6. Pay statement viewing (Employee)

## 📞 Support & Resources

### Tools Recommended
- **UML Diagrams**: Lucidchart, Draw.io, or hand-drawn (neat)
- **Database**: dBeaver for schema management
- **IDE**: IntelliJ IDEA or Eclipse
- **Version Control**: Git (recommended for team collaboration)

### Important Notes
- ⚠️ **No late submissions** for group assignments
- 📁 **Avoid monolithic code** - use proper class organization
- 🔐 **Never hardcode passwords** - use configuration files
- 📝 **Document all code** with proper comments
- 🧪 **Test thoroughly** before final submission

## 📈 Grading Breakdown

| Component | Points | Type | Due Date |
|-----------|--------|------|----------|
| UML Diagrams (Initial) | 150 | Individual | 11/02/2025 |
| Programming Tasks + Tests | 160 | Group | 11/11/2025 |
| Sequence Diagrams (Final) | 110 | Individual | 11/16/2025 |
| Software Design Document | 150 | Group | 12/08/2025 |
| Final Demo + Code | 250 | Group | 12/11/2025 |
| **Total** | **820** | | |

---

**Good luck with your Employee Management System project! 🚀**

*Remember: Start early, work consistently, and don't hesitate to ask for help when needed.*
