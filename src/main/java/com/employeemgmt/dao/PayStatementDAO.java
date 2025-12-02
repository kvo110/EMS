package com.employeemgmt.dao;

import com.employeemgmt.models.PayStatement;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayStatementDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    // gets all pay statements for a single employee, newest first
    public List<PayStatement> findByEmployee(int empid) {
        List<PayStatement> list = new ArrayList<>();

        String sql = "SELECT pay_id, empid, pay_date, gross_pay, net_pay " +
                     "FROM pay_statements " +
                     "WHERE empid = ? " +
                     "ORDER BY pay_date DESC";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PayStatement p = new PayStatement();
                p.setPayId(rs.getInt("pay_id"));
                p.setEmpid(rs.getInt("empid"));
                Date d = rs.getDate("pay_date");
                if (d != null) {
                    p.setPayDate(d.toLocalDate());
                }
                p.setGrossPay(rs.getBigDecimal("gross_pay"));
                p.setNetPay(rs.getBigDecimal("net_pay"));
                list.add(p);
            }

        } catch (Exception ex) {
            System.out.println("Error findByEmployee: " + ex.getMessage());
        }

        return list;
    }

    // simple insert for a pay record
    public boolean insert(PayStatement p) {
        String sql = "INSERT INTO pay_statements (empid, pay_date, gross_pay, net_pay) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getEmpid());
            ps.setDate(2, Date.valueOf(p.getPayDate()));
            ps.setBigDecimal(3, p.getGrossPay());
            ps.setBigDecimal(4, p.getNetPay());

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            System.out.println("Error insert pay_statement: " + ex.getMessage());
        }

        return false;
    }

    // helper to see if a statement already exists for that month
    private boolean existsForMonth(int empid, LocalDate payDate) {
        String sql = "SELECT COUNT(*) FROM pay_statements WHERE empid = ? AND pay_date = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empid);
            ps.setDate(2, Date.valueOf(payDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception ex) {
            System.out.println("Error existsForMonth: " + ex.getMessage());
        }

        return false;
    }

    // auto-generate monthly pay history from hire date to now
    // skips months that already have a record
    public void generateHistory(int empid, LocalDate hireDate, BigDecimal monthlyGross) {
        if (empid <= 0 || hireDate == null || monthlyGross == null) {
            return;
        }

        LocalDate today = LocalDate.now().withDayOfMonth(1);
        LocalDate cursor = hireDate.withDayOfMonth(1);

        // basic "after tax" estimate so net_pay is not the same as gross
        BigDecimal netMultiplier = new BigDecimal("0.88");

        while (!cursor.isAfter(today)) {
            if (!existsForMonth(empid, cursor)) {
                PayStatement p = new PayStatement();
                p.setEmpid(empid);
                p.setPayDate(cursor);
                p.setGrossPay(monthlyGross);
                p.setNetPay(monthlyGross.multiply(netMultiplier));

                insert(p);
            }

            cursor = cursor.plusMonths(1);
        }
    }
}
