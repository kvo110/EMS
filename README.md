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

│   └── test/java/                    # Test classes

├── uml-diagrams/                     # UML diagrams and documentation

│   ├── Danny's_Schema_Diagram.png    # ✅ COMPLETED - Database schema

│   ├── UML_Use_Case.png             # ✅ COMPLETED - Use case diagram  

│   └── UML_Sequence_Diagram.png     # ✅ COMPLETED - Employee search sequence

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

| **11/02/2025 11:59PM** | Individual | 150pts | UML Use Case + Sequence + DB Schema | ✅ **COMPLETED** |

| **11/11/2025 11:59PM** | Group | 160pts | Programming Tasks + Test Cases | ❌ **7 DAYS LEFT** |

| **11/16/2025 11:59PM** | Individual | 110pts | Sequence Diagrams (Salary + Add Employee) | ❌ |

| **12/08/2025 11:59PM** | Group | 150pts | Software Design Document (SDD) | ❌ |

| **12/11/2025 11:59PM** | Group | 250pts | Final Demo Video + Code | ❌ |

> **⚠️ NO LATE PENALTIES ALLOWED** for Group assignments (11/11, 12/08, 12/11)

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

## 📋 Next Priority: Group Assignment (Due 11/11/2025)

### **5 Programming Tasks to Implement**

1.**User Authentication System** (`UserDAO.java`, `AuthenticationService.java`)

2.**Employee Search Functionality** (`EmployeeDAO.java`, search methods)

3.**Employee CRUD Operations** (`EmployeeService.java`, update/create methods)

4.**Salary Update by Percentage** (batch salary update logic)

5.**Report Generation System** (`ReportService.java`, `PayrollDAO.java`)

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

### Important Notes

- ⚠️ **No late submissions** for group assignments
- 📁 **Avoid monolithic code** - use proper class organization
- 🔐 **Never hardcode passwords** - use configuration files
- 📝 **Document all code** with proper comments
- 🧪 **Test thoroughly** before final submission

## 📈 Grading Breakdown

| Component | Points | Type | Due Date | Status |

|-----------|--------|------|----------|--------|

| UML Diagrams (Initial) | 150 | Individual | 11/02/2025 | ✅ **COMPLETED** |

| Programming Tasks + Tests | 160 | Group | 11/11/2025 | 🚀 **IN PROGRESS** (Foundation Complete) |

| Sequence Diagrams (Final) | 110 | Individual | 11/16/2025 | ❌ |

| Software Design Document | 150 | Group | 12/08/2025 | ❌ |

| Final Demo + Code | 250 | Group | 12/11/2025 | ❌ |

| **Total** | **820** | | | **150/820** |

---

## 📋 **Development Progress Log**

### **Phase 1: Database Setup & Foundation** ✅ **COMPLETED by Danny (11/04/2025)**

#### **Database Infrastructure:**

- ✅ **Enhanced MySQL Schema** - Created comprehensive normalized database schema with 9 tables
  - Core tables: `employees`, `address`, `city`, `state`, `division`, `job_titles`
  - Security tables: `user_account`, `role`
  - Payroll tables: `payroll`, `pay_statement`
  - Proper foreign key relationships and indexes for performance
- ✅ **MySQL Workbench Integration** - Successfully connected project to MySQL Workbench
- ✅ **Sample Data Population** - Inserted test data for all tables (5 employees, addresses, divisions, etc.)
- ✅ **Database Connection** - Implemented JDBC connectivity with connection pooling

#### **Security & Configuration:**

- ✅ **Environment Variables** - Implemented `.env` file system for secure credential management
- ✅ **Git Security** - Added `.gitignore` to prevent credential exposure
- ✅ **Connection Testing** - Verified Java-MySQL connectivity with comprehensive test suite

#### **Core Model Development:**

- ✅ **Employee Model** - Complete implementation matching enhanced database schema
  - Field validation (SSN format, email validation, salary constraints)
  - Business logic methods (age calculation, years of service, formatted salary)
  - Proper encapsulation with getters/setters
  - Comparable interface for sorting
  - Object methods (equals, hashCode, toString)
- ✅ **Model Testing** - Comprehensive test suite validating all Employee model functionality

#### **Technical Achievements:**

- **Database Schema**: Normalized design supporting 55+ employees with full demographic data
- **Security**: Environment-based configuration with fallback to properties files
- **Code Quality**: Full validation, error handling, and comprehensive testing
- **Performance**: Strategic database indexes and connection pooling
- **Maintainability**: Clean separation of concerns and proper documentation

### **Next Phase: Programming Tasks Implementation** 🚀 **IN PROGRESS**

**Remaining Tasks for Group Assignment (Due 11/11/2025 - 6 days remaining):**

1. **Task 1**: User Authentication System - Completed by **DANNY NGUYEN ** 11/04/2025
2. **Task 2**: Employee Search Functionality - Completed by **DANNY NGUYEN ** 11/04/2025
3. **Task 3**: Employee CRUD Operations
4. **Task 4**: Salary Update by Percentage
5. **Task 5**: Report Generation System

---
