#!/bin/bash
# Test script for Enhanced Console GUI

echo "Starting Enhanced Employee Management System GUI..."
echo "=============================================="
echo ""
echo "Test Credentials:"
echo "HR Admin: admin / admin123"
echo "Employee: employee / emp123"
echo ""
echo "Features to test:"
echo "- Professional login screen with validation"
echo "- Role-based menus (HR Admin vs Employee)"
echo "- Employee search and management"
echo "- Salary management interface"
echo "- Reports menu system"
echo "- Formatted data display"
echo ""
echo "Starting application..."
echo ""

cd .../EMS
java -cp "build/classes:lib/*" com.employeemgmt.Main
