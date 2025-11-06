# Employee Management System - Team Project

## Project Overview

A comprehensive employee management system for Company 'Z' built with Java and MySQL. The system provides secure, role-based access for HR administrators and general employees to manage employee data, payroll, and generate reports.

## 👥 Team Contributors

### Programming Tasks Implementation

- **Danny Nguyen**: Project lead, database design, Task 1 (User Authentication), Task 2 (Employee Search)
- **Huy Vo**: Task 3 (Employee CRUD Operations), Task 4 (Salary Update by Percentage)
- **Prakash Rizal**: Task 5 (Report Generation System)

### Individual Contributions

- **Danny Nguyen**: UML diagrams, database schema design, project architecture
- **Team Collaboration**: Code integration, testing, and final submission preparation

## 🗂️ Project Structure

```

Employee-Management-System-TeamProject/

├── README.md                          # This file

├── database/                          # Database scripts and schema

│   ├── employeeData_MySQL_create.sql  # Original database schema

│   ├── enhanced_schema.sql            # Enhanced schema with additional tables

│   ├── sample_data.sql               # Sample data for testing

│   ├── database_setup.sql            # Database setup and security

│   └── mysql-workbench-setup.md      # MySQL Workbench specific instructions

├── src/                              # Java source code

│   ├── main/java/com/employeemgmt/

│   │   ├── Main.java                 # Application entry point

│   │   ├── models/                   # Data models (Employee, Address, Payroll, User)

│   │   ├── dao/                      # Data Access Objects

│   │   ├── services/                 # Business logic layer

│   │   ├── controllers/              # Controller layer

│   │   ├── ui/                       # User interface (Console/JavaFX)

│   │   └── utils/                    # Utility classes (Validation, Security)

│   └── test/java/                 # ✅ COMPREHENSIVE TESTING
│       ├── FinalAllTasksTest.java        # ✅ Main comprehensive test (All 5 Tasks)
│       ├── AuthenticationSystemTest.java # ✅ Task 1: User Authentication
│       ├── EmployeeSearchTest.java       # ✅ Task 2: Employee Search
│       ├── EmployeeCRUDTest.java         # ✅ Task 3: Employee CRUD
│       ├── SalaryUpdateTest.java         # ✅ Task 4: Salary Updates
│       ├── AllTasksTest.java             # ✅ Sequential test runner
│       ├── DatabaseConnectionTest.java   # ✅ Database connectivity
│       └── README.md                     # ✅ Test documentation

├── uml-diagrams/                     # UML diagrams and documentation

│   ├── Danny's_Schema_Diagram.png    # ✅ COMPLETED - Database schema

│   ├── UML_Use_Case.png             # ✅ COMPLETED - Use case diagram  

│   └── UML_Sequence_Diagram.png  # ✅ Employee search sequence
│
├── documentation/                # Project documentation
└── deliverables/                # Assignment deliverables
    ├── individual/              # Individual assignments
    └── group/                   # Group assignments

```

### 📊 Task Implementation Details

| Task                        | Implementer   | Status      | Database Connected |
| --------------------------- | ------------- | ----------- | ------------------ |
| Task 1: User Authentication | Danny Nguyen  | ✅ Complete | ✅ Yes             |
| Task 2: Employee Search     | Danny Nguyen  | ✅ Complete | ✅ Yes             |
| Task 3: Employee CRUD       | Huy Vo        | ✅ Complete | ✅ Yes             |
| Task 4: Salary Updates      | Huy Vo        | ✅ Complete | ✅ Yes             |
| Task 5: Report Generation   | Prakash Rizal | ✅ Complete | ✅ Yes             |

## 📅 Project Timeline & Deadlines

### ⚠️ CRITICAL DEADLINES

| Date | Type | Points | Assignment | Status |

|------|------|--------|------------|--------|

| **11/02/2025 11:59PM** | Individual | 150pts | UML Use Case + Sequence + DB Schema | ✅ **COMPLETED** |

| **11/11/2025 11:59PM** | Group | 160pts | Programming Tasks + Test Cases | ✅ **COMPLETED** |

| **11/16/2025 11:59PM** | Individual | 110pts | Sequence Diagrams (Salary + Add Employee) | ✅ **COMPLETED** |

| **12/08/2025 11:59PM** | Group | 150pts | Software Design Document (SDD) | ❌ |

| **12/11/2025 11:59PM** | Group | 250pts | Final Demo Video + Code | ❌ |

## 🎯 Key Features

### User Roles

-**HR Admin**: Full CRUD access to all employee data

-**General Employee**: Read-only access to personal data

### Core Functionality

- ✅ Secure user authentication and authorization
- ✅ Employee search (name, DOB, SSN, empid)
- ✅ Employee data management (CRUD operations)
- ✅ Salary updates by percentage for salary ranges
- ✅ Comprehensive reporting system
- ✅ Pay statement history tracking

### Reports Available

-**General Employee**: Personal pay statement history

-**HR Admin**: Total pay by job title (monthly)

-**HR Admin**: Total pay by division (monthly)

-**HR Admin**: Employees hired within date range

## 🛠️ Technology Stack

-**Language**: Java

-**Database**: MySQL with **MySQL Workbench**

-**UI Options**: Console/Terminal or JavaFX/Swing

-**Database Connectivity**: JDBC

-**Testing**: JUnit

-**Security**: BCrypt password hashing, role-based access

## 🚀 Getting Started with MySQL Workbench

### Prerequisites

- Java JDK 11 or higher
- MySQL Server 8.0+

-**MySQL Workbench** (latest version)

- MySQL Connector/J (JDBC driver)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Database Setup with MySQL Workbench

1.**Open MySQL Workbench** and connect to your MySQL server

2.**Create Database**:

```sql

CREATEDATABASEIFNOTEXISTS employeeData;

USE employeeData;

```

3.**Run Scripts in Order**:

-`database/employeeData_MySQL_create.sql`

-`database/enhanced_schema.sql`

-`database/sample_data.sql`

4.**Set Up Users** (see `database/mysql-workbench-setup.md` for details)

### Generate Schema Diagram in MySQL Workbench

1.**Database** → **Reverse Engineer**

2. Select `employeeData` database
3. Choose all tables
4. Export as PNG for your deliverables

### Configuration

- Update `resources/database.properties` with MySQL Workbench connection settings
- Configure HR Admin and General Employee database users

## 🧪 How to Run Tests

**🎯 TL;DR - Quick Start:**

```bash
cd .../EMS
javac -cp "lib/*:src/main/java:src/test/java:resources" src/test/java/*.java
java -cp "lib/*:src/main/java:src/test/java:resources" FinalAllTasksTest
```

This runs the complete test suite for all 5 tasks and shows submission readiness.

### Prerequisites for Testing

- Java JDK 11 or higher installed
- MySQL server running with sample data loaded
- Environment variables configured in `.env` file
- All dependencies in `lib/` directory

### Quick Test (Recommended)

Run the complete system validation test:

```bash
# Navigate to project directory
cd .../EMS

# Compile all test files
javac -cp "lib/*:src/main/java:src/test/java:resources" src/test/java/*.java

# Run complete system test (All 5 Tasks)
java -cp "lib/*:src/main/java:src/test/java:resources" FinalAllTasksTest
```

### Individual Task Tests

#### Task 1: User Authentication System

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" AuthenticationSystemTest
```

Tests: Password hashing, validation, login/logout, role-based access

#### Task 2: Employee Search Functionality

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" EmployeeSearchTest
```

Tests: Search by ID, name, SSN, DOB, advanced search, role-based access

#### Task 3: Employee CRUD Operations

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" EmployeeCRUDTest
```

Tests: Create, Read, Update, Delete operations, data validation

#### Task 4: Salary Update by Percentage

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" SalaryUpdateTest
```

Tests: Bulk salary updates, percentage calculations, range validation

#### Task 5: Report Generation System

Task 5 is included in the `FinalAllTasksTest` - tests payroll model, report generation, and database integration.

### Sequential Test Runner (Tasks 1-4)

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" AllTasksTest
```

### Database Connectivity Test

```bash
java -cp "lib/*:src/main/java:src/test/java:resources" DatabaseConnectionTest
```

### Expected Test Results

When all tests pass, you should see:

- ✅ All 5 tasks marked as COMPLETE
- ✅ Database connectivity confirmed (5 employees found)
- ✅ Role-based security working
- ✅ "READY FOR NOVEMBER 11, 2025 SUBMISSION!" message

### Test Files Location

All test files are located in: `src/test/java/`

- `FinalAllTasksTest.java` - **Main comprehensive test**
- `AuthenticationSystemTest.java` - Task 1 test
- `EmployeeSearchTest.java` - Task 2 test
- `EmployeeCRUDTest.java` - Task 3 test
- `SalaryUpdateTest.java` - Task 4 test
- `README.md` - Test documentation

### Troubleshooting

If tests fail:

1. **Database Connection**: Ensure MySQL server is running and `.env` file is configured
2. **Sample Data**: Run `database/sample_data.sql` to populate test data
3. **Dependencies**: Verify MySQL JDBC driver is in `lib/` directory
4. **Compilation**: Check that all Java files compile without errors

### **Required Test Cases**

-**Test a)** Update employee data functionality

-**Test b)** Search for employee (admin user)

-**Test c)** Update salary for employees below threshold

## 🧪 Testing Strategy

### Test Categories

1.**Unit Tests** - Individual component testing

2.**Integration Tests** - Database connectivity and service layer

3.**Security Tests** - Authentication and authorization

4.**Performance Tests** - Large dataset handling

## 🔒 Security Features

-**Authentication**: Secure login system with password hashing

-**Authorization**: Role-based access control (HR Admin vs Employee)

-**Data Protection**: Encryption for sensitive data (SSN, etc.)

-**Input Validation**: SQL injection prevention and data sanitization

-**Session Management**: Secure session handling and timeout

## 📊 Database Schema

### Original Tables

-`employees` - Core employee information

-`payroll` - Payroll and deduction data

-`job_titles` - Job title definitions

-`employee_job_titles` - Employee-job title relationships

-`division` - Company divisions

-`employee_division` - Employee-division relationships

### Enhanced Tables (From Your Schema Diagram)

-`address` - Employee addresses with city/state normalization

-`city` - City lookup table

-`state` - State lookup table (50 states)

-`user_account` - User authentication

-`pay_statement` - Pay statement records

-`role` - User roles

## 🎥 Demo Video Requirements (Due 12/11/2025)

### Technical Specs

-**Duration**: 10-15 minutes (continuous, no editing)

-**Format**: MPEG-4, MPEG, WMV, AVI, MKV, WebM, 3GP, MOV

-**Content**: Live software demonstration with presenter visible

-**Slides**: Maximum 5 PowerPoint slides allowed

## 📞 Support & Resources

### Tools Being Used

-**UML Diagrams**: Lucidchart, Draw.io, or hand-drawn (neat)

-**Database**: **MySQL Workbench** for schema management and development

-**IDE**: IntelliJ IDEA or Eclipse

-**Version Control**: Git (recommended for team collaboration)
