package com.employeemgmt.dao;

import com.employeemgmt.models.PayStatement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

// Handles all queries for the pay_statements table
public class PayStatementDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    private PayStatement mapRow(ResultSet rs) throws Exception {
        PayStatement p = new PayStatement();
        p.setPayId(rs.getInt("pay_id"));
        p.setEmpid(rs.getInt("empid"));
        Date d = rs.getDate("pay_date");
        p.setPayDate(d != null ? d.toLocalDate() : null);
        p.setGrossPay(rs.getBigDecimal("gross_pay"));
        p.setNetPay(rs.getBigDecimal("net_pay"));
        return p;
    }

    // Gets all pay statements for one employee, newest first
    public List<PayStatement> findByEmployeeOrdered(int empid) {
        List<PayStatement> list = new ArrayList<>();

        String sql = """
            SELECT pay_id, empid, pay_date, gross_pay, net_pay
            FROM pay_statements
            WHERE empid = ?
            ORDER BY pay_date DESC, pay_id DESC
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.out.println("Error findByEmployeeOrdered: " + e.getMessage());
        }

        return list;
    }
}
