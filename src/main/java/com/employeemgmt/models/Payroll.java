package com.employeemgmt.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Payroll model class representing a pay statement
 * 
 * Maps to pay_statement table:
 * - id (primary key)
 * - empid (foreign key to Employee)
 * - pay_date
 * - gross (gross earnings)
 * - taxes (total taxes)
 * - net (net pay after taxes)
 */
public class Payroll {
    
    private long id;
    private int empid;
    private LocalDate payDate;
    private BigDecimal gross;
    private BigDecimal taxes;
    private BigDecimal net;
    
    // Default constructor
    public Payroll() {
    }
    
    // Full constructor
    public Payroll(long id, int empid, LocalDate payDate, BigDecimal gross, BigDecimal taxes, BigDecimal net) {
        this.id = id;
        this.empid = empid;
        this.payDate = payDate;
        this.gross = gross;
        this.taxes = taxes;
        this.net = net;
    }
    
    // Constructor without id (for new records)
    public Payroll(int empid, LocalDate payDate, BigDecimal gross, BigDecimal taxes) {
        this.empid = empid;
        this.payDate = payDate;
        this.gross = gross;
        this.taxes = taxes;
        this.net = calculateNetPay(gross, taxes);
    }
    
    // Calculate net pay from gross and taxes
    public static BigDecimal calculateNetPay(BigDecimal gross, BigDecimal taxes) {
        if (gross == null || taxes == null) {
            return BigDecimal.ZERO;
        }
        return gross.subtract(taxes).setScale(2, RoundingMode.HALF_UP);
    }
    
    // Recalculate net pay
    public void recalculateNetPay() {
        this.net = calculateNetPay(this.gross, this.taxes);
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getEmpid() {
        return empid;
    }
    
    public void setEmpid(int empid) {
        this.empid = empid;
    }
    
    public LocalDate getPayDate() {
        return payDate;
    }
    
    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }
    
    public BigDecimal getGross() {
        return gross;
    }
    
    public void setGross(BigDecimal gross) {
        this.gross = gross;
        recalculateNetPay();
    }
    
    public BigDecimal getTaxes() {
        return taxes;
    }
    
    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
        recalculateNetPay();
    }
    
    public BigDecimal getNet() {
        return net;
    }
    
    public void setNet(BigDecimal net) {
        this.net = net;
    }
    
    @Override
    public String toString() {
        return "Payroll{" +
                "id=" + id +
                ", empid=" + empid +
                ", payDate=" + payDate +
                ", gross=" + gross +
                ", taxes=" + taxes +
                ", net=" + net +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payroll payroll = (Payroll) o;
        return id == payroll.id && empid == payroll.empid;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, empid);
    }
}
