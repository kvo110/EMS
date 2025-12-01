package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import java.util.ArrayList;
import java.util.List;

/*
    EmployeeService
    ---------------
    This sits between the UI and the DAO.
    All permission checks and friendly messages live here.
*/
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    // small helper wrapper so the UI can get status + message + list
    public static class SearchResult {
        private final boolean success;
        private final String message;
        private final List<Employee> employees;

        public SearchResult(boolean success, String message, List<Employee> list) {
            this.success = success;
            this.message = message;
            this.employees = (list != null) ? list : new ArrayList<>();
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<Employee> getEmployees() { return employees; }
        public int getCount() { return employees.size(); }
    }

    // search by employee ID (with permission check)
    public SearchResult searchEmployeeById(int empid, User user) {
        try {
            var result = employeeDAO.findById(empid);

            if (result.isPresent()) {
                Employee emp = result.get();

                // only admins or the same employee can view this record
                if (!user.isAdmin() && user.getEmpid() != emp.getEmpid()) {
                    return new SearchResult(false, "You do not have permission to view this employee.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            }

            return new SearchResult(false, "No employee found with ID " + empid, null);

        } catch (Exception e) {
            return new SearchResult(false, "Error searching by ID: " + e.getMessage(), null);
        }
    }

    // search by first and/or last name (still here for other screens/tests)
    public SearchResult searchByName(String first, String last, User user) {
        try {
            var list = employeeDAO.findByName(first, last);

            // non-admins should only see themselves
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

                if (!user.isAdmin() && emp.getEmpid() != user.getEmpid()) {
                    return new SearchResult(false, "You cannot view another employee's SSN.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            }

            return new SearchResult(false, "No employee found with that SSN.", null);

        } catch (Exception e) {
            return new SearchResult(false, "Error searching by SSN: " + e.getMessage(), null);
        }
    }

    // get all employees (admins see everyone, others only see themselves)
    public SearchResult getAllEmployees(User user) {
        try {
            var list = employeeDAO.findAll();

            if (!user.isAdmin()) {
                list.removeIf(e -> e.getEmpid() != user.getEmpid());
            }

            return new SearchResult(true, "Employee list loaded.", list);

        } catch (Exception e) {
            return new SearchResult(false, "Error loading employees: " + e.getMessage(), null);
        }
    }

    // add a new employee (admins only)
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

    // update an existing employee (admins only)
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

    // delete an employee (admins only)
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

    // salary update for a given salary band (used by Salary Tools screen)
    public SearchResult updateSalaryRange(double percent, double min, double max, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can modify salaries.", null);
        }

        try {
            int updated = employeeDAO.updateSalaryByRange(percent, min, max);
            return new SearchResult(true, updated + " employees updated.", null);

        } catch (Exception e) {
            return new SearchResult(false, "Error updating salaries: " + e.getMessage(), null);
        }
    }

    // ---------------------------------------------------
    // Unified search for search bar (ID, name, DOB, SSN, etc.)
    // ---------------------------------------------------
    public SearchResult searchAllFields(String query, User user) {
        String q = query.trim().toLowerCase();

        // grab everyone first, then filter in memory
        var list = employeeDAO.findAll();

        // needs to be effectively final for the lambda
        final String qLower = q;

        list.removeIf(e -> {
            boolean match = false;

            // by empid
            if (String.valueOf(e.getEmpid()).contains(qLower)) match = true;

            // by first/last name
            if (e.getFirstName() != null &&
                    e.getFirstName().toLowerCase().contains(qLower)) match = true;
            if (e.getLastName() != null &&
                    e.getLastName().toLowerCase().contains(qLower)) match = true;

            // by SSN
            if (e.getSsn() != null &&
                    e.getSsn().toLowerCase().contains(qLower)) match = true;

            // by DOB (stored on the model, formatted as yyyy-MM-dd)
            if (e.getDob() != null &&
                    e.getDob().toString().contains(qLower)) match = true;

            // you can also match email if you want more hits
            if (e.getEmail() != null &&
                    e.getEmail().toLowerCase().contains(qLower)) match = true;

            return !match;
        });

        if (!user.isAdmin()) {
            list.removeIf(e -> e.getEmpid() != user.getEmpid());
        }

        return new SearchResult(true, "Unified search complete.", list);
    }
}
