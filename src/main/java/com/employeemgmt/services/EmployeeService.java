package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
    EmployeeService
    ---------------
    Middle layer between UI and DAO.
    Handles permissions and wraps messages for the screens.
*/
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    // Small wrapper so screens get status + message + list together
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

    // Search a single employee by ID (used by admin + employee dashboard)
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

    // Search by first and/or last name (still used in some spots if needed)
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

    // Search by SSN (strict)
    public SearchResult searchBySSN(String ssn, User user) {
        try {
            var result = employeeDAO.findBySSN(ssn);

            if (result.isPresent()) {
                Employee emp = result.get();

                // same idea: admin or self only
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

    // Unified search: name, empid, email, SSN, hire date string
    // (DOB is not in the DB schema right now, so we can't truly filter on DOB from SQL)
    public SearchResult searchAllFields(String query, User user) {
        String q = (query == null) ? "" : query.trim().toLowerCase();

        try {
            List<Employee> list = employeeDAO.findAll();

            if (!q.isEmpty()) {
                final String qq = q;

                list.removeIf(e -> {
                    boolean match = false;

                    // empid as text
                    if (String.valueOf(e.getEmpid()).contains(qq)) match = true;

                    // first / last name
                    if (e.getFirstName() != null &&
                            e.getFirstName().toLowerCase().contains(qq)) match = true;
                    if (e.getLastName() != null &&
                            e.getLastName().toLowerCase().contains(qq)) match = true;

                    // email
                    if (e.getEmail() != null &&
                            e.getEmail().toLowerCase().contains(qq)) match = true;

                    // SSN
                    if (e.getSsn() != null &&
                            e.getSsn().toLowerCase().contains(qq)) match = true;

                    // hire date (string match, e.g. "2024-10-01")
                    if (e.getHireDate() != null &&
                            e.getHireDate().toString().contains(qq)) match = true;

                    // if later you add DOB as a real DB column and hydrate it into the model,
                    // you can hook it in here the same way as hire date.

                    return !match;
                });
            }

            // general employees only see their own row
            if (!user.isAdmin()) {
                list.removeIf(e -> e.getEmpid() != user.getEmpid());
            }

            return new SearchResult(true, "Unified search complete.", list);

        } catch (Exception e) {
            return new SearchResult(false, "Error performing unified search: " + e.getMessage(), null);
        }
    }

    // All employees (admins see everyone, employees see themselves)
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

    // Employees hired in a date range (Admin-only report)
    public SearchResult getEmployeesHiredBetween(LocalDate start, LocalDate end, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false, "Only HR Admins can view this report.", null);
        }

        if (start == null || end == null) {
            return new SearchResult(false, "Please pick both start and end dates.", null);
        }

        if (end.isBefore(start)) {
            return new SearchResult(false, "End date cannot be before start date.", null);
        }

        try {
            List<Employee> all = employeeDAO.findAll();
            List<Employee> filtered = new ArrayList<>();

            for (Employee e : all) {
                if (e.getHireDate() == null) continue;
                LocalDate h = e.getHireDate();
                if ((h.isEqual(start) || h.isAfter(start)) &&
                        (h.isEqual(end) || h.isBefore(end))) {
                    filtered.add(e);
                }
            }

            return new SearchResult(true, "Hired-in-range report ready.", filtered);

        } catch (Exception e) {
            return new SearchResult(false, "Error building hire date report: " + e.getMessage(), null);
        }
    }

    // Add new employee (Admin only)
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

    // Update existing employee (Admin only)
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

    // Delete employee (Admin only)
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

    // Salary update for a band (used by Salary Tools)
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
}
