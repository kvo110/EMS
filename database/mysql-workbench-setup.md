# MySQL Workbench Setup Guide

## Database Setup Instructions for MySQL Workbench

### 1. Initial Database Creation

```sql
-- Create the main database
CREATE DATABASE IF NOT EXISTS employeeData;
USE employeeData;
```

### 2. User Account Setup

```sql
-- Create HR Admin user with full privileges
CREATE USER 'hr_admin'@'localhost' IDENTIFIED BY 'SecureHRPassword123!';
GRANT ALL PRIVILEGES ON employeeData.* TO 'hr_admin'@'localhost';

-- Create General Employee user with read-only access
CREATE USER 'general_employee'@'localhost' IDENTIFIED BY 'EmployeeReadOnly456!';
GRANT SELECT ON employeeData.* TO 'general_employee'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

### 3. Execute Database Scripts in Order

1. **Basic Schema**: Run `employeeData_MySQL_create.sql`
2. **Enhanced Schema**: Run `enhanced_schema.sql` 
3. **Sample Data**: Run `sample_data.sql`

### 4. Generate Database Schema Diagram in MySQL Workbench

#### Method 1: Forward Engineering Diagram
1. Go to **Database** → **Reverse Engineer**
2. Select your connection and database (`employeeData`)
3. Choose all tables to include
4. Click **Execute** to generate the diagram
5. Export as PNG: **File** → **Export** → **Export as PNG**

#### Method 2: EER Diagram
1. Go to **File** → **New Model**
2. **Database** → **Reverse Engineer**
3. Follow the wizard to import your existing schema
4. **Model** → **Create Diagram from Catalog Objects**
5. Export the diagram

### 5. MySQL Workbench Advantages for This Project

- **Visual Query Builder**: Easier to construct complex JOIN queries
- **Data Import/Export Wizard**: Bulk data operations
- **Performance Dashboard**: Monitor query performance
- **Schema Synchronization**: Compare and sync schema changes
- **Built-in Documentation**: Generate schema documentation

### 6. Connection Configuration

Update your `database.properties` file:

```properties
# MySQL Workbench Connection Settings
db.url=jdbc:mysql://localhost:3306/employeeData?useSSL=true&serverTimezone=UTC
db.driver=com.mysql.cj.jdbc.Driver

# HR Admin Connection
db.admin.username=hr_admin
db.admin.password=SecureHRPassword123!

# General Employee Connection  
db.employee.username=general_employee
db.employee.password=EmployeeReadOnly456!
```

### 7. Recommended MySQL Workbench Plugins

- **MySQL Utilities**: Additional database management tools
- **MySQL Shell**: Advanced scripting capabilities

### 8. Best Practices for Team Development

1. **Export Schema Regularly**: Keep schema files updated
2. **Use Version Control**: Track schema changes in Git
3. **Document Changes**: Comment all schema modifications
4. **Test Permissions**: Verify role-based access works correctly
5. **Backup Data**: Regular backups before major changes

### 9. Troubleshooting Common Issues

#### Connection Problems
```sql
-- Check user privileges
SHOW GRANTS FOR 'hr_admin'@'localhost';
SHOW GRANTS FOR 'general_employee'@'localhost';
```

#### Performance Issues
```sql
-- Add indexes for better performance
CREATE INDEX idx_employee_name ON employees(Fname, Lname);
CREATE INDEX idx_employee_ssn ON employees(SSN);
CREATE INDEX idx_payroll_date ON payroll(pay_date);
CREATE INDEX idx_payroll_empid ON payroll(empid);
```

### 10. Schema Diagram Export for Assignment

For your **Database Schema Diagram** deliverable:
1. Create the diagram in MySQL Workbench
2. Export as high-resolution PNG
3. Include all tables and relationships
4. Show primary/foreign key connections clearly
5. Save as `database-schema-workbench.png`
