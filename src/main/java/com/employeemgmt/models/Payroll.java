package com.employeemgmt.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Payroll model class representing a complete pay statement
 * 
 * Maps to pay_statement table in the enhanced database schema
 * Handles all payroll calculations and pay statement generation
 */
public class Payroll {
    
    // Primary key and employee reference
    private int payrollId;
    private int empid;
    
    // Pay period information
    private LocalDate payDate;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    
    // Earnings
    private BigDecimal grossPay;
    private BigDecimal overtimePay;
    private BigDecimal bonusPay;
    
    // Tax deductions
    private BigDecimal federalTax;
    private BigDecimal federalMedicare;
    private BigDecimal federalSocialSecurity;
    private BigDecimal stateTax;
    
    // Other deductions
    private BigDecimal retirement401k;
    private BigDecimal healthCare;
    private BigDecimal dentalInsurance;
    private BigDecimal visionInsurance;
    
    // Calculated fields
    private BigDecimal totalEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    
    // Constructors
    public Payroll() {
        // Initialize BigDecimal fields to zero
        this.grossPay = BigDecimal.ZERO;
        this.overtimePay = BigDecimal.ZERO;
        this.bonusPay = BigDecimal.ZERO;
        this.federalTax = BigDecimal.ZERO;
        this.federalMedicare = BigDecimal.ZERO;
        this.federalSocialSecurity = BigDecimal.ZERO;
        this.stateTax = BigDecimal.ZERO;
        this.retirement401k = BigDecimal.ZERO;
        this.healthCare = BigDecimal.ZERO;
        this.dentalInsurance = BigDecimal.ZERO;
        this.visionInsurance = BigDecimal.ZERO;
    }
    
    public Payroll(int empid, LocalDate payDate, BigDecimal grossPay, BigDecimal totalDeductions) {
        this();
        this.empid = empid;
        this.payDate = payDate;
        this.grossPay = grossPay;
        this.totalDeductions = totalDeductions;
        calculateNetPay();
    }
    
    public Payroll(int empid, LocalDate payDate, BigDecimal grossPay, 
                   BigDecimal federalTax, BigDecimal stateTax, BigDecimal retirement401k) {
        this();
        this.empid = empid;
        this.payDate = payDate;
        this.grossPay = grossPay;
        this.federalTax = federalTax;
        this.stateTax = stateTax;
        this.retirement401k = retirement401k;
        calculateAllFields();
    }
    
    // Core getters and setters
    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }
    
    public int getEmpid() { return empid; }
    public void setEmpid(int empid) { this.empid = empid; }
    
    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }
    
    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public void setPayPeriodStart(LocalDate payPeriodStart) { this.payPeriodStart = payPeriodStart; }
    
    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public void setPayPeriodEnd(LocalDate payPeriodEnd) { this.payPeriodEnd = payPeriodEnd; }
    
    // Earnings getters and setters
    public BigDecimal getGrossPay() { return grossPay; }
    public void setGrossPay(BigDecimal grossPay) { 
        this.grossPay = grossPay != null ? grossPay : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { 
        this.overtimePay = overtimePay != null ? overtimePay : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getBonusPay() { return bonusPay; }
    public void setBonusPay(BigDecimal bonusPay) { 
        this.bonusPay = bonusPay != null ? bonusPay : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    // Tax deduction getters and setters
    public BigDecimal getFederalTax() { return federalTax; }
    public void setFederalTax(BigDecimal federalTax) { 
        this.federalTax = federalTax != null ? federalTax : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getFederalMedicare() { return federalMedicare; }
    public void setFederalMedicare(BigDecimal federalMedicare) { 
        this.federalMedicare = federalMedicare != null ? federalMedicare : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getFederalSocialSecurity() { return federalSocialSecurity; }
    public void setFederalSocialSecurity(BigDecimal federalSocialSecurity) { 
        this.federalSocialSecurity = federalSocialSecurity != null ? federalSocialSecurity : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getStateTax() { return stateTax; }
    public void setStateTax(BigDecimal stateTax) { 
        this.stateTax = stateTax != null ? stateTax : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    // Other deduction getters and setters
    public BigDecimal getRetirement401k() { return retirement401k; }
    public void setRetirement401k(BigDecimal retirement401k) { 
        this.retirement401k = retirement401k != null ? retirement401k : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getHealthCare() { return healthCare; }
    public void setHealthCare(BigDecimal healthCare) { 
        this.healthCare = healthCare != null ? healthCare : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getDentalInsurance() { return dentalInsurance; }
    public void setDentalInsurance(BigDecimal dentalInsurance) { 
        this.dentalInsurance = dentalInsurance != null ? dentalInsurance : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    public BigDecimal getVisionInsurance() { return visionInsurance; }
    public void setVisionInsurance(BigDecimal visionInsurance) { 
        this.visionInsurance = visionInsurance != null ? visionInsurance : BigDecimal.ZERO;
        calculateAllFields();
    }
    
    // Calculated field getters
    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public BigDecimal getNetPay() { return netPay; }
    
    // Convenience getters for compatibility
    public BigDecimal getGross() { return grossPay; }
    public void setGross(BigDecimal gross) { setGrossPay(gross); }
    
    public BigDecimal getTaxes() { return getTotalTaxDeductions(); }
    public void setTaxes(BigDecimal taxes) { 
        // Distribute taxes proportionally across tax types
        if (taxes != null && taxes.compareTo(BigDecimal.ZERO) > 0) {
            this.federalTax = taxes.multiply(new BigDecimal("0.70")); // 70% federal
            this.stateTax = taxes.multiply(new BigDecimal("0.20"));   // 20% state
            this.federalMedicare = taxes.multiply(new BigDecimal("0.05")); // 5% medicare
            this.federalSocialSecurity = taxes.multiply(new BigDecimal("0.05")); // 5% SS
            calculateAllFields();
        }
    }
    
    public BigDecimal getNet() { return netPay; }
    public void setNet(BigDecimal net) { 
        // This is calculated, but allow setting for compatibility
        this.netPay = net != null ? net : BigDecimal.ZERO;
    }
    
    // Business logic methods
    public void calculateAllFields() {
        calculateTotalEarnings();
        calculateTotalDeductions();
        calculateNetPay();
    }
    
    public void calculateTotalEarnings() {
        this.totalEarnings = grossPay.add(overtimePay).add(bonusPay);
    }
    
    public void calculateTotalDeductions() {
        this.totalDeductions = federalTax
            .add(federalMedicare)
            .add(federalSocialSecurity)
            .add(stateTax)
            .add(retirement401k)
            .add(healthCare)
            .add(dentalInsurance)
            .add(visionInsurance);
    }
    
    public void calculateNetPay() {
        if (totalEarnings == null) calculateTotalEarnings();
        if (totalDeductions == null) calculateTotalDeductions();
        this.netPay = totalEarnings.subtract(totalDeductions);
    }
    
    public BigDecimal getTotalTaxDeductions() {
        return federalTax.add(federalMedicare).add(federalSocialSecurity).add(stateTax);
    }
    
    public BigDecimal getTotalBenefitDeductions() {
        return retirement401k.add(healthCare).add(dentalInsurance).add(visionInsurance);
    }
    
    // Utility methods
    public String getFormattedGrossPay() {
        return String.format("$%,.2f", grossPay);
    }
    
    public String getFormattedNetPay() {
        return String.format("$%,.2f", netPay);
    }
    
    public String getFormattedTotalDeductions() {
        return String.format("$%,.2f", totalDeductions);
    }
    
    public double getTaxRate() {
        if (totalEarnings.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return getTotalTaxDeductions().divide(totalEarnings, 4, RoundingMode.HALF_UP)
               .multiply(new BigDecimal("100")).doubleValue();
    }
    
    public boolean isValid() {
        return empid > 0 && 
               payDate != null && 
               grossPay != null && 
               grossPay.compareTo(BigDecimal.ZERO) >= 0 &&
               netPay != null;
    }
    
    // Static utility method for net pay calculation
    public static BigDecimal calculateNetPay(BigDecimal gross, BigDecimal deductions) {
        if (gross == null) gross = BigDecimal.ZERO;
        if (deductions == null) deductions = BigDecimal.ZERO;
        return gross.subtract(deductions);
    }
    
    // Object methods
    @Override
    public String toString() {
        return String.format("Payroll{id=%d, empid=%d, payDate=%s, gross=%s, net=%s}", 
                payrollId, empid, payDate, getFormattedGrossPay(), getFormattedNetPay());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payroll)) return false;
        Payroll payroll = (Payroll) o;
        return payrollId == payroll.payrollId &&
               empid == payroll.empid &&
               Objects.equals(payDate, payroll.payDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(payrollId, empid, payDate);
    }
}
