package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import java.util.ArrayList;
import java.util.List;

/*
    EmployeeService

    This sits between the UI layer (JavaFX / console) and the DAO.
    - UI should talk to this class instead of hitting EmployeeDAO directly.
    - We do permission checks here using the User object.
    - We also wrap results into a SearchResult so the UI can show messages
      without needing to catch a bunch of exceptions.
*/
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    /*
        Small helper "result" type so we can return:
        - success flag
        - message (for the UI to show)
        - list of employees (if any)

        This makes the console UI and JavaFX code a lot cleaner.
    */
    public static class SearchResult {
        private final boolean success;
        private final String message;
        private final List<Employee> employees;

        public SearchResult(boolean success, String message, List<Employee> list) {
            this.success = success;
            this.message = message;
            // make sure we never return null lists to the UI
            this.employees = (list != null) ? list : new ArrayList<>();
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<Employee> getEmployees() { return employees; }
        public int getCount() { return employees.size(); }
    }

    // ---------------------------
    // BASIC SEARCH OPERATIONS
    // ---------------------------

    // search by ID, with permission check
    public SearchResult searchEmployeeById(int empid, User user) {
        try {
            var result = employeeDAO.findById(empid);

            if (result.isPresent()) {
                Employee emp = result.get();

                // regular employees can only see themselves, admins see everyone
                if (!user.isAdmin() && user.getEmpid() != emp.getEmpid()) {
                    return new SearchResult(false, "You do not have permission to view this employee.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            } else {
                return new SearchResult(false, "No employee found with ID " + empid, null);
            }

        } catch (Exception e) {
            // we swallow exception details here and push a friendly message up to UI
            return new SearchResult(false, "Error searching by ID: " + e.getMessage(), null);
        }
    }

    // search by first/last name
    public SearchResult searchByName(String first, String last, User user) {
        try {
            var list = employeeDAO.findByName(first, last);

            // non-admins should never see other people in the list
            if (!user.isAdmin()) {
                list.removeIf(e -> e.getEmpid() != user.getEmpid());
            }

            return new SearchResult(true, "Search complete.", list);

        } catch (Exception e) {
            return new SearchResult(false, "Error searching by name: " + e.getMessage(), null);
        }
    }

    // search by SSN (very restricted)
    public SearchResult searchBySSN(String ssn, User user) {
        try {
            var result = employeeDAO.findBySSN(ssn);

            if (result.isPresent()) {
                Employee emp = result.get();

                // only admins or the same employee can see SSN-related info
                if (!user.isAdmin() && emp.getEmpid() != user.getEmpid()) {
                    return new SearchResult(false, "You cannot view another employee's SSN.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            }

            return new SearchResult(false, "No employee found with SSN " + ssn, null);

        } catch (Exception e) {
            return new SearchResult(false, "Error searching by SSN: " + e.getMessage(), null);
        }
    }

    // load all employees in the system (or just "self" if not admin)
    public SearchResult getAllEmployees(User user) {
        try {
            var list = employeeDAO.findAll();

            if (!user.isAdmin()) {
                // normal employees only get themselves in the list
                list.removeIf(e -> e.getEmpid() != user.getEmpid());
            }

            return new SearchResult(true, "Employee list retrieved.", list);

        } catch (Exception e) {
            return new SearchResult(false, "Error retrieving employees: " + e.getMessage(), null);
        }
    }

    // ---------------------------
    // INSERT / UPDATE OPERATIONS
    // ---------------------------

    // create a new employee row (admin only)
    public SearchResult addEmployee(Employee emp, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can add employees.", null);
        }

        try {
            boolean ok = employeeDAO.save(emp);
            if (ok) {
                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee added successfully.", list);
            } else {
                return new SearchResult(false, "Failed to save employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false, "Error adding employee: " + e.getMessage(), null);
        }
    }

    // update an existing employee row (admin only)
    public SearchResult updateEmployee(Employee emp, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can update employees.", null);
        }

        try {
            boolean ok = employeeDAO.update(emp);

            if (ok) {
                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee updated.", list);
            } else {
                return new SearchResult(false, "Failed to update employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false, "Error updating employee: " + e.getMessage(), null);
        }
    }

    // delete an employee row (hard delete in this schema)
    public SearchResult deleteEmployee(int empid, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can delete employees.", null);
        }

        try {
            boolean ok = employeeDAO.delete(empid);

            if (ok) {
                return new SearchResult(true, "Employee deleted.", null);
            } else {
                return new SearchResult(false, "Could not delete employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false, "Error deleting employee: " + e.getMessage(), null);
        }
    }

    // ---------------------------
    // SALARY FEATURES
    // ---------------------------

    // batch raise for everyone in a certain salary bracket
    public SearchResult updateSalaryRange(double percentage, double min, double max, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can modify salaries.", null);
        }

        try {
            int updated = employeeDAO.updateSalaryByRange(percentage, min, max);
            return new SearchResult(true, updated + " employees updated.", null);

        } catch (Exception e) {
            return new SearchResult(false, "Error updating salaries: " + e.getMessage(), null);
        }
    }
}