package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.PayStatementDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.PayStatement;
import com.employeemgmt.models.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
    EmployeeService
    ---------------
    Middle layer between UI and DAOs.
    - handles permissions
    - returns friendly messages
    - now also exposes a unified search + pay history helper
*/
public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final PayStatementDAO payStatementDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.payStatementDAO = new PayStatementDAO();
    }

    // small wrapper so UI gets success flag + message + list
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

    // for pay history
    public static class PayHistoryResult {
        private final boolean success;
        private final String message;
        private final List<PayStatement> history;

        public PayHistoryResult(boolean success, String message, List<PayStatement> history) {
            this.success = success;
            this.message = message;
            this.history = (history != null) ? history : new ArrayList<>();
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<PayStatement> getHistory() { return history; }
        public int getCount() { return history.size(); }
    }

    // ========== BASIC SEARCH METHODS (used by consoles + FX) ==========

    // search by employee ID (with permission check)
    public SearchResult searchEmployeeById(int empid, User user) {
        try {
            var result = employeeDAO.findById(empid);

            if (result.isPresent()) {
                Employee emp = result.get();

                // only admins or the same employee can view this record
                if (!user.isAdmin() && user.getEmpid() != emp.getEmpid()) {
                    return new SearchResult(false,
                            "You do not have permission to view this employee.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            }

            return new SearchResult(false,
                    "No employee found with ID " + empid, null);

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error searching by ID: " + e.getMessage(), null);
        }
    }

    // search by first and/or last name
    public SearchResult searchByName(String first, String last, User user) {
        try {
            var list = employeeDAO.findByName(first, last);

            // non-admins should only see themselves
            if (!user.isAdmin()) {
                list.removeIf(e -> e.getEmpid() != user.getEmpid());
            }

            return new SearchResult(true, "Search complete.", list);

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error searching by name: " + e.getMessage(), null);
        }
    }

    // search by SSN (very restricted)
    public SearchResult searchBySSN(String ssn, User user) {
        try {
            var result = employeeDAO.findBySSN(ssn);

            if (result.isPresent()) {
                Employee emp = result.get();

                if (!user.isAdmin() && emp.getEmpid() != user.getEmpid()) {
                    return new SearchResult(false,
                            "You cannot view another employee's SSN.", null);
                }

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true, "Employee found.", list);
            }

            return new SearchResult(false,
                    "No employee found with that SSN.", null);

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error searching by SSN: " + e.getMessage(), null);
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
            return new SearchResult(false,
                    "Error loading employees: " + e.getMessage(), null);
        }
    }

    // ========== UNIFIED SEARCH FOR FX SCREEN ==========

    /*
       searchAllFields
       ---------------
       Takes a single query string and tries:
       - empid (int)
       - SSN
       - name (first, last, or "first last")
       - DOB (in-memory, based on Employee.dob if present)
    */
    public SearchResult searchAllFields(String query, User user) {
        String q = (query == null) ? "" : query.trim();

        if (q.isEmpty()) {
            return getAllEmployees(user);
        }

        List<Employee> combined = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();

        // helper to merge results without duplicates
        java.util.function.Consumer<SearchResult> addResult = r -> {
            if (r == null || !r.isSuccess()) return;
            for (Employee emp : r.getEmployees()) {
                if (!seen.contains(emp.getEmpid())) {
                    seen.add(emp.getEmpid());
                    combined.add(emp);
                }
            }
        };

        // try as numeric ID
        try {
            int asId = Integer.parseInt(q);
            addResult.accept(searchEmployeeById(asId, user));
        } catch (NumberFormatException ignored) {
            // not an integer, skip
        }

        // try as SSN-like query
        if (q.matches("\\d{3}-?\\d{2}-?\\d{4}")) {
            addResult.accept(searchBySSN(q, user));
        }

        // try as name "first last"
        String first = q;
        String last = q;
        if (q.contains(" ")) {
            String[] parts = q.split("\\s+", 2);
            first = parts[0];
            last = parts[1];
        }
        addResult.accept(searchByName(first, last, user));

        // try DOB (in-memory only, based on Employee.dob field)
        try {
            LocalDate dob = LocalDate.parse(q);
            var all = getAllEmployees(user);
            if (all.isSuccess()) {
                for (Employee emp : all.getEmployees()) {
                    if (emp.getDob() != null && emp.getDob().equals(dob)) {
                        if (!seen.contains(emp.getEmpid())) {
                            seen.add(emp.getEmpid());
                            combined.add(emp);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // not a date format we support, skip
        }

        if (combined.isEmpty()) {
            return new SearchResult(false,
                    "No employees matched: \"" + q + "\"", null);
        }

        return new SearchResult(true, "Search complete.", combined);
    }

    // ========== CRUD ==========

    // add a new employee (admins only)
    public SearchResult addEmployee(Employee emp, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false,
                    "Only HR Admins can add employees.", null);
        }

        try {
            boolean ok = employeeDAO.save(emp);
            if (ok) {
                ensurePayHistory(emp);

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true,
                        "Employee added successfully.", list);
            } else {
                return new SearchResult(false,
                        "Failed to save employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error adding employee: " + e.getMessage(), null);
        }
    }

    // update an existing employee (admins only)
    public SearchResult updateEmployee(Employee emp, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false,
                    "Only HR Admins can update employees.", null);
        }

        try {
            boolean ok = employeeDAO.update(emp);
            if (ok) {
                ensurePayHistory(emp);

                List<Employee> list = new ArrayList<>();
                list.add(emp);
                return new SearchResult(true,
                        "Employee updated.", list);
            } else {
                return new SearchResult(false,
                        "Failed to update employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error updating employee: " + e.getMessage(), null);
        }
    }

    // delete an employee (admins only)
    public SearchResult deleteEmployee(int empid, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false,
                    "Only HR Admins can delete employees.", null);
        }

        try {
            boolean ok = employeeDAO.delete(empid);
            if (ok) {
                return new SearchResult(true,
                        "Employee deleted.", null);
            } else {
                return new SearchResult(false,
                        "Could not delete employee.", null);
            }

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error deleting employee: " + e.getMessage(), null);
        }
    }

    // salary update for a given salary band (used by Salary Tools screen)
    public SearchResult updateSalaryRange(double percent, double min, double max, User user) {
        if (!user.isAdmin()) {
            return new SearchResult(false,
                    "Only HR Admins can modify salaries.", null);
        }

        try {
            int updated = employeeDAO.updateSalaryByRange(percent, min, max);
            return new SearchResult(true,
                    updated + " employees updated.", null);

        } catch (Exception e) {
            return new SearchResult(false,
                    "Error updating salaries: " + e.getMessage(), null);
        }
    }

    // ========== PAY HISTORY HELPERS ==========

    // ensure there is a pay history from hire date up to now
    public void ensurePayHistory(Employee emp) {
        if (emp == null) return;
        if (emp.getEmpid() <= 0) return;
        if (emp.getHireDate() == null) return;
        if (emp.getBaseSalary() == null) return;

        payStatementDAO.generateHistory(
                emp.getEmpid(),
                emp.getHireDate(),
                emp.getBaseSalary()
        );
    }

    // get pay history for either:
    // - the logged-in employee (regular user)
    // - any employee (admin)
    public PayHistoryResult getPayHistoryForEmployee(int empid, User user) {
        if (!user.isAdmin() && user.getEmpid() != empid) {
            return new PayHistoryResult(false,
                    "You are not allowed to view another employee's pay history.", null);
        }

        try {
            List<PayStatement> list = payStatementDAO.findByEmployee(empid);
            return new PayHistoryResult(true, "Loaded pay history.", list);

        } catch (Exception e) {
            return new PayHistoryResult(false,
                    "Error loading pay history: " + e.getMessage(), null);
        }
    }
}
