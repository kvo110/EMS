package com.employeemgmt.services;

import com.employeemgmt.dao.ReportDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Small service so the UI gets clean strings + permission checks
public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public List<String> monthlyPayByJob(int year, int month, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            return List.of("Access denied: HR Admin only.");
        }

        var rows = reportDAO.totalNetPayByJob(year, month);
        List<String> lines = new ArrayList<>();

        if (rows.isEmpty()) {
            lines.add("No pay data found for that month.");
            return lines;
        }

        lines.add("Total net pay by job title for " + year + "-" + String.format("%02d", month));
        lines.add("------------------------------------------------");
        rows.forEach(r -> lines.add(r.getName() + " -> " + r.getTotalNet()));
        return lines;
    }

    public List<String> monthlyPayByDivision(int year, int month, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            return List.of("Access denied: HR Admin only.");
        }

        var rows = reportDAO.totalNetPayByDivision(year, month);
        List<String> lines = new ArrayList<>();

        if (rows.isEmpty()) {
            lines.add("No pay data found for that month.");
            return lines;
        }

        lines.add("Total net pay by division for " + year + "-" + String.format("%02d", month));
        lines.add("------------------------------------------------");
        rows.forEach(r -> lines.add(r.getName() + " -> " + r.getTotalNet()));
        return lines;
    }

    public List<String> employeesHiredBetween(LocalDate start, LocalDate end, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            return List.of("Access denied: HR Admin only.");
        }

        List<Employee> list = reportDAO.employeesHiredBetween(start, end);
        List<String> lines = new ArrayList<>();

        if (list.isEmpty()) {
            lines.add("No employees hired in that date range.");
            return lines;
        }

        lines.add("Employees hired between " + start + " and " + end);
        lines.add("------------------------------------------------");

        for (Employee e : list) {
            lines.add(
                    e.getEmpid() + " | " +
                    e.getFullName() + " | " +
                    e.getEmail() + " | " +
                    e.getHireDate()
            );
        }

        return lines;
    }
}
